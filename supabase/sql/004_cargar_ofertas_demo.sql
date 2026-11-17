-- Solo proyecto nuevo. Primero crear en Authentication > Users las cuentas demo.
-- REEMPLAZAR estos dos correos con las cuentas que TU creaste; no contienen claves.
-- No vuelve a insertar ni restablece stock de ofertas ya importadas.
begin;
do $seed$
declare
    correo_negocio text := 'panaderialacentral@gmail.com';
    correo_cliente text := 'arianylopez341@gmail.com';
    vendedor uuid;
    cliente uuid;
    negocio integer;
begin
    select id into vendedor from auth.users where lower(email) = lower(correo_negocio);
    select id into cliente from auth.users where lower(email) = lower(correo_cliente);
    if vendedor is null or cliente is null or vendedor = cliente then
        raise exception 'Crea dos usuarios distintos en Authentication y reemplaza los correos de este archivo';
    end if;
    if not exists(select 1 from public.perfiles where id = vendedor)
       or not exists(select 1 from public.perfiles where id = cliente) then
        raise exception 'Faltan perfiles: crea las cuentas DESPUES de ejecutar 001';
    end if;
    if exists(select 1 from public.negocios where propietario_id = cliente) then
        raise exception 'La cuenta cliente ya pertenece a un negocio; usa una cuenta de cliente distinta';
    end if;
    update public.perfiles set rol = 'NEGOCIO',nombre = 'Responsable La Central' where id = vendedor;
    update public.perfiles set nombre = 'Cliente de demostración' where id = cliente;
    insert into public.negocios(propietario_id,nombre)
    values (vendedor,'Panadería La Central')
    on conflict (propietario_id) do nothing;
    select id into negocio from public.negocios where propietario_id = vendedor;
    if (select nombre from public.negocios where id = negocio) <> 'Panadería La Central' then
        raise exception 'El propietario ya tiene otro negocio; revisa la cuenta antes de importar';
    end if;
    insert into public.ofertas(
        negocio_id,origen,legacy_id,nombre,categoria,descripcion,peso_kg,
        precio_normal,precio_rescate,cantidad_disponible,fecha_retiro,hora_retiro_desde,hora_retiro_hasta)
    select negocio,'mockapi',x.id,x.nombre,x.categoria,x.descripcion,x."pesoKg",
           x."precioNormal",x."precioRescate",x."cantidadDisponible",
           (now() at time zone 'America/La_Paz')::date + 1,
           x."horaRetiroDesde"::time,x."horaRetiroHasta"::time
    from jsonb_to_recordset($datos$[{"id":"1","nombre":"Pack Sorpresa","comercio":"Panadería La Central","categoria":"PANADERIA","descripcion":"Surtido de panes y facturas del día","pesoKg":1,"precioNormal":45,"precioRescate":25,"cantidadDisponible":5,"horaRetiroDesde":"18:00","horaRetiroHasta":"20:00"},{"id":"2","nombre":"Pack Medialunas","comercio":"Panadería La Central","categoria":"PANADERIA","descripcion":"Medialunas de manteca del día","pesoKg":0.5,"precioNormal":28,"precioRescate":14,"cantidadDisponible":4,"horaRetiroDesde":"17:00","horaRetiroHasta":"19:00"},{"id":"3","nombre":"Pack Salteñas","comercio":"Panadería La Central","categoria":"COMIDA","descripcion":"Salteñas de carne y pollo","pesoKg":0.8,"precioNormal":42,"precioRescate":21,"cantidadDisponible":3,"horaRetiroDesde":"12:00","horaRetiroHasta":"14:00"},{"id":"4","nombre":"Torta del Día","comercio":"Panadería La Central","categoria":"POSTRES","descripcion":"Porciones de torta de chocolate","pesoKg":0.7,"precioNormal":50,"precioRescate":26,"cantidadDisponible":2,"horaRetiroDesde":"18:00","horaRetiroHasta":"20:00"},{"nombre":"Pack Croissants","comercio":"Panadería La Central","categoria":"PANADERIA","descripcion":"Croissants de mantequilla del día","pesoKg":0.6,"precioNormal":30,"precioRescate":20,"cantidadDisponible":2,"horaRetiroDesde":"17:00","horaRetiroHasta":"19:00","id":"6"}]$datos$::jsonb) as x(
        id text,nombre text,comercio text,categoria text,descripcion text,
        "pesoKg" numeric,"precioNormal" numeric,"precioRescate" numeric,
        "cantidadDisponible" integer,"horaRetiroDesde" text,"horaRetiroHasta" text)
    on conflict (origen,legacy_id) do nothing;
end;
$seed$;
commit;

select id,nombre,legacy_id,estado,fecha_retiro,cantidad_disponible
from public.ofertas where origen = 'mockapi' order by id;
