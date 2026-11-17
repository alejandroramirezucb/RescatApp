-- Ejecutar despues de 001. Todas las operaciones validan al usuario del JWT.
begin;

create function public.reservar_oferta(p_oferta_id integer,p_solicitud_id uuid)
returns setof public.pedidos language plpgsql security definer set search_path = '' as $$
declare
    usuario uuid := auth.uid();
    oferta public.ofertas%rowtype;
    negocio public.negocios%rowtype;
    pedido public.pedidos%rowtype;
    nombre_cliente text;
begin
    if usuario is null or p_solicitud_id is null then
        raise exception 'Se necesita sesion y solicitud_id';
    end if;
    select nombre into nombre_cliente from public.perfiles where id = usuario and rol = 'CLIENTE';
    if not found then raise exception 'Solo un cliente puede reservar'; end if;
    -- Serializa reintentos de una misma solicitud, incluso simultaneos.
    perform pg_catalog.pg_advisory_xact_lock(
        pg_catalog.hashtextextended(usuario::text || ':' || p_solicitud_id::text,0));
    select * into pedido from public.pedidos
    where cliente_id = usuario and solicitud_id = p_solicitud_id;
    if found then
        if pedido.oferta_id <> p_oferta_id then raise exception 'Solicitud reutilizada para otra oferta'; end if;
        return next pedido;
        return;
    end if;
    select * into oferta from public.ofertas where id = p_oferta_id for update;
    if not found then raise exception 'Oferta no encontrada'; end if;
    select * into negocio from public.negocios where id = oferta.negocio_id;
    if not negocio.activo or oferta.estado <> 'ACTIVA' or oferta.cantidad_disponible <= 0 then
        raise exception 'Oferta no disponible';
    end if;
    if ((oferta.fecha_retiro + oferta.hora_retiro_hasta) at time zone 'America/La_Paz') <= now() then
        raise exception 'El horario de retiro ya vencio';
    end if;
    update public.ofertas set cantidad_disponible = cantidad_disponible - 1 where id = oferta.id;
    insert into public.pedidos(
        cliente_id,cliente_nombre,negocio_id,oferta_id,solicitud_id,nombre_oferta,comercio,
        categoria,precio_pagado,ahorro,peso_kg,fecha_retiro,hora_retiro_desde,hora_retiro_hasta,imagen_url)
    values (
        usuario,nombre_cliente,negocio.id,oferta.id,p_solicitud_id,oferta.nombre,negocio.nombre,
        oferta.categoria,oferta.precio_rescate,oferta.precio_normal-oferta.precio_rescate,
        oferta.peso_kg,oferta.fecha_retiro,oferta.hora_retiro_desde,oferta.hora_retiro_hasta,oferta.imagen_url)
    returning * into pedido;
    insert into public.notificaciones(destinatario_id,rol_destino,titulo,mensaje,tipo,pedido_id,oferta_id)
    values
        (usuario,'CLIENTE','Reserva confirmada',oferta.nombre,'PEDIDO_RESERVADO',pedido.id,oferta.id),
        (negocio.propietario_id,'NEGOCIO','Nuevo pedido',oferta.nombre,'NUEVO_PEDIDO_VENDEDOR',pedido.id,oferta.id);
    if oferta.cantidad_disponible = 1 then
        insert into public.notificaciones(destinatario_id,rol_destino,titulo,mensaje,tipo,oferta_id)
        values (negocio.propietario_id,'NEGOCIO','Oferta agotada',oferta.nombre,'OFERTA_AGOTADA',oferta.id);
    end if;
    return next pedido;
end;
$$;

create function public.cancelar_pedido(p_pedido_id integer)
returns setof public.pedidos language plpgsql security definer set search_path = '' as $$
declare
    usuario uuid := auth.uid();
    pedido public.pedidos%rowtype;
    propietario uuid;
begin
    if usuario is null then raise exception 'Se necesita sesion'; end if;
    select * into pedido from public.pedidos where id = p_pedido_id for update;
    if not found then raise exception 'Pedido no encontrado'; end if;
    select propietario_id into propietario from public.negocios where id = pedido.negocio_id;
    if usuario <> pedido.cliente_id and usuario <> propietario then
        raise exception 'No tienes acceso a este pedido';
    end if;
    if pedido.estado = 'CANCELADO' then return next pedido; return; end if;
    if pedido.estado <> 'RESERVADO' then raise exception 'Solo se cancela un pedido reservado'; end if;
    update public.ofertas set cantidad_disponible = cantidad_disponible + 1 where id = pedido.oferta_id;
    update public.pedidos set estado = 'CANCELADO' where id = pedido.id returning * into pedido;
    insert into public.notificaciones(destinatario_id,rol_destino,titulo,mensaje,tipo,pedido_id,oferta_id)
    values
        (pedido.cliente_id,'CLIENTE','Pedido cancelado',pedido.nombre_oferta,'PEDIDO_CANCELADO',pedido.id,pedido.oferta_id),
        (propietario,'NEGOCIO','Pedido cancelado',pedido.nombre_oferta,'PEDIDO_CANCELADO',pedido.id,pedido.oferta_id);
    return next pedido;
