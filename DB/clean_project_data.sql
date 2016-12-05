-- Select queries
select * from project;
select * from project_area;

--update project_area set district_name = 'Iringa (Rural)' where name = 'Kinywangaanga';
--update project_area set district_name = 'Iringa (Rural)' where name = 'Kitayawa_Live';
--update project_area set district_name = 'Iringa (Rural)' where name = 'Itagutwa_Live';

select * from  spatial_unit where project_name = 'Kinywangaanga';
select * from spatial_unit_tmp where project_name = 'Kinywangaanga';
select * from source_document where usin in (select usin from spatial_unit where project_name = 'Kinywangaanga');
select * from sunit_workflow_status_history where usin in (select usin from spatial_unit where project_name = 'Kinywangaanga');
select * from spatialunit_personwithinterest where usin in (select usin from spatial_unit where project_name = 'Kinywangaanga');
select * from spatialunit_deceased_person where usin in (select usin from spatial_unit where project_name = 'Kinywangaanga');
select * from social_tenure_relationship where usin in (select usin from spatial_unit where project_name = 'Kinywangaanga');
select * from person where person_gid in (select id from persons_tmp);
select * from non_natural_person where non_natural_person_gid in (select id from persons_tmp);
select * from natural_person where gid in (select person_gid from social_tenure_relationship where usin in (select usin from spatial_unit where project_name = 'Kinywangaanga'));

-- Delete queries
delete from source_document where usin in (select usin from spatial_unit where project_name = 'Kinywangaanga');
delete from sunit_workflow_status_history where usin in (select usin from spatial_unit where project_name = 'Kinywangaanga');
delete from spatialunit_personwithinterest where usin in (select usin from spatial_unit where project_name = 'Kinywangaanga');
delete from spatialunit_deceased_person where usin in (select usin from spatial_unit where project_name = 'Kinywangaanga');
CREATE TEMP TABLE persons_tmp (
  id bigint
);
insert into persons_tmp (id) select person_gid from social_tenure_relationship where usin in (select usin from spatial_unit where project_name = 'Kinywangaanga');
delete from social_tenure_relationship where usin in (select usin from spatial_unit where project_name = 'Kinywangaanga');
delete from non_natural_person where non_natural_person_gid in (select id from persons_tmp);
delete from natural_person where gid in (select id from persons_tmp);
delete from person where person_gid in (select id from persons_tmp);
drop table persons_tmp;
delete from spatial_unit_tmp where project_name = 'Kinywangaanga';
delete from spatial_unit where project_name = 'Kinywangaanga';
