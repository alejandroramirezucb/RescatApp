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

commit;
