begin;

alter table public.negocios add column if not exists latitud double precision;
alter table public.negocios add column if not exists longitud double precision;
alter table public.negocios drop constraint if exists negocios_ubicacion_valida;
alter table public.negocios add constraint negocios_ubicacion_valida check (
    (latitud is null and longitud is null)
    or (latitud between -90 and 90 and longitud between -180 and 180)
);

grant update(latitud,longitud) on public.negocios to authenticated;

commit;
