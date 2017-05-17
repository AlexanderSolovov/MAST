-- DELETES CLAIMS MARKED TO DELETION active = false
DO $$
DECLARE project TEXT;
BEGIN

project := 'Kinywangaanga';

-- documents
delete from attribute where 
  uid in (select pa.uid from surveyprojectattributes pa inner join attribute_master am on pa.id=am.id where am.attributecategoryid = 3 and am.reftable = 'custom') 
  and parentuid in (select gid from source_document where usin in (select usin from spatial_unit where project_name = project and active = 'f'));
delete from attribute where 
  uid in (select pa.uid from surveyprojectattributes pa inner join attribute_master am on pa.id=am.id where am.reftable = 'source_document') 
  and parentuid in (select gid from source_document where usin in (select usin from spatial_unit where project_name = project and active = 'f'));
delete from source_document where usin in (select usin from spatial_unit where project_name = project and active = 'f');

-- Worflow history
delete from sunit_workflow_status_history where usin in (select usin from spatial_unit where project_name = project and active = 'f');

-- POIs
delete from spatialunit_personwithinterest where usin in (select usin from spatial_unit where project_name = project and active = 'f');

-- Deceased persons
delete from spatialunit_deceased_person where usin in (select usin from spatial_unit where project_name = project and active = 'f');

-- Make temp persons ids
CREATE TEMP TABLE persons_tmp (
  id bigint
);
insert into persons_tmp (id) 
(
select person_gid from social_tenure_relationship where usin in (select usin from spatial_unit where project_name = project and active = 'f')
union 
select p.gid from natural_person p inner join dispute_person dp on p.gid = dp.person_id inner join dispute d on dp.dispute_id = d.id 
where d.usin in (select usin from spatial_unit where project_name = project and active = 'f')
);

-- Right
delete from attribute where 
  uid in (select pa.uid from surveyprojectattributes pa inner join attribute_master am on pa.id=am.id where am.attributecategoryid = 4 and am.reftable = 'custom') 
  and parentuid in (select gid from social_tenure_relationship where usin in (select usin from spatial_unit where project_name = project and active = 'f'));
delete from attribute where 
  uid in (select pa.uid from surveyprojectattributes pa inner join attribute_master am on pa.id=am.id where am.reftable = 'social_tenure_relationship') 
  and parentuid in (select gid from social_tenure_relationship where usin in (select usin from spatial_unit where project_name = project and active = 'f'));
delete from social_tenure_relationship where usin in (select usin from spatial_unit where project_name = project and active = 'f');

-- Non natural person
delete from attribute where 
  uid in (select pa.uid from surveyprojectattributes pa inner join attribute_master am on pa.id=am.id where am.attributecategoryid = 5 and am.reftable = 'custom') 
  and parentuid in (select non_natural_person_gid from non_natural_person where non_natural_person_gid in (select id from persons_tmp));
delete from attribute where 
  uid in (select pa.uid from surveyprojectattributes pa inner join attribute_master am on pa.id=am.id where am.reftable = 'non_natural_person') 
  and parentuid in (select non_natural_person_gid from non_natural_person where non_natural_person_gid in (select id from persons_tmp));
delete from non_natural_person where non_natural_person_gid in (select id from persons_tmp);

-- Natural person
delete from attribute where 
  uid in (select pa.uid from surveyprojectattributes pa inner join attribute_master am on pa.id=am.id where am.attributecategoryid = 2 and am.reftable = 'custom') 
  and parentuid in (select gid from natural_person where gid in (select id from persons_tmp));
delete from attribute where 
  uid in (select pa.uid from surveyprojectattributes pa inner join attribute_master am on pa.id=am.id where am.reftable = 'natural_person') 
  and parentuid in (select gid from natural_person where gid in (select id from persons_tmp));
delete from natural_person where gid in (select id from persons_tmp);

-- General person
delete from person where person_gid in (select id from persons_tmp);
drop table persons_tmp;

-- Disputes
delete from dispute 
where usin in (select usin from spatial_unit where project_name = project and active = 'f');

-- Spatial unit
delete from attribute where 
  uid in (select pa.uid from surveyprojectattributes pa inner join attribute_master am on pa.id=am.id where am.attributecategoryid in (1,7,6) and am.reftable = 'custom') 
  and parentuid in (select usin from spatial_unit where project_name = project and active = 'f');
delete from attribute where 
  uid in (select pa.uid from surveyprojectattributes pa inner join attribute_master am on pa.id=am.id where am.reftable = 'spatial_unit') 
  and parentuid in (select usin from spatial_unit where project_name = project and active = 'f');
delete from spatial_unit_tmp where project_name = project and active = 'f';
delete from spatial_unit where project_name = project and active = 'f';
  
END $$;


