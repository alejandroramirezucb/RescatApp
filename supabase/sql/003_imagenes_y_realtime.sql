begin;
insert into storage.buckets(id,name,public,file_size_limit,allowed_mime_types)
values ('ofertas','ofertas',true,5242880,array['image/jpeg','image/png','image/webp']);

-- Formato de ruta obligatorio: UUID_DEL_PROPIETARIO/nombre-unico.jpg
-- Las fotos son publicas; escribir exige sesion y ser propietario del negocio.
create policy rescatapp_imagenes_insert on storage.objects for insert to authenticated
with check (bucket_id = 'ofertas'
    and (storage.foldername(name))[1] = (select auth.uid()::text)
    and exists(select 1 from public.negocios n where n.propietario_id = (select auth.uid())));
create policy rescatapp_imagenes_select on storage.objects for select to authenticated
using (bucket_id = 'ofertas' and (storage.foldername(name))[1] = (select auth.uid()::text));
create policy rescatapp_imagenes_update on storage.objects for update to authenticated
using (bucket_id = 'ofertas' and (storage.foldername(name))[1] = (select auth.uid()::text))
with check (bucket_id = 'ofertas' and (storage.foldername(name))[1] = (select auth.uid()::text));
create policy rescatapp_imagenes_delete on storage.objects for delete to authenticated
using (bucket_id = 'ofertas' and (storage.foldername(name))[1] = (select auth.uid()::text));

-- No reemplaza la publicacion ni las tablas que ya pudieran estar suscritas.
do $$
declare tabla text;
begin
    if not exists(select 1 from pg_publication where pubname = 'supabase_realtime') then
        raise exception 'No existe supabase_realtime: revisa Database > Publications';
    end if;
    foreach tabla in array array['negocios','ofertas','pedidos','notificaciones','catalogo_actualizacion'] loop
        if not exists(select 1 from pg_publication_tables
                      where pubname = 'supabase_realtime' and schemaname = 'public' and tablename = tabla) then
            execute format('alter publication supabase_realtime add table public.%I',tabla);
        end if;
    end loop;
end;
$$;
commit;
