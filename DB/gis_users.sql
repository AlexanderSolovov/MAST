-- Create gis_users role who can access MASD DB and modify spatial units
CREATE ROLE gis_users;
GRANT USAGE ON SCHEMA public TO gis_users;
GRANT SELECT ON ALL TABLES IN SCHEMA public TO gis_users;
GRANT UPDATE ON public.spatial_unit TO gis_users;
GRANT INSERT ON public.spatial_unit TO gis_users;
GRANT DELETE ON public.spatial_unit TO gis_users;
REVOKE SELECT ON public.users FROM gis_users;

-- Greates gis user and assign gis_users role
CREATE ROLE gis WITH LOGIN PASSWORD 'Welcome1';
GRANT gis_users TO gis;

