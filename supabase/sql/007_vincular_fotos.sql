-- OPCIONAL: primero subir las cinco fotos JPG a Storage.
-- Ruta: ofertas/UUID_PROPIETARIO/mockapi-<legacy_id>.jpg
begin;
do $$
declare
    proyecto_url text := 'https://sdmhcdnyuplnthlljmud.supabase.co';
    correo_negocio text := 'panaderialacentral@gmail.com';
    propietario uuid;
begin
    if proyecto_url like '%REEMPLAZAR%' or correo_negocio like '%REEMPLAZAR%' then
        raise exception 'Reemplaza la URL del proyecto y el correo del negocio';
    end if;
    select n.propietario_id into propietario
    from public.negocios n join auth.users u on u.id = n.propietario_id
    where lower(u.email) = lower(correo_negocio);
    if propietario is null then raise exception 'No se encontro el negocio'; end if;
    update public.ofertas
    set imagen_url = rtrim(proyecto_url,'/') || '/storage/v1/object/public/ofertas/' ||
                     propietario::text || '/mockapi-' || legacy_id || '.jpg'
    where origen = 'mockapi' and legacy_id in ('1','2','3','4','6')
      and negocio_id in (select id from public.negocios where propietario_id = propietario);
end;
$$;
commit;
