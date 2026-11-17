-- RescatApp: ejecutar UNA VEZ, en un proyecto nuevo dedicado a la app.
-- No borra ni reemplaza tablas existentes. Si algo falla, se revierte este bloque.
begin;

create table public.perfiles (
    id uuid primary key references auth.users(id) on delete cascade,
    nombre text not null check (length(btrim(nombre)) between 1 and 120),
    rol text not null default 'CLIENTE' check (rol in ('CLIENTE', 'NEGOCIO')),
    telefono text not null default '',
    direccion text not null default '',
    creado_at timestamptz not null default now()
);

create table public.negocios (
    id integer generated always as identity primary key,
    propietario_id uuid not null unique references public.perfiles(id) on delete restrict,
    nombre text not null check (length(btrim(nombre)) between 1 and 160),
    direccion text not null default '',
    imagen_url text,
    activo boolean not null default true,
    creado_at timestamptz not null default now()
);

create table public.ofertas (
    id integer generated always as identity primary key,
    negocio_id integer not null references public.negocios(id) on delete restrict,
    origen text,
    legacy_id text,
    nombre text not null check (length(btrim(nombre)) between 1 and 160),
    categoria text not null check (categoria in ('PANADERIA','COMIDA','POSTRES','CAFETERIA')),
    descripcion text not null default '',
    peso_kg numeric(10,3) not null check (peso_kg > 0 and peso_kg <> 'NaN'::numeric),
    precio_normal numeric(12,2) not null check (precio_normal > 0 and precio_normal <> 'NaN'::numeric),
    precio_rescate numeric(12,2) not null check (
        precio_rescate > 0 and precio_rescate < precio_normal and precio_rescate <> 'NaN'::numeric),
    cantidad_disponible integer not null check (cantidad_disponible >= 0),
    fecha_retiro date not null default ((now() at time zone 'America/La_Paz')::date),
    hora_retiro_desde time not null,
    hora_retiro_hasta time not null,
    imagen_url text,
    estado text not null default 'ACTIVA' check (estado in ('ACTIVA','PAUSADA','ELIMINADA')),
    creado_at timestamptz not null default now(),
    actualizado_at timestamptz not null default now(),
    unique (origen, legacy_id),
    check (hora_retiro_hasta > hora_retiro_desde),
    check ((origen is null) = (legacy_id is null))
);
create index ofertas_negocio_idx on public.ofertas(negocio_id);
create index ofertas_catalogo_idx on public.ofertas(estado, categoria, fecha_retiro);

create table public.pedidos (
    id integer generated always as identity primary key,
    cliente_id uuid not null references public.perfiles(id) on delete restrict,
    cliente_nombre text not null,
    negocio_id integer not null references public.negocios(id) on delete restrict,
    oferta_id integer not null references public.ofertas(id) on delete restrict,
    solicitud_id uuid not null,
    nombre_oferta text not null,
    comercio text not null,
    categoria text not null,
    precio_pagado numeric(12,2) not null check (precio_pagado > 0),
    ahorro numeric(12,2) not null check (ahorro >= 0),
    peso_kg numeric(10,3) not null check (peso_kg > 0),
    fecha_retiro date not null,
    hora_retiro_desde time not null,
    hora_retiro_hasta time not null,
    imagen_url text,
    estado text not null default 'RESERVADO'
        check (estado in ('RESERVADO','PREPARANDO','LISTO','RECOGIDO','CANCELADO')),
    codigo_rescate text generated always as ('#APV-' || id::text) stored,
    pago_simulado boolean not null default true check (pago_simulado),
    creado_at timestamptz not null default now(),
    actualizado_at timestamptz not null default now(),
    unique (cliente_id, solicitud_id)
);
create index pedidos_cliente_idx on public.pedidos(cliente_id, creado_at desc);
create index pedidos_negocio_idx on public.pedidos(negocio_id, creado_at desc);

create table public.notificaciones (
    id uuid primary key default gen_random_uuid(),
    destinatario_id uuid not null references public.perfiles(id) on delete cascade,
    rol_destino text not null check (rol_destino in ('CLIENTE','NEGOCIO')),
    titulo text not null,
    mensaje text not null,
    tipo text not null check (tipo in (
        'PEDIDO_RESERVADO','PEDIDO_EN_PREPARACION','PEDIDO_LISTO','PEDIDO_RECOGIDO',
        'PEDIDO_CANCELADO','NUEVO_PEDIDO_VENDEDOR','OFERTA_AGOTADA','OFERTA_PUBLICADA','SISTEMA'
    )),
    leida boolean not null default false,
    pedido_id integer references public.pedidos(id) on delete restrict,
    oferta_id integer references public.ofertas(id) on delete restrict,
    creado_at timestamptz not null default now()
);
create index notificaciones_usuario_idx on public.notificaciones(destinatario_id, leida, creado_at desc);

-- Evento publico sin datos privados: permite retirar una oferta pausada del
-- catalogo aunque RLS ya no deje al cliente ver la nueva fila de esa oferta.
create table public.catalogo_actualizacion (
    id smallint primary key check (id = 1),
    version bigint not null default 0,
    actualizado_at timestamptz not null default now()
);
insert into public.catalogo_actualizacion(id) values (1);
create function public.rescatapp_invalidar_catalogo()
returns trigger language plpgsql security definer set search_path = '' as $$
begin
    update public.catalogo_actualizacion set version = version + 1,actualizado_at = now() where id = 1;
    return null;
end;
$$;
revoke all on function public.rescatapp_invalidar_catalogo() from public,anon,authenticated;
create trigger ofertas_invalidar_catalogo after insert or update or delete on public.ofertas
for each statement execute function public.rescatapp_invalidar_catalogo();
create trigger negocios_invalidar_catalogo after insert or update or delete on public.negocios
for each statement execute function public.rescatapp_invalidar_catalogo();

create function public.rescatapp_actualizar_fecha()
returns trigger language plpgsql set search_path = '' as $$
begin
    new.actualizado_at := now();
    return new;
end;
$$;
create trigger ofertas_fecha before update on public.ofertas
for each row execute function public.rescatapp_actualizar_fecha();
create trigger pedidos_fecha before update on public.pedidos
for each row execute function public.rescatapp_actualizar_fecha();
revoke all on function public.rescatapp_actualizar_fecha() from public, anon, authenticated;

-- Los metadatos solo pueden elegir entre los dos perfiles permitidos.
-- La propiedad del negocio siempre procede de NEW.id, nunca del cliente.
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

-- Revocar los permisos por defecto de estas tablas; no se tocan tablas ajenas.
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

commit;
