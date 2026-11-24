begin;

alter table public.pedidos add column if not exists cliente_telefono text;

update public.pedidos as pedido
set cliente_telefono = nullif(btrim(perfil.telefono), '')
from public.perfiles as perfil
where perfil.id = pedido.cliente_id and pedido.cliente_telefono is null;

create or replace function public.rescatapp_contacto_cliente_pedido()
returns trigger language plpgsql security definer set search_path = '' as $$
begin
    select perfil.nombre, nullif(btrim(perfil.telefono), '')
    into new.cliente_nombre, new.cliente_telefono
    from public.perfiles as perfil
    where perfil.id = new.cliente_id;
    return new;
end;
$$;

revoke all on function public.rescatapp_contacto_cliente_pedido() from public, anon, authenticated;
drop trigger if exists pedidos_contacto_cliente on public.pedidos;
create trigger pedidos_contacto_cliente before insert on public.pedidos
for each row execute function public.rescatapp_contacto_cliente_pedido();

notify pgrst, 'reload schema';
commit;
