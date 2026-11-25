begin;

alter table public.pedidos add column if not exists estado_pago_simulado text
    not null default 'APROBADO';
update public.pedidos
set estado_pago_simulado = case when estado = 'CANCELADO' then 'ANULADO' else 'APROBADO' end
where estado_pago_simulado is distinct from
    case when estado = 'CANCELADO' then 'ANULADO' else 'APROBADO' end;
alter table public.pedidos drop constraint if exists pedidos_pago_simulado_estado;
alter table public.pedidos add constraint pedidos_pago_simulado_estado check (
    estado_pago_simulado in ('APROBADO','ANULADO')
    and ((estado = 'CANCELADO') = (estado_pago_simulado = 'ANULADO'))
);

create or replace function public.cancelar_pedido(p_pedido_id integer)
returns setof public.pedidos language plpgsql security definer set search_path = '' as $$
declare
    usuario uuid := auth.uid();
    pedido public.pedidos%rowtype;
    propietario uuid;
begin
    if usuario is null then raise exception 'Se necesita sesion'; end if;
    select * into pedido from public.pedidos where id = p_pedido_id for update;
    if not found then raise exception 'Pedido no encontrado'; end if;
    select propietario_id into propietario from public.negocios where id = pedido.negocio_id;
    if usuario <> pedido.cliente_id and usuario <> propietario then
        raise exception 'No tienes acceso a este pedido';
    end if;
    if pedido.estado = 'CANCELADO' then return next pedido; return; end if;
    if pedido.estado <> 'RESERVADO' then
        raise exception 'Solo se cancela un pedido reservado';
    end if;
    update public.ofertas set cantidad_disponible = cantidad_disponible + 1
    where id = pedido.oferta_id;
    update public.pedidos set estado = 'CANCELADO',estado_pago_simulado = 'ANULADO'
    where id = pedido.id returning * into pedido;
    insert into public.notificaciones(destinatario_id,rol_destino,titulo,mensaje,tipo,pedido_id,oferta_id)
    values
        (pedido.cliente_id,'CLIENTE','Pedido cancelado',
         pedido.nombre_oferta || '. Pago simulado anulado; no hubo un cobro bancario real.',
         'PEDIDO_CANCELADO',pedido.id,pedido.oferta_id),
        (propietario,'NEGOCIO','Pedido cancelado',
         pedido.nombre_oferta || '. Unidad devuelta al stock y pago simulado anulado.',
         'PEDIDO_CANCELADO',pedido.id,pedido.oferta_id);
    return next pedido;
end;
$$;
revoke all on function public.cancelar_pedido(integer) from public,anon,authenticated;
grant execute on function public.cancelar_pedido(integer) to authenticated;

notify pgrst, 'reload schema';
commit;
