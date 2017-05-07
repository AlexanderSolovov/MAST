--select string_agg(usin::text,',') from spatial_unit where the_geom is null;

-- DELETES CLAIMS 
DO $$
DECLARE usins BIGINT ARRAY;
BEGIN

usins := ARRAY[1,2,3];

-- documents
delete from attribute where 
  uid in (select pa.uid from surveyprojectattributes pa inner join attribute_master am on pa.id=am.id where am.attributecategoryid = 3 and am.reftable = 'custom') 
  and parentuid in (select gid from source_document where usin = any (usins));
delete from attribute where 
  uid in (select pa.uid from surveyprojectattributes pa inner join attribute_master am on pa.id=am.id where am.reftable = 'source_document') 
  and parentuid in (select gid from source_document where usin = any (usins));
delete from source_document where usin = any (usins);

-- Worflow history
delete from sunit_workflow_status_history where usin = any (usins);

-- POIs
delete from spatialunit_personwithinterest where usin = any (usins);

-- Deceased persons
delete from spatialunit_deceased_person where usin = any (usins);

-- Make temp persons ids
CREATE TEMP TABLE persons_tmp (
  id bigint
);
insert into persons_tmp (id) 
(
select person_gid from social_tenure_relationship where usin = any (usins)
union 
select p.gid from natural_person p inner join dispute_person dp on p.gid = dp.person_id inner join dispute d on dp.dispute_id = d.id 
where d.usin = any (usins)
);

-- Right
delete from attribute where 
  uid in (select pa.uid from surveyprojectattributes pa inner join attribute_master am on pa.id=am.id where am.attributecategoryid = 4 and am.reftable = 'custom') 
  and parentuid in (select gid from social_tenure_relationship where usin = any (usins));
delete from attribute where 
  uid in (select pa.uid from surveyprojectattributes pa inner join attribute_master am on pa.id=am.id where am.reftable = 'social_tenure_relationship') 
  and parentuid in (select gid from social_tenure_relationship where usin = any (usins));
delete from social_tenure_relationship where usin = any (usins);

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
where usin = any (usins);

-- Spatial unit
delete from attribute where 
  uid in (select pa.uid from surveyprojectattributes pa inner join attribute_master am on pa.id=am.id where am.attributecategoryid in (1,7,6) and am.reftable = 'custom') 
  and parentuid = any (usins);
delete from attribute where 
  uid in (select pa.uid from surveyprojectattributes pa inner join attribute_master am on pa.id=am.id where am.reftable = 'spatial_unit') 
  and parentuid = any (usins);
delete from spatial_unit_tmp where usin = any (usins);
delete from spatial_unit where usin = any (usins);
  
END $$;


