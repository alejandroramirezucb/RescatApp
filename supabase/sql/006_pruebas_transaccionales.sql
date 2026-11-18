begin;
do $$
declare cliente uuid; propietario uuid; negocio integer; oferta integer;
begin
    select id into cliente from public.perfiles where rol = 'CLIENTE' order by creado_at limit 1;
    select id,propietario_id into negocio,propietario from public.negocios order by id limit 1;
    if cliente is null or propietario is null then
        raise exception 'Se requiere una cuenta CLIENTE y un negocio con propietario';
    end if;
    insert into public.ofertas(negocio_id,nombre,categoria,peso_kg,precio_normal,precio_rescate,
        cantidad_disponible,fecha_retiro,hora_retiro_desde,hora_retiro_hasta)
    values (negocio,'PRUEBA TRANSACCIONAL','PANADERIA',1,40,20,1,
            (now() at time zone 'America/La_Paz')::date+1,'18:00','20:00')
    returning id into oferta;
    perform set_config('rescatapp.test_cliente',cliente::text,true);
    perform set_config('rescatapp.test_propietario',propietario::text,true);
    perform set_config('rescatapp.test_oferta',oferta::text,true);
    perform set_config('request.jwt.claim.sub',cliente::text,true);
end;
$$;

set local role authenticated;
do $$
declare
    oferta integer := current_setting('rescatapp.test_oferta')::integer;
    cliente uuid := current_setting('rescatapp.test_cliente')::uuid;
    propietario uuid := current_setting('rescatapp.test_propietario')::uuid;
    solicitud uuid := gen_random_uuid();
    pedido public.pedidos%rowtype;
    repetido public.pedidos%rowtype;
    actualizado public.pedidos%rowtype;
begin
    select * into strict pedido from public.reservar_oferta(oferta,solicitud);
    select * into strict repetido from public.reservar_oferta(oferta,solicitud);
    if repetido.id <> pedido.id then raise exception 'FALLO: solicitud duplicada'; end if;
    if exists(select 1 from public.ofertas where id = oferta) then
        raise exception 'FALLO: el cliente ve una oferta agotada';
    end if;
    begin
        perform public.reservar_oferta(oferta,gen_random_uuid());
        raise exception using errcode = 'P0002',message = 'FALLO: sobreventa';
    exception when raise_exception then
        if sqlerrm <> 'Oferta no disponible' then raise; end if;
    end;
    select * into strict actualizado from public.cancelar_pedido(pedido.id);
    if actualizado.estado <> 'CANCELADO' then raise exception 'FALLO: cancelacion'; end if;
    perform public.cancelar_pedido(pedido.id);
    if (select cantidad_disponible from public.ofertas where id = oferta) is distinct from 1 then
        raise exception 'FALLO: cancelacion devolvio dos veces el stock';
    end if;
    perform set_config('request.jwt.claim.sub',gen_random_uuid()::text,true);
    if exists(select 1 from public.pedidos where id = pedido.id) then
        raise exception 'FALLO: pedido visible para un tercero';
    end if;
    begin
        perform public.cancelar_pedido(pedido.id);
        raise exception using errcode = 'P0002',message = 'FALLO: un tercero cancelo';
    exception when raise_exception then
        if sqlerrm <> 'No tienes acceso a este pedido' then raise; end if;
    end;
    perform set_config('request.jwt.claim.sub',propietario::text,true);
    perform public.cambiar_estado_oferta(oferta,'PAUSADA');
    perform set_config('request.jwt.claim.sub',cliente::text,true);
    if exists(select 1 from public.ofertas where id = oferta) then
        raise exception 'FALLO: oferta pausada visible';
    end if;
    perform set_config('request.jwt.claim.sub',propietario::text,true);
    perform public.cambiar_estado_oferta(oferta,'ACTIVA');
    perform set_config('request.jwt.claim.sub',cliente::text,true);
    select * into strict pedido from public.reservar_oferta(oferta,gen_random_uuid());
    begin
        update public.perfiles set rol = 'NEGOCIO' where id = cliente;
        raise exception using errcode = 'P0002',message = 'FALLO: cambio de rol no autorizado';
    exception when insufficient_privilege then null;
    end;
    perform set_config('request.jwt.claim.sub',propietario::text,true);
    select * into strict actualizado from public.avanzar_pedido(pedido.id,'RESERVADO');
    select * into strict repetido from public.avanzar_pedido(pedido.id,'RESERVADO');
    if actualizado.estado <> 'PREPARANDO' or repetido.estado <> 'PREPARANDO' then
        raise exception 'FALLO: avanzar dos veces la misma transicion';
    end if;
    perform public.avanzar_pedido(pedido.id,'PREPARANDO');
    begin
        perform public.avanzar_pedido(pedido.id,'LISTO','CODIGO-INCORRECTO');
        raise exception using errcode = 'P0002',message = 'FALLO: acepto codigo incorrecto';
    exception when raise_exception then
        if sqlerrm <> 'El codigo de rescate no coincide' then raise; end if;
    end;
    select * into strict actualizado from public.avanzar_pedido(pedido.id,'LISTO',pedido.codigo_rescate);
    if actualizado.estado <> 'RECOGIDO' then raise exception 'FALLO: retiro'; end if;
    begin
        perform public.cancelar_pedido(pedido.id);
        raise exception using errcode = 'P0002',message = 'FALLO: cancelo pedido recogido';
    exception when raise_exception then
        if sqlerrm <> 'Solo se cancela un pedido reservado' then raise; end if;
    end;
    perform public.eliminar_oferta(oferta);
    perform set_config('request.jwt.claim.sub',cliente::text,true);
    if exists(select 1 from public.ofertas where id = oferta) then
        raise exception 'FALLO: oferta eliminada visible';
    end if;
    if not exists(select 1 from public.pedidos where id = pedido.id and estado = 'RECOGIDO') then
        raise exception 'FALLO: eliminar oferta perdio el historial';
    end if;
    raise notice 'OK: reserva, reintento, stock, cancelacion, RLS, pausa, estados, codigo e historial.';
end;
$$;
reset role;
rollback;
select 'Pruebas completadas; datos de prueba revertidos.' as resultado;
