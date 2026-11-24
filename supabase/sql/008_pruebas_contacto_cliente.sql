begin;

do $$
declare cliente uuid; propietario uuid; negocio integer; oferta integer;
begin
    select id into cliente from public.perfiles where rol = 'CLIENTE' order by creado_at limit 1;
    select id,propietario_id into negocio,propietario from public.negocios order by id limit 1;
    if cliente is null or propietario is null then
        raise exception 'Se requiere una cuenta CLIENTE y un negocio con propietario';
    end if;
    update public.perfiles set telefono = '+591 71234567' where id = cliente;
    insert into public.ofertas(negocio_id,nombre,categoria,peso_kg,precio_normal,precio_rescate,
        cantidad_disponible,fecha_retiro,hora_retiro_desde,hora_retiro_hasta)
    values (negocio,'PRUEBA CONTACTO','PANADERIA',1,40,20,1,
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
    cliente uuid := current_setting('rescatapp.test_cliente')::uuid;
    propietario uuid := current_setting('rescatapp.test_propietario')::uuid;
    oferta integer := current_setting('rescatapp.test_oferta')::integer;
    pedido public.pedidos%rowtype;
    nombre text;
begin
    select perfiles.nombre into nombre from public.perfiles where id = cliente;
    select * into strict pedido from public.reservar_oferta(oferta,gen_random_uuid());
    if pedido.cliente_nombre is distinct from nombre
       or pedido.cliente_telefono is distinct from '+591 71234567' then
        raise exception 'FALLO: la reserva no devuelve nombre y telefono';
    end if;
    perform set_config('request.jwt.claim.sub',propietario::text,true);
    if not exists(select 1 from public.pedidos p where p.id = pedido.id
                  and p.cliente_nombre = nombre and p.cliente_telefono = '+591 71234567') then
        raise exception 'FALLO: el vendedor no puede consultar el comprador';
    end if;
    if exists(select 1 from public.perfiles where id = cliente) then
        raise exception 'FALLO: se habilito acceso al perfil ajeno';
    end if;
    perform set_config('request.jwt.claim.sub',gen_random_uuid()::text,true);
    if exists(select 1 from public.pedidos p where p.id = pedido.id) then
        raise exception 'FALLO: contacto visible para un tercero';
    end if;
    perform set_config('request.jwt.claim.sub',cliente::text,true);
    perform public.cancelar_pedido(pedido.id);
    update public.perfiles set telefono = '' where id = cliente;
    select * into strict pedido from public.reservar_oferta(oferta,gen_random_uuid());
    if pedido.cliente_telefono is not null then
        raise exception 'FALLO: un telefono vacio no se representa como ausente';
    end if;
    raise notice 'OK: contacto de reserva, acceso del vendedor, RLS y telefono ausente.';
end;
$$;
reset role;
rollback;
select 'Pruebas de contacto completadas; datos de prueba revertidos.' as resultado;
