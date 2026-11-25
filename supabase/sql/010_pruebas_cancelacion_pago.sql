-- Ejecutar después de 009. No conserva pedidos, stock ni notificaciones de prueba.
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
    values (negocio,'PRUEBA CANCELACION Y PAGO','PANADERIA',1,40,20,1,
        (now() at time zone 'America/La_Paz')::date+1,'18:00','20:00') returning id into oferta;
    perform set_config('rescatapp.test_cliente',cliente::text,true);
    perform set_config('rescatapp.test_propietario',propietario::text,true);
    perform set_config('rescatapp.test_oferta',oferta::text,true);
end;
$$;
set local role authenticated;
do $$
declare
    cliente uuid := current_setting('rescatapp.test_cliente')::uuid;
    propietario uuid := current_setting('rescatapp.test_propietario')::uuid;
    oferta integer := current_setting('rescatapp.test_oferta')::integer;
    pedido public.pedidos%rowtype;
    resultado public.pedidos%rowtype;
    estado_actual text;
begin
    perform set_config('request.jwt.claim.sub',cliente::text,true);
    select * into strict pedido from public.reservar_oferta(oferta,gen_random_uuid());
    if pedido.estado_pago_simulado <> 'APROBADO' then raise exception 'FALLO: pago inicial'; end if;
    -- Un tercero y una sesión ausente no pueden anular el pedido ni el pago.
    perform set_config('request.jwt.claim.sub',gen_random_uuid()::text,true);
    begin
        perform public.cancelar_pedido(pedido.id);
        raise exception using errcode = 'P0002',message = 'FALLO: tercero cancelo';
    exception when raise_exception then
        if sqlerrm <> 'No tienes acceso a este pedido' then raise; end if;
    end;
    perform set_config('request.jwt.claim.sub','',true);
    begin
        perform public.cancelar_pedido(pedido.id);
        raise exception using errcode = 'P0002',message = 'FALLO: cancelo sin sesion';
    exception when raise_exception then
        if sqlerrm <> 'Se necesita sesion' then raise; end if;
    end;
    perform set_config('request.jwt.claim.sub',cliente::text,true);
    select * into strict resultado from public.cancelar_pedido(pedido.id);
    if resultado.estado <> 'CANCELADO' or resultado.estado_pago_simulado <> 'ANULADO' then
        raise exception 'FALLO: cancelacion y pago no atomicos';
    end if;
    perform public.cancelar_pedido(pedido.id);
    if not exists(select 1 from public.ofertas where id = oferta and cantidad_disponible = 1) then
        raise exception 'FALLO: stock duplicado';
    end if;
    if (select count(*) from public.notificaciones where pedido_id = pedido.id
        and tipo = 'PEDIDO_CANCELADO') <> 1 then
        raise exception 'FALLO: notificacion del cliente duplicada';
    end if;
    -- Si ganó la cancelación, el comercio no puede aceptar el pedido después.
    perform set_config('request.jwt.claim.sub',propietario::text,true);
    begin
        perform public.avanzar_pedido(pedido.id,'RESERVADO');
        raise exception using errcode = 'P0002',message = 'FALLO: acepto un pedido cancelado';
    exception when raise_exception then
        if sqlerrm <> 'El estado del pedido cambio; actualiza la pantalla' then raise; end if;
    end;
    -- Se mantiene la cancelación del negocio antes de aceptar.
    perform set_config('request.jwt.claim.sub',cliente::text,true);
    select * into strict pedido from public.reservar_oferta(oferta,gen_random_uuid());
    perform set_config('request.jwt.claim.sub',propietario::text,true);
    select * into strict resultado from public.cancelar_pedido(pedido.id);
    if resultado.estado_pago_simulado <> 'ANULADO' then raise exception 'FALLO: cancelacion del negocio'; end if;
    -- Si ganó la aceptación, PREPARANDO y LISTO ya no admiten cancelaciones.
    perform set_config('request.jwt.claim.sub',cliente::text,true);
    select * into strict pedido from public.reservar_oferta(oferta,gen_random_uuid());
    perform set_config('request.jwt.claim.sub',propietario::text,true);
    perform public.avanzar_pedido(pedido.id,'RESERVADO');
    foreach estado_actual in array array['PREPARANDO','LISTO','RECOGIDO'] loop
        perform set_config('request.jwt.claim.sub',cliente::text,true);
        begin
            perform public.cancelar_pedido(pedido.id);
            raise exception using errcode = 'P0002',message = 'FALLO: cancelo despues de aceptar';
        exception when raise_exception then
            if sqlerrm <> 'Solo se cancela un pedido reservado' then raise; end if;
        end;
        select * into strict resultado from public.pedidos where id = pedido.id;
        if resultado.estado <> estado_actual or resultado.estado_pago_simulado <> 'APROBADO' then
            raise exception 'FALLO: rechazo modifico el pedido o su pago';
        end if;
        perform set_config('request.jwt.claim.sub',propietario::text,true);
        if not exists(select 1 from public.ofertas where id = oferta and cantidad_disponible = 0) then
            raise exception 'FALLO: rechazo devolvio stock';
        end if;
        if estado_actual <> 'RECOGIDO' then
            perform public.avanzar_pedido(pedido.id,estado_actual,pedido.codigo_rescate);
        end if;
    end loop;
end;
$$;
reset role;
rollback;
select 'Cancelacion, pago simulado, reintentos y permisos verificados; datos revertidos.' as resultado;
