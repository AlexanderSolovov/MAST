delete from source_document where usin in (select usin from spatial_unit where project_name = 'Kinywangaanga' and active = 'f');
delete from sunit_workflow_status_history where usin in (select usin from spatial_unit where project_name = 'Kinywangaanga' and active = 'f');
delete from spatialunit_personwithinterest where usin in (select usin from spatial_unit where project_name = 'Kinywangaanga' and active = 'f');
delete from spatialunit_deceased_person where usin in (select usin from spatial_unit where project_name = 'Kinywangaanga' and active = 'f');
CREATE TEMP TABLE persons_tmp (
  id bigint
);
insert into persons_tmp (id) select person_gid from social_tenure_relationship where usin in (select usin from spatial_unit where project_name = 'Kinywangaanga' and active = 'f');
delete from social_tenure_relationship where usin in (select usin from spatial_unit where project_name = 'Kinywangaanga' and active = 'f');
delete from non_natural_person where non_natural_person_gid in (select id from persons_tmp);
delete from natural_person where gid in (select id from persons_tmp);
delete from person where person_gid in (select id from persons_tmp);
delete from attribute where uid in (832,833) and parentuid in (select id from persons_tmp);
drop table persons_tmp;
delete from attribute where uid in (830,831) and parentuid in (select usin from spatial_unit where project_name = 'Kinywangaanga' and active = 'f');
delete from spatial_unit_tmp where project_name = 'Kinywangaanga' and active = 'f';
delete from spatial_unit where project_name = 'Kinywangaanga' and active = 'f';
