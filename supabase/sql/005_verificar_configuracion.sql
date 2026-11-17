-- Solo consultas. El SQL Editor usa postgres y omite RLS:
-- este archivo comprueba configuracion, no sustituye las pruebas de usuario.
select tablename,rowsecurity from pg_tables
where schemaname = 'public' and tablename in
('perfiles','negocios','ofertas','pedidos','notificaciones','catalogo_actualizacion')
order by tablename;

select tablename,policyname,roles,cmd from pg_policies
where schemaname = 'public' and tablename in
('perfiles','negocios','ofertas','pedidos','notificaciones','catalogo_actualizacion')
order by tablename,policyname;

select p.id,p.nombre,p.rol,n.id as negocio_id,n.nombre as negocio
from public.perfiles p left join public.negocios n on n.propietario_id = p.id
order by p.creado_at;

select count(*) as ofertas_mockapi from public.ofertas where origen = 'mockapi';
select id,legacy_id,nombre,estado,cantidad_disponible,fecha_retiro,imagen_url
from public.ofertas where origen = 'mockapi' order by id;

select id,public,file_size_limit,allowed_mime_types from storage.buckets where id = 'ofertas';
select schemaname,tablename from pg_publication_tables
where pubname = 'supabase_realtime' and schemaname = 'public'
order by tablename;

select
    has_table_privilege('anon','public.pedidos','SELECT') as anon_puede_leer_pedidos,
    has_table_privilege('authenticated','public.pedidos','INSERT') as cliente_puede_insertar_pedido_directo,
    has_column_privilege('authenticated','public.perfiles','rol','UPDATE') as usuario_puede_cambiar_rol,
    has_function_privilege('anon','public.reservar_oferta(integer,uuid)','EXECUTE') as anon_puede_reservar;
-- Las cuatro columnas anteriores deben ser false.
