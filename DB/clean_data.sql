
-- documents
delete from attribute;
delete from source_document;

-- Worflow history
delete from sunit_workflow_status_history;

-- POIs
delete from spatialunit_personwithinterest;

-- Deceased persons
delete from spatialunit_deceased_person;

-- Right
delete from social_tenure_relationship;

-- Non natural person
delete from non_natural_person;

-- Natural person
delete from natural_person;

-- General person
delete from person_administrator;
delete from person;

-- Disputes
delete from dispute;

-- Spatial unit
ALTER TABLE spatial_unit DISABLE TRIGGER trg_change_spatial_unit;
delete from spatial_unit_tmp;
delete from spatial_unit;
ALTER TABLE spatial_unit ENABLE TRIGGER trg_change_spatial_unit;

/* 
PROJECTS CLEANING
Removes all projects except Kiponzelo and removes all users except admin. Admin user is assigned 'admin' password.
First step after accessing the system is to create intermediary user who will have access to the Kiponzelo project.
Alternatively, new project can be created.
*/ 

delete from layer_field where layer not in ('mast:spatial_unit','mast:Kiponzelo_Boundary','mast:Kiponzelo_raster');
delete from layer where name not in ('mast:spatial_unit','mast:Kiponzelo_Boundary','mast:Kiponzelo_raster') and alias != 'spatial_unit';
delete from layer where alias = 'Test:spatial_unit';
update layer set url = 'http://localhost:9090/geoserver/wms?' where name = 'mast:spatial_unit';

delete from layer_layergroup where layergroup not in ('Spatial_Unit','Kiponzelo_back_image','Kiponzelo_vector');
delete from layergroup where name not in ('Spatial_Unit','Kiponzelo_back_image','Kiponzelo_vector');

-- gis_users role

DO
$body$
BEGIN
   IF NOT EXISTS (
      SELECT                       
      FROM   pg_catalog.pg_user
      WHERE  usename = 'gis_users') THEN

      CREATE ROLE gis_users NOSUPERUSER INHERIT NOCREATEDB NOCREATEROLE NOREPLICATION;
   END IF;
END
$body$;

-- Users

delete from user_role where userid in (select id from users where username != 'admin');
delete from user_project where userid in (select id from users where username != 'admin') or project != 'Kiponzelo';
delete from users where username != 'admin';
update users set defaultproject = 'Kiponzelo', passwordexpires = '2020-01-01', active='t', password='vNB4eyGdRaHryEEhufXlZw==', 
authkey='VwwJWms09cV9J7QyfPgt943bRd0P3Mwt2EJ9qAAXj6QU9v%2FvWYhHzxQN%2FB30qGKZ'
where username = 'admin';

-- Projects
delete from surveyprojectattributes where name != 'Kiponzelo';
delete from project_adjudicators where project_name != 'Kiponzelo';
delete from project_area where name != 'Kiponzelo';
delete from project_baselayer where project != 'Kiponzelo';
delete from project_hamlets where project_name != 'Kiponzelo';

delete from project_layergroup where project != 'Kiponzelo';
delete from project_spatial_data where name != 'Kiponzelo';
delete from bookmark where project != 'Kiponzelo';
delete from savedquery where name != 'Kiponzelo';
delete from project where name != 'Kiponzelo';