end;
$$;

create function public.avanzar_pedido(
    p_pedido_id integer,p_estado_esperado text,p_codigo_rescate text default null)
returns setof public.pedidos language plpgsql security definer set search_path = '' as $$
declare
    pedido public.pedidos%rowtype;
    siguiente text;
    tipo_aviso text;
begin
    if auth.uid() is null then raise exception 'Se necesita sesion'; end if;
    select * into pedido from public.pedidos where id = p_pedido_id for update;
    if not found then raise exception 'Pedido no encontrado'; end if;
    if not exists(select 1 from public.negocios n where n.id = pedido.negocio_id
                  and n.propietario_id = auth.uid()) then
        raise exception 'Solo el negocio propietario puede avanzar el pedido';
    end if;
    siguiente := case p_estado_esperado when 'RESERVADO' then 'PREPARANDO'
                 when 'PREPARANDO' then 'LISTO' when 'LISTO' then 'RECOGIDO' else null end;
    if siguiente is null then raise exception 'Transicion invalida'; end if;
    if p_estado_esperado = 'LISTO' and btrim(coalesce(p_codigo_rescate,'')) <> pedido.codigo_rescate then
        raise exception 'El codigo de rescate no coincide';
    end if;
    -- Reintentar la MISMA transicion no avanza un segundo paso.
    if pedido.estado = siguiente then return next pedido; return; end if;
    if pedido.estado <> p_estado_esperado then raise exception 'El estado del pedido cambio; actualiza la pantalla'; end if;
    update public.pedidos set estado = siguiente where id = pedido.id returning * into pedido;
    tipo_aviso := case siguiente when 'PREPARANDO' then 'PEDIDO_EN_PREPARACION'
                  when 'LISTO' then 'PEDIDO_LISTO' else 'PEDIDO_RECOGIDO' end;
    insert into public.notificaciones(destinatario_id,rol_destino,titulo,mensaje,tipo,pedido_id,oferta_id)
    values (pedido.cliente_id,'CLIENTE','Pedido: ' || siguiente,pedido.nombre_oferta,
            tipo_aviso,pedido.id,pedido.oferta_id);
    return next pedido;
end;
$$;

create function public.cambiar_estado_oferta(p_oferta_id integer,p_estado text)
returns setof public.ofertas language plpgsql security definer set search_path = '' as $$
declare oferta public.ofertas%rowtype;
begin
    if auth.uid() is null then raise exception 'Se necesita sesion'; end if;
    if p_estado not in ('ACTIVA','PAUSADA') or p_estado is null then
        raise exception 'Usa ACTIVA o PAUSADA';
    end if;
    select * into oferta from public.ofertas where id = p_oferta_id for update;
    if not found then raise exception 'Oferta no encontrada'; end if;
    if not exists(select 1 from public.negocios n where n.id = oferta.negocio_id
                  and n.propietario_id = auth.uid()) then
        raise exception 'La oferta pertenece a otro negocio';
    end if;
    if oferta.estado = 'ELIMINADA' then raise exception 'La oferta fue eliminada'; end if;
    update public.ofertas set estado = p_estado where id = oferta.id returning * into oferta;
    return next oferta;
end;
$$;

create function public.eliminar_oferta(p_oferta_id integer)
returns setof public.ofertas language plpgsql security definer set search_path = '' as $$
declare oferta public.ofertas%rowtype;
begin
    if auth.uid() is null then raise exception 'Se necesita sesion'; end if;
    select * into oferta from public.ofertas where id = p_oferta_id for update;
    if not found then raise exception 'Oferta no encontrada'; end if;
    if not exists(select 1 from public.negocios n where n.id = oferta.negocio_id
                  and n.propietario_id = auth.uid()) then
        raise exception 'La oferta pertenece a otro negocio';
    end if;
    -- Borrado logico: conserva relaciones e historicos; la app la retira de las listas.
    update public.ofertas set estado = 'ELIMINADA' where id = oferta.id returning * into oferta;
    return next oferta;
end;
$$;

revoke all on function public.reservar_oferta(integer,uuid) from public,anon,authenticated;
revoke all on function public.cancelar_pedido(integer) from public,anon,authenticated;
revoke all on function public.avanzar_pedido(integer,text,text) from public,anon,authenticated;
revoke all on function public.cambiar_estado_oferta(integer,text) from public,anon,authenticated;
revoke all on function public.eliminar_oferta(integer) from public,anon,authenticated;
grant execute on function public.reservar_oferta(integer,uuid) to authenticated;
grant execute on function public.cancelar_pedido(integer) to authenticated;
grant execute on function public.avanzar_pedido(integer,text,text) to authenticated;
grant execute on function public.cambiar_estado_oferta(integer,text) to authenticated;
grant execute on function public.eliminar_oferta(integer) to authenticated;

commit;
