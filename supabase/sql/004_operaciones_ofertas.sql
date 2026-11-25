begin;

create function public.cambiar_estado_oferta(p_oferta_id integer,p_estado text)
returns setof public.ofertas language plpgsql security definer set search_path = '' as $$
declare oferta public.ofertas%rowtype;
begin
    if auth.uid() is null then raise exception 'Se necesita sesion'; end if;
    if p_estado not in ('ACTIVA','PAUSADA') or p_estado is null then
        raise exception 'Usa ACTIVA o PAUSADA';
    end if;
    select * into oferta from public.ofertas where id = p_oferta_id for update;
    if not found then raise exception 'Oferta no encontrada'; end if;
    if not exists(select 1 from public.negocios n where n.id = oferta.negocio_id
                  and n.propietario_id = auth.uid()) then
        raise exception 'La oferta pertenece a otro negocio';
    end if;
    if oferta.estado = 'ELIMINADA' then raise exception 'La oferta fue eliminada'; end if;
    update public.ofertas set estado = p_estado where id = oferta.id returning * into oferta;
    return next oferta;
end;
$$;

create function public.eliminar_oferta(p_oferta_id integer)
returns setof public.ofertas language plpgsql security definer set search_path = '' as $$
declare oferta public.ofertas%rowtype;
begin
    if auth.uid() is null then raise exception 'Se necesita sesion'; end if;
    select * into oferta from public.ofertas where id = p_oferta_id for update;
    if not found then raise exception 'Oferta no encontrada'; end if;
    if not exists(select 1 from public.negocios n where n.id = oferta.negocio_id
                  and n.propietario_id = auth.uid()) then
        raise exception 'La oferta pertenece a otro negocio';
    end if;
    update public.ofertas set estado = 'ELIMINADA' where id = oferta.id returning * into oferta;
    return next oferta;
end;
$$;

revoke all on function public.cambiar_estado_oferta(integer,text) from public,anon,authenticated;
revoke all on function public.eliminar_oferta(integer) from public,anon,authenticated;
grant execute on function public.cambiar_estado_oferta(integer,text) to authenticated;
grant execute on function public.eliminar_oferta(integer) to authenticated;

commit;
