-- Create gis_users role who can access MASD DB and modify spatial units
DO
$body$
BEGIN
   IF NOT EXISTS (
      SELECT 1
      FROM   pg_catalog.pg_roles
      WHERE  rolname='gis_users') THEN
        CREATE ROLE gis_users;
   END IF;
END
$body$;

GRANT USAGE ON SCHEMA public TO gis_users;
GRANT SELECT ON ALL TABLES IN SCHEMA public TO gis_users;
GRANT UPDATE ON public.spatial_unit TO gis_users;
GRANT INSERT ON public.spatial_unit TO gis_users;
GRANT DELETE ON public.spatial_unit TO gis_users;
REVOKE SELECT ON public.users FROM gis_users;

-- Greates gis user and assign gis_users role
DO
$body$
BEGIN
   IF NOT EXISTS (
      SELECT 1
      FROM   pg_catalog.pg_user
      WHERE  usename='gis') THEN
        CREATE ROLE gis WITH LOGIN PASSWORD 'Welcome1';
   END IF;
END
$body$;

GRANT gis_users TO gis;


