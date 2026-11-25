begin;

create function public.rescatapp_al_crear_usuario()
returns trigger language plpgsql security definer set search_path = '' as $$
declare
    tipo text := case when new.raw_user_meta_data->>'rol' = 'NEGOCIO'
                      then 'NEGOCIO' else 'CLIENTE' end;
    nombre_usuario text := coalesce(nullif(btrim(new.raw_user_meta_data->>'nombre'), ''),
                                   split_part(new.email, '@', 1), 'Usuario');
    nombre_negocio text := nullif(btrim(new.raw_user_meta_data->>'nombreNegocio'), '');
begin
    insert into public.perfiles(id, nombre, rol, telefono, direccion)
    values (new.id, left(nombre_usuario,120), tipo,
            coalesce(new.raw_user_meta_data->>'telefono',''),
            coalesce(new.raw_user_meta_data->>'direccion',''));
    if tipo = 'NEGOCIO' then
        if nombre_negocio is null then
            raise exception 'El nombre del negocio es obligatorio';
        end if;
        insert into public.negocios(propietario_id,nombre,direccion)
        values (new.id,left(nombre_negocio,160),
                coalesce(new.raw_user_meta_data->>'direccion',''));
    end if;
    return new;
end;
$$;
revoke all on function public.rescatapp_al_crear_usuario() from public, anon, authenticated;
create trigger rescatapp_usuario_creado after insert on auth.users
for each row execute function public.rescatapp_al_crear_usuario();

alter table public.perfiles enable row level security;
alter table public.negocios enable row level security;
alter table public.ofertas enable row level security;
alter table public.pedidos enable row level security;
alter table public.notificaciones enable row level security;
alter table public.catalogo_actualizacion enable row level security;
revoke all on public.catalogo_actualizacion from public,anon,authenticated;
grant select on public.catalogo_actualizacion to anon,authenticated;
create policy catalogo_revision_publica on public.catalogo_actualizacion for select to anon,authenticated
using (true);

revoke all on public.perfiles,public.negocios,public.ofertas,public.pedidos,public.notificaciones
from public,anon,authenticated;
grant select on public.perfiles to authenticated;
grant update(nombre,telefono,direccion) on public.perfiles to authenticated;
grant select on public.negocios,public.ofertas to anon,authenticated;
grant update(nombre,direccion,imagen_url,activo) on public.negocios to authenticated;
grant insert(negocio_id,nombre,categoria,descripcion,peso_kg,precio_normal,precio_rescate,
    cantidad_disponible,fecha_retiro,hora_retiro_desde,hora_retiro_hasta,imagen_url)
on public.ofertas to authenticated;
grant update(nombre,categoria,descripcion,peso_kg,precio_normal,precio_rescate,
    cantidad_disponible,fecha_retiro,hora_retiro_desde,hora_retiro_hasta,imagen_url)
on public.ofertas to authenticated;
grant usage on sequence public.ofertas_id_seq to authenticated;
grant select on public.pedidos to authenticated;
grant select,delete on public.notificaciones to authenticated;
grant update(leida) on public.notificaciones to authenticated;

create policy perfiles_propios_select on public.perfiles for select to authenticated
using (id = (select auth.uid()));
create policy perfiles_propios_update on public.perfiles for update to authenticated
using (id = (select auth.uid())) with check (id = (select auth.uid()));

create policy negocios_lectura on public.negocios for select to anon,authenticated
using (activo or propietario_id = (select auth.uid()));
create policy negocios_edicion on public.negocios for update to authenticated
using (propietario_id = (select auth.uid()))
with check (propietario_id = (select auth.uid()));

create policy ofertas_lectura on public.ofertas for select to anon,authenticated
using (
    exists(select 1 from public.negocios n where n.id = negocio_id
           and n.propietario_id = (select auth.uid()))
    or (
        estado = 'ACTIVA' and cantidad_disponible > 0
        and ((fecha_retiro + hora_retiro_hasta) at time zone 'America/La_Paz') > now()
        and exists(select 1 from public.negocios n where n.id = negocio_id and n.activo)
    )
);
create policy ofertas_creacion on public.ofertas for insert to authenticated
with check (exists(select 1 from public.negocios n where n.id = negocio_id
                   and n.propietario_id = (select auth.uid())));
create policy ofertas_edicion on public.ofertas for update to authenticated
using (estado <> 'ELIMINADA' and exists(select 1 from public.negocios n
       where n.id = negocio_id and n.propietario_id = (select auth.uid())))
with check (estado <> 'ELIMINADA' and exists(select 1 from public.negocios n
            where n.id = negocio_id and n.propietario_id = (select auth.uid())));

create policy pedidos_participantes on public.pedidos for select to authenticated
using (cliente_id = (select auth.uid()) or exists(select 1 from public.negocios n
       where n.id = negocio_id and n.propietario_id = (select auth.uid())));
create policy notificaciones_lectura on public.notificaciones for select to authenticated
using (destinatario_id = (select auth.uid()));
create policy notificaciones_leidas on public.notificaciones for update to authenticated
using (destinatario_id = (select auth.uid()))
with check (destinatario_id = (select auth.uid()));
create policy notificaciones_borrado on public.notificaciones for delete to authenticated
using (destinatario_id = (select auth.uid()));

insert into storage.buckets(id,name,public,file_size_limit,allowed_mime_types)
values ('ofertas','ofertas',true,5242880,array['image/jpeg','image/png','image/webp']);

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
