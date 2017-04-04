-- land use
ALTER TABLE public.land_use_type
 ADD COLUMN active boolean NOT NULL DEFAULT true;
COMMENT ON COLUMN public.land_use_type.active IS 'Boolean flag indicating whether the record is active and should be displayed on the forms.';

update public.land_use_type set land_use_type = 'Forestry', land_use_type_sw = 'Misitu na hifadhi' where use_type_id = 2;
update public.land_use_type set land_use_type = 'Residential', land_use_type_sw = 'Makazi' where use_type_id = 3;
update public.land_use_type set land_use_type = 'Grazing', land_use_type_sw = 'Malisho' where use_type_id = 4;
insert into public.land_use_type (use_type_id, land_use_type, land_use_type_sw) values (15, 'Residential and agricultural', 'Makazi na kilimo');
insert into public.land_use_type (use_type_id, land_use_type, land_use_type_sw) values (16, 'Wildlife/Tourism', 'Hifadhi za wanyama na Utaliio');
insert into public.land_use_type (use_type_id, land_use_type, land_use_type_sw) values (17, 'Social services', 'Huduma za jamii');
insert into public.land_use_type (use_type_id, land_use_type, land_use_type_sw) values (18, 'Mining', 'Maeneo ya madini');
update public.land_use_type set active = 'f' where use_type_id in (5,6,7,13);

-- spatial unit status
update public.sunit_status set workflow_status = 'Denied' where workflow_status_id = 5;
update public.sunit_status set workflow_status = 'Validated' where workflow_status_id = 3;
update public.sunit_status set workflow_status = 'Referred to VC/VLC' where workflow_status_id = 4;
update public.sunit_status set workflow_status = 'Approved' where workflow_status_id = 2;
update public.sunit_workflow_status_history set workflow_status_id = 2 where workflow_status_id = 6;
update public.sunit_workflow_status_history set workflow_status_id = 2 where workflow_status_id = 7;
update public.sunit_workflow_status_history set workflow_status_id = 3 where workflow_status_id = 4;

update public.spatial_unit set current_workflow_status_id = 2 where current_workflow_status_id = 6;
update public.spatial_unit set current_workflow_status_id = 2 where current_workflow_status_id = 7;
update public.spatial_unit set current_workflow_status_id = 3 where current_workflow_status_id = 4;

delete from public.sunit_status where workflow_status_id in (6,7);

-- hamlet
ALTER TABLE public.project_hamlets
  ADD COLUMN hamlet_leader_name character varying(500);
COMMENT ON COLUMN public.project_hamlets.hamlet_leader_name IS 'Hamlet leader name(s)';

-- ref data
CREATE TABLE public.claim_type
(
   code character varying(20) NOT NULL, 
   name character varying(200) NOT NULL, 
   name_other_lang character varying(200), 
   active boolean NOT NULL DEFAULT 't', 
   CONSTRAINT code_pk PRIMARY KEY (code)
) 
WITH (
  OIDS = FALSE
)
;
COMMENT ON COLUMN public.claim_type.code IS 'Claim type code - primary key';
COMMENT ON COLUMN public.claim_type.name IS 'Claim type name';
COMMENT ON COLUMN public.claim_type.name_other_lang IS 'Claim type name in other language';
COMMENT ON COLUMN public.claim_type.active IS 'Boolean flag indicating whether the record is active and should be displayed on the forms.';
COMMENT ON TABLE public.claim_type
  IS 'Reference data table for claim types';

INSERT INTO public.claim_type (code, name, name_other_lang, active) VALUES ('newClaim', 'New claim', 'Madai new', 't');
INSERT INTO public.claim_type (code, name, name_other_lang, active) VALUES ('existingClaim', 'Existing right', 'Haki zilizopo', 't');
INSERT INTO public.claim_type (code, name, name_other_lang, active) VALUES ('unclaimed', 'Unclaimed', 'Unclaimed', 't');
INSERT INTO public.claim_type (code, name, name_other_lang, active) VALUES ('dispute', 'Disputed claim', 'Madai mgogoro', 't');

ALTER TABLE public.tenure_class ADD COLUMN for_adjudication boolean DEFAULT 't';
COMMENT ON COLUMN public.tenure_class.for_adjudication IS 'Indicates whether type right should be processed through adjudication process.';

UPDATE public.tenure_class SET for_adjudication = 'f';
UPDATE public.tenure_class SET for_adjudication = 't' WHERE tenureclass_id = 2;

CREATE TABLE public.acquisition_type
(
  code integer NOT NULL, -- Acquisition type code - primary key.
  name character varying(200) NOT NULL, -- Acquisition type name.
  name_other_lang character varying(200), -- Acquisition type name in other language.
  active boolean NOT NULL DEFAULT true, -- Boolean flag indicating whether the record is active and can be used for displaying on the forms.
  CONSTRAINT pk_code PRIMARY KEY (code)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.acquisition_type
  OWNER TO postgres;
COMMENT ON TABLE public.acquisition_type
  IS 'Reference data table listing different type of land acquisition.';
COMMENT ON COLUMN public.acquisition_type.code IS 'Acquisition type code - primary key.';
COMMENT ON COLUMN public.acquisition_type.name IS 'Acquisition type name.';
COMMENT ON COLUMN public.acquisition_type.name_other_lang IS 'Acquisition type name in other language.';
COMMENT ON COLUMN public.acquisition_type.active IS 'Boolean flag indicating whether the record is active and can be used for displaying on the forms.';

INSERT INTO public.acquisition_type(code, name, name_other_lang, active) VALUES (1, 'Allocated by Village Council', 'Zilizotengwa kwa Halmashauri ya Kijiji', 't');
INSERT INTO public.acquisition_type(code, name, name_other_lang, active) VALUES (2, 'Gift', 'Kipawa', 't');
INSERT INTO public.acquisition_type(code, name, name_other_lang, active) VALUES (3, 'Inheritance', 'Urithi', 't');
INSERT INTO public.acquisition_type(code, name, name_other_lang, active) VALUES (4, 'Purchase', 'Ununuzi', 't');

ALTER TABLE public.spatial_unit ADD COLUMN claim_type character varying(20);
ALTER TABLE public.spatial_unit ADD COLUMN polygon_number character varying(20);

ALTER TABLE public.spatial_unit ADD CONSTRAINT fk_claim_type_code FOREIGN KEY (claim_type) REFERENCES public.claim_type (code) ON UPDATE NO ACTION ON DELETE NO ACTION;

COMMENT ON COLUMN public.spatial_unit.claim_type IS 'Claim type code';
COMMENT ON COLUMN public.spatial_unit.polygon_number IS 'Polygon number assigned in the field manually. This value is supposed to be unique per village.';

UPDATE public.spatial_unit SET claim_type = 'newClaim';

ALTER TABLE public.spatial_unit ALTER COLUMN claim_type SET DEFAULT 'newClaim';
ALTER TABLE public.spatial_unit ALTER COLUMN claim_type SET NOT NULL;

ALTER TABLE public.spatial_unit_tmp ADD COLUMN claim_type character varying(20);
ALTER TABLE public.spatial_unit_tmp ADD COLUMN polygon_number character varying(20);

ALTER TABLE public.spatial_unit_tmp ADD CONSTRAINT fk_claim_type_code FOREIGN KEY (claim_type) REFERENCES public.claim_type (code) ON UPDATE NO ACTION ON DELETE NO ACTION;

COMMENT ON COLUMN public.spatial_unit_tmp.claim_type IS 'Claim type code';
COMMENT ON COLUMN public.spatial_unit_tmp.polygon_number IS 'Polygon number assigned in the field manually. This value is supposed to be unique per village.';

-- spatial_unit trigger to control modifications
CREATE OR REPLACE FUNCTION public.f_for_trg_change_spatial_unit()
  RETURNS trigger AS
$BODY$
BEGIN
   IF (OLD.current_workflow_status_id not in (1,3,4)) THEN
      RAISE EXCEPTION 'You cannot modify this record, because of its status';
   END IF; 
   RETURN NEW;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION public.f_for_trg_change_spatial_unit()
  OWNER TO postgres;
COMMENT ON FUNCTION public.f_for_trg_change_spatial_unit() IS 'This function controls spatial_unit table modifications, allowing modifications only on records with new status.';


CREATE TRIGGER trg_change_spatial_unit
  BEFORE UPDATE OR DELETE
  ON public.spatial_unit
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_change_spatial_unit();

 
CREATE TABLE public.id_type
(
   code integer NOT NULL, 
   name character varying(200) NOT NULL, 
   name_other_lang character varying(200), 
   active boolean NOT NULL DEFAULT 't', 
   CONSTRAINT id_type_pk_code PRIMARY KEY (code)
) 
WITH (
  OIDS = FALSE
)
;
COMMENT ON COLUMN public.id_type.code IS 'ID type code - primary key';
COMMENT ON COLUMN public.id_type.name IS 'ID type name';
COMMENT ON COLUMN public.id_type.name_other_lang IS 'ID type name in other language';
COMMENT ON COLUMN public.id_type.active IS 'Boolean flag indicating whether the record is active and should be displayed on the forms.';
COMMENT ON TABLE public.id_type
  IS 'Reference data table for ID types';

INSERT INTO public.id_type (code, name, name_other_lang, active) VALUES (1, 'Voter ID', 'ID wapiga kura', 't');
INSERT INTO public.id_type (code, name, name_other_lang, active) VALUES (2, 'Driving license', 'Leseni ya kuendesha gari', 't');
INSERT INTO public.id_type (code, name, name_other_lang, active) VALUES (3, 'Passport', 'Pasipoti', 't');
INSERT INTO public.id_type (code, name, name_other_lang, active) VALUES (4, 'ID card', 'Namba ya kitambulisho', 't');
INSERT INTO public.id_type (code, name, name_other_lang, active) VALUES (5, 'Political affiliation card', 'Political kadi uhusiano', 't');

-- Person
CREATE TABLE public.relationship_type
(
  code integer NOT NULL, -- Relationship type code - primary key.
  name character varying(200) NOT NULL, -- Relationship type name.
  name_other_lang character varying(200), -- Relationship type name in other language.
  active boolean NOT NULL DEFAULT true, -- Boolean flag indicating whether the record is active and can be used for displaying on the forms.
  CONSTRAINT relationship_type_pk_code PRIMARY KEY (code)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.relationship_type
  OWNER TO postgres;
COMMENT ON TABLE public.relationship_type
  IS 'Reference data table listing relationship types between persons.';
COMMENT ON COLUMN public.relationship_type.code IS 'Relationship type code - primary key.';
COMMENT ON COLUMN public.relationship_type.name IS 'Relationship type name.';
COMMENT ON COLUMN public.relationship_type.name_other_lang IS 'Relationship type name in other language.';
COMMENT ON COLUMN public.relationship_type.active IS 'Boolean flag indicating whether the record is active and can be used for displaying on the forms.';

INSERT INTO public.relationship_type(code, name, name_other_lang, active) VALUES (1, 'Father', 'Baba', 't');
INSERT INTO public.relationship_type(code, name, name_other_lang, active) VALUES (2, 'Mother', 'Mama', 't');
INSERT INTO public.relationship_type(code, name, name_other_lang, active) VALUES (3, 'Sister', 'Dada', 't');
INSERT INTO public.relationship_type(code, name, name_other_lang, active) VALUES (4, 'Brother', 'Kaka', 't');
INSERT INTO public.relationship_type(code, name, name_other_lang, active) VALUES (5, 'Son', 'Mwana', 't');
INSERT INTO public.relationship_type(code, name, name_other_lang, active) VALUES (6, 'Daughter', 'Binti', 't');
INSERT INTO public.relationship_type(code, name, name_other_lang, active) VALUES (7, 'Grandmother', 'Bibi', 't');
INSERT INTO public.relationship_type(code, name, name_other_lang, active) VALUES (8, 'Grandfather', 'Babu', 't');
INSERT INTO public.relationship_type(code, name, name_other_lang, active) VALUES (9, 'Grandson', 'Mjukuu', 't');
INSERT INTO public.relationship_type(code, name, name_other_lang, active) VALUES (10, 'Granddaughter', 'Mjukuu', 't');
INSERT INTO public.relationship_type(code, name, name_other_lang, active) VALUES (11, 'Uncle', 'Mjomba', 't');
INSERT INTO public.relationship_type(code, name, name_other_lang, active) VALUES (12, 'Aunt', 'Shangazi', 't');
INSERT INTO public.relationship_type(code, name, name_other_lang, active) VALUES (13, 'Niece', 'Mpwa', 't');
INSERT INTO public.relationship_type(code, name, name_other_lang, active) VALUES (14, 'Nephew', 'Mpwa', 't');
INSERT INTO public.relationship_type(code, name, name_other_lang, active) VALUES (15, 'Other relatives', 'Ndugu wengine', 't');
INSERT INTO public.relationship_type(code, name, name_other_lang, active) VALUES (16, 'Partners', 'Washirika', 't');
INSERT INTO public.relationship_type(code, name, name_other_lang, active) VALUES (17, 'Other', 'Nyingine', 't');
INSERT INTO public.relationship_type(code, name, name_other_lang, active) VALUES (18, 'Spouses', 'Wanandoa', 't');
INSERT INTO public.relationship_type(code, name, name_other_lang, active) VALUES (19, 'Parents and children', 'Wazazi na watoto', 't');
INSERT INTO public.relationship_type(code, name, name_other_lang, active) VALUES (20, 'Siblings', 'Ndugu', 't');

ALTER TABLE public.natural_person ADD COLUMN id_number character varying(50);
ALTER TABLE public.natural_person ADD COLUMN id_type integer;
ALTER TABLE public.natural_person ADD COLUMN dob date;
ALTER TABLE public.natural_person ADD COLUMN share character varying(100);
ALTER TABLE public.natural_person ADD COLUMN acquisition_type integer;
ALTER TABLE public.natural_person ADD CONSTRAINT fk_natural_person_id_type FOREIGN KEY (id_type) REFERENCES public.id_type (code) ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE public.natural_person ADD CONSTRAINT natural_person_fk_acquisition_type FOREIGN KEY (acquisition_type) REFERENCES public.acquisition_type (code) ON UPDATE NO ACTION ON DELETE NO ACTION;

COMMENT ON COLUMN public.natural_person.id_number IS 'Identification document number.';
COMMENT ON COLUMN public.natural_person.id_type IS 'Identification document type code.';
COMMENT ON COLUMN public.natural_person.dob IS 'Date of birth.';
COMMENT ON COLUMN public.natural_person.share IS 'Share size or share description in case of common ownership right';
COMMENT ON COLUMN public.natural_person.acquisition_type IS 'Acquisition type code. Mostly used for disputing parties';

ALTER TABLE public.spatialunit_personwithinterest ADD COLUMN dob date;
ALTER TABLE public.spatialunit_personwithinterest ADD COLUMN gender_id integer;
ALTER TABLE public.spatialunit_personwithinterest ADD COLUMN relationship_type integer;
ALTER TABLE public.spatialunit_personwithinterest ADD CONSTRAINT fk_poi_gender FOREIGN KEY (gender_id) REFERENCES public.gender (gender_id) ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE public.spatialunit_personwithinterest ADD CONSTRAINT fk_poi_relationship_type FOREIGN KEY (relationship_type) REFERENCES public.relationship_type (code) ON UPDATE NO ACTION ON DELETE NO ACTION;
COMMENT ON COLUMN public.spatialunit_personwithinterest.dob IS 'Date of birth.';
COMMENT ON COLUMN public.spatialunit_personwithinterest.gender_id IS 'Gender code.';
COMMENT ON COLUMN public.spatialunit_personwithinterest.relationship_type IS 'Relationship type code.';

-- Ownership

ALTER TABLE public.social_tenure_relationship ADD COLUMN relationship_type integer;
ALTER TABLE public.social_tenure_relationship ADD COLUMN cert_number character varying(30);
ALTER TABLE public.social_tenure_relationship ADD COLUMN juridical_area double precision;
ALTER TABLE public.social_tenure_relationship ADD COLUMN acquisition_type integer;
ALTER TABLE public.social_tenure_relationship ADD CONSTRAINT fk_social_tenure_relationship_type FOREIGN KEY (relationship_type) REFERENCES public.relationship_type (code) ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE public.social_tenure_relationship ADD CONSTRAINT fk_acquisition_type FOREIGN KEY (acquisition_type) REFERENCES public.acquisition_type (code) ON UPDATE NO ACTION ON DELETE NO ACTION;

COMMENT ON COLUMN public.social_tenure_relationship.relationship_type IS 'Relationship type code.';
COMMENT ON COLUMN public.social_tenure_relationship.cert_number IS 'Certificate number. Used for existing CCROs or other existing right type.';
COMMENT ON COLUMN public.social_tenure_relationship.tenure_duration IS 'Length of parcel occupancy.';
COMMENT ON COLUMN public.social_tenure_relationship.juridical_area IS 'Parcel area according to the legal documents. Used for existing CCROs or other existing right type.';
COMMENT ON COLUMN public.social_tenure_relationship.acquisition_type IS 'Acquisition type code.';

-- Project

ALTER TABLE public.project_area ADD COLUMN region_code character varying(20);
COMMENT ON COLUMN public.project_area.region_code IS 'Region code.';

UPDATE public.project_area SET region_code = '119IR';

-- DISPUTES

CREATE TABLE public.dispute_status
(
   code int NOT NULL, 
   name character varying(200) NOT NULL, 
   name_other_lang character varying(200), 
   active boolean NOT NULL DEFAULT 't', 
   CONSTRAINT dispute_status_pk_code PRIMARY KEY (code)
) 
WITH (
  OIDS = FALSE
)
;
COMMENT ON COLUMN public.dispute_status.code IS 'Dispute type code - primary key';
COMMENT ON COLUMN public.dispute_status.name IS 'Dispute type name';
COMMENT ON COLUMN public.dispute_status.name_other_lang IS 'Dispute type name in other language';
COMMENT ON COLUMN public.dispute_status.active IS 'Boolean flag indicating whether the record is active and should be displayed on the forms.';
COMMENT ON TABLE public.dispute_status
  IS 'Reference data table for Dispute types';

INSERT INTO public.dispute_status (code, name, name_other_lang, active) VALUES (1, 'Active', 'Kazi', 't');
INSERT INTO public.dispute_status (code, name, name_other_lang, active) VALUES (2, 'Resolved', 'Kutatuliwa', 't');

CREATE TABLE public.dispute_type
(
   code int NOT NULL, 
   name character varying(200) NOT NULL, 
   name_other_lang character varying(200), 
   active boolean NOT NULL DEFAULT 't', 
   CONSTRAINT dispute_type_pk_code PRIMARY KEY (code)
) 
WITH (
  OIDS = FALSE
)
;
COMMENT ON COLUMN public.dispute_type.code IS 'Dispute type code - primary key';
COMMENT ON COLUMN public.dispute_type.name IS 'Dispute type name';
COMMENT ON COLUMN public.dispute_type.name_other_lang IS 'Dispute type name in other language';
COMMENT ON COLUMN public.dispute_type.active IS 'Boolean flag indicating whether the record is active and should be displayed on the forms.';
COMMENT ON TABLE public.dispute_type
  IS 'Reference data table for Dispute types';

INSERT INTO public.dispute_type (code, name, name_other_lang, active) VALUES (1, 'Boundary', 'Mpaka', 't');
INSERT INTO public.dispute_type (code, name, name_other_lang, active) VALUES (2, 'Counter-claim (Inter-family)', 'Mgogoro baina ya familia', 't');
INSERT INTO public.dispute_type (code, name, name_other_lang, active) VALUES (3, 'Counter-claim (Intra-family)', 'Mgogoro ndani ya familia', 't');
INSERT INTO public.dispute_type (code, name, name_other_lang, active) VALUES (4, 'Counter-claim (Others)', 'Mgogoro baina ya watu tofauti', 't');
INSERT INTO public.dispute_type (code, name, name_other_lang, active) VALUES (5, 'Other interests', 'Maslahi mengine', 't');

CREATE TABLE public.dispute
(
   id bigint NOT NULL, 
   usin bigint NOT NULL,
   dispute_type int NOT NULL, 
   description character varying(500), 
   reg_date date,
   resolution_text text,
   resolution_date date,
   status int NOT NULL DEFAULT 1, 
   deleted boolean DEFAULT true,
   CONSTRAINT dispute_pk_id PRIMARY KEY (id), 
   CONSTRAINT dispute_fk_spatial_unit FOREIGN KEY (usin) REFERENCES public.spatial_unit (usin) ON UPDATE NO ACTION ON DELETE CASCADE, 
   CONSTRAINT dispute_fk_dispute_type FOREIGN KEY (dispute_type) REFERENCES public.dispute_type (code) ON UPDATE NO ACTION ON DELETE NO ACTION, 
   CONSTRAINT dispute_fk_status FOREIGN KEY (status) REFERENCES public.dispute_status (code) ON UPDATE NO ACTION ON DELETE NO ACTION
) 
WITH (
  OIDS = FALSE
)
;
COMMENT ON COLUMN public.dispute.id IS 'Primary key';
COMMENT ON COLUMN public.dispute.usin IS 'Parcel ID';
COMMENT ON COLUMN public.dispute.dispute_type IS 'Dispute type code';
COMMENT ON COLUMN public.dispute.description IS 'Dispute description';
COMMENT ON COLUMN public.dispute.reg_date IS 'Date when dispute was registered';
COMMENT ON COLUMN public.dispute.resolution_text IS 'Dispute resolution text';
COMMENT ON COLUMN public.dispute.resolution_date IS 'Date when dispute was resolved';
COMMENT ON COLUMN public.dispute.status IS 'Dispute status code';
COMMENT ON COLUMN public.dispute.deleted IS 'Indication whether record is deleted or not';

CREATE SEQUENCE public.dispute_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 6741
  CACHE 1;
ALTER TABLE public.dispute_id_seq
  OWNER TO postgres;
  
CREATE TABLE public.dispute_person
(
   dispute_id bigint NOT NULL, 
   person_id bigint NOT NULL, 
   CONSTRAINT dispute_person_pk_id PRIMARY KEY (dispute_id, person_id), 
   CONSTRAINT dispute_person_fk_dispute FOREIGN KEY (dispute_id) REFERENCES public.dispute (id) ON UPDATE NO ACTION ON DELETE CASCADE, 
   CONSTRAINT dispute_person_fk_person FOREIGN KEY (person_id) REFERENCES public.natural_person (gid) ON UPDATE NO ACTION ON DELETE CASCADE
) 
WITH (
  OIDS = FALSE
)
;
COMMENT ON COLUMN public.dispute_person.dispute_id IS 'Dispute id';
COMMENT ON COLUMN public.dispute_person.person_id IS 'Person id';

-- Document type

CREATE TABLE public.document_type
(
   code integer NOT NULL, 
   name character varying(200) NOT NULL, 
   name_other_lang character varying(200), 
   active boolean NOT NULL DEFAULT 't', 
   CONSTRAINT document_type_pk_code PRIMARY KEY (code)
) 
WITH (
  OIDS = FALSE
)
;
COMMENT ON COLUMN public.document_type.code IS 'Document type code - primary key';
COMMENT ON COLUMN public.document_type.name IS 'Document type name';
COMMENT ON COLUMN public.document_type.name_other_lang IS 'Document type name in other language';
COMMENT ON COLUMN public.document_type.active IS 'Boolean flag indicating whether the record is active and should be displayed on the forms.';
COMMENT ON TABLE public.document_type
  IS 'Reference data table for Document types';

INSERT INTO public.document_type (code, name, name_other_lang, active) VALUES (1, 'Informal Receipt of Sale', 'Yasiyo rasmi ya kupokea kuuza', 't');
INSERT INTO public.document_type (code, name, name_other_lang, active) VALUES (2, 'Formal Receipt of Sale', 'Rasmi ya kupokea kuuza', 't');
INSERT INTO public.document_type (code, name, name_other_lang, active) VALUES (3, 'Letter of Allocation', 'Barua ya mgao', 't');
INSERT INTO public.document_type (code, name, name_other_lang, active) VALUES (4, 'Probate Document', 'Hati probate', 't');
INSERT INTO public.document_type (code, name, name_other_lang, active) VALUES (5, 'Other', 'Nyingine', 't');

-- Document

ALTER TABLE public.source_document ADD COLUMN document_type integer;
ALTER TABLE public.source_document ADD COLUMN dispute_id bigint;
ALTER TABLE public.source_document ADD CONSTRAINT fk_document_document_type FOREIGN KEY (document_type) REFERENCES public.document_type (code) ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE public.source_document ADD CONSTRAINT fk_document_dispute FOREIGN KEY (dispute_id) REFERENCES public.dispute (id) ON UPDATE NO ACTION ON DELETE NO ACTION;
COMMENT ON COLUMN public.source_document.document_type IS 'Document type code.';
COMMENT ON COLUMN public.source_document.dispute_id IS 'Dispute id.';

-- Master attributes
UPDATE public.attribute_master SET alias = 'Length of occupancy (years)', alias_second_language = 'Urefu wa kumiliki ardhi (miaka)' WHERE id = 13;

INSERT INTO public.attribute_master(id, alias, alias_second_language, fieldname, datatype_id, attributecategoryid, reftable, size, mandatory, listing, active, master_attrib)
VALUES (300, 'Acquisition Type', 'Upatikanaji aina', 'acquisition_type', 5, 4, 'social_tenure_relationship', 20, true, 3, true, true);

INSERT INTO public.attribute_master(id, alias, alias_second_language, fieldname, datatype_id, attributecategoryid, reftable, size, mandatory, listing, active, master_attrib)
VALUES (310, 'ID number', 'Idadi ID', 'id_number', 1, 2, 'natural_person', 50, true, 90, true, true);

INSERT INTO public.attribute_master(id, alias, alias_second_language, fieldname, datatype_id, attributecategoryid, reftable, size, mandatory, listing, active, master_attrib)
VALUES (320, 'ID type', 'Aina ID', 'id_type', 5, 2, 'natural_person', 20, true, 80, true, true);

INSERT INTO public.attribute_master(id, alias, alias_second_language, fieldname, datatype_id, attributecategoryid, reftable, size, mandatory, listing, active, master_attrib)
VALUES (330, 'Date of birth', 'Tarehe Ya Ku Zaliwa', 'dob', 2, 2, 'natural_person', 10, true, 60, true, true);

INSERT INTO public.attribute_master(id, alias, alias_second_language, fieldname, datatype_id, attributecategoryid, reftable, size, mandatory, listing, active, master_attrib)
VALUES (340, 'Document type', 'Aina hati', 'document_type', 5, 3, 'source_document', 20, false, 20, true, true);

UPDATE public.attribute_master SET active = false WHERE id in (264,265,266,267,31,23,24);
UPDATE public.attribute_master SET listing = 10 WHERE id = 16; 
UPDATE public.attribute_master SET listing = 20 WHERE id = 9;
UPDATE public.attribute_master SET listing = 30 WHERE id = 44;
UPDATE public.attribute_master SET listing = 40 WHERE id = 45;
UPDATE public.attribute_master SET listing = 50 WHERE id = 46;
UPDATE public.attribute_master SET listing = 60 WHERE id = 47;
UPDATE public.attribute_master SET listing = 70 WHERE id = 50;
UPDATE public.attribute_master SET listing = 80 WHERE id = 51;
UPDATE public.attribute_master SET listing = 25 WHERE id = 37;
UPDATE public.attribute_master SET listing = 26 WHERE id = 39;
UPDATE public.attribute_master SET listing = 27 WHERE id = 38;
update public.attribute_master set listing = 10 where id = 1;
update public.attribute_master set listing = 20 where id = 2;
update public.attribute_master set listing = 30 where id = 3;
update public.attribute_master set listing = 40 where id = 4;
update public.attribute_master set listing = 50 where id = 42;
update public.attribute_master set listing = 70 where id = 21;
update public.attribute_master set listing = 100 where id = 22;
update public.attribute_master set listing = 110 where id = 5;
update public.attribute_master set listing = 120 where id = 19;
update public.attribute_master set listing = 30 where id = 10;
update public.attribute_master set listing = 10 where id = 11;
update public.attribute_master set listing = 1, attributecategoryid=4 where id = 9;

-- Master attribute options
-- acquisition
INSERT INTO public.attribute_options(id, optiontext, attribute_id, optiontext_second_language, parent_id)
VALUES ((select max(id)+1 from public.attribute_options), 'Allocated by Village Council', 300, 'Zilizotengwa kwa Halmashauri ya Kijiji', 1);

INSERT INTO public.attribute_options(id, optiontext, attribute_id, optiontext_second_language, parent_id)
VALUES ((select max(id)+1 from public.attribute_options), 'Gift', 300, 'Kipawa', 2);

INSERT INTO public.attribute_options(id, optiontext, attribute_id, optiontext_second_language, parent_id)
VALUES ((select max(id)+1 from public.attribute_options), 'Inheritance', 300, 'Urithi', 3);

INSERT INTO public.attribute_options(id, optiontext, attribute_id, optiontext_second_language, parent_id)
VALUES ((select max(id)+1 from public.attribute_options), 'Purchase', 300, 'Ununuzi', 4);
-- id type
INSERT INTO public.attribute_options(id, optiontext, attribute_id, optiontext_second_language, parent_id)
VALUES ((select max(id)+1 from public.attribute_options), 'Voter ID', 320, 'ID wapiga kura', 1);

INSERT INTO public.attribute_options(id, optiontext, attribute_id, optiontext_second_language, parent_id)
VALUES ((select max(id)+1 from public.attribute_options), 'Driving license', 320, 'Leseni ya kuendesha gari', 2);

INSERT INTO public.attribute_options(id, optiontext, attribute_id, optiontext_second_language, parent_id)
VALUES ((select max(id)+1 from public.attribute_options), 'Passport', 320, 'Pasipoti', 3);

INSERT INTO public.attribute_options(id, optiontext, attribute_id, optiontext_second_language, parent_id)
VALUES ((select max(id)+1 from public.attribute_options), 'ID card', 320, 'Namba ya kitambulisho', 4);

INSERT INTO public.attribute_options(id, optiontext, attribute_id, optiontext_second_language, parent_id)
VALUES ((select max(id)+1 from public.attribute_options), 'Political affiliation card', 320, 'Political kadi uhusiano', 5);

-- document type
INSERT INTO public.attribute_options(id, optiontext, attribute_id, optiontext_second_language, parent_id)
VALUES ((select max(id)+1 from public.attribute_options), 'Informal Receipt of Sale', 340, 'Yasiyo rasmi ya kupokea kuuza', 1);

INSERT INTO public.attribute_options(id, optiontext, attribute_id, optiontext_second_language, parent_id)
VALUES ((select max(id)+1 from public.attribute_options), 'Formal Receipt of Sale', 340, 'Rasmi ya kupokea kuuza', 2);

INSERT INTO public.attribute_options(id, optiontext, attribute_id, optiontext_second_language, parent_id)
VALUES ((select max(id)+1 from public.attribute_options), 'Letter of Allocation', 340, 'Barua ya mgao', 3);

INSERT INTO public.attribute_options(id, optiontext, attribute_id, optiontext_second_language, parent_id)
VALUES ((select max(id)+1 from public.attribute_options), 'Probate Document', 340, 'Hati probate', 4);

INSERT INTO public.attribute_options(id, optiontext, attribute_id, optiontext_second_language, parent_id)
VALUES ((select max(id)+1 from public.attribute_options), 'Other', 340, 'Nyingine', 5);

-- land use
delete from public.attribute_options where attribute_id = 9 and parent_id not in (1,2,3,4,14);
delete from public.attribute_options where attribute_id = 16 and parent_id not in (1,2,3,4,14);

update public.attribute_options set optiontext = 'Forestry', optiontext_second_language = 'Misitu na hifadhi' where attribute_id = 9 and parent_id = 2;
update public.attribute_options set optiontext = 'Residential', optiontext_second_language = 'Makazi' where attribute_id = 9 and parent_id = 3;
update public.attribute_options set optiontext = 'Grazing', optiontext_second_language = 'Malisho' where attribute_id = 9 and parent_id = 4;

update public.attribute_options set optiontext = 'Forestry', optiontext_second_language = 'Misitu na hifadhi' where attribute_id = 16 and parent_id = 2;
update public.attribute_options set optiontext = 'Residential', optiontext_second_language = 'Makazi' where attribute_id = 16 and parent_id = 3;
update public.attribute_options set optiontext = 'Grazing', optiontext_second_language = 'Malisho' where attribute_id = 16 and parent_id = 4;

INSERT INTO public.attribute_options(id, optiontext, attribute_id, optiontext_second_language, parent_id)
VALUES ((select max(id)+1 from public.attribute_options), 'Residential and agricultural', 9, 'Makazi na kilimo', 15);

INSERT INTO public.attribute_options(id, optiontext, attribute_id, optiontext_second_language, parent_id)
VALUES ((select max(id)+1 from public.attribute_options), 'Wildlife/Tourism', 9, 'Hifadhi za wanyama na Utaliio', 16);

INSERT INTO public.attribute_options(id, optiontext, attribute_id, optiontext_second_language, parent_id)
VALUES ((select max(id)+1 from public.attribute_options), 'Social services', 9, 'Huduma za jamii', 17);

INSERT INTO public.attribute_options(id, optiontext, attribute_id, optiontext_second_language, parent_id)
VALUES ((select max(id)+1 from public.attribute_options), 'Mining', 9, 'Maeneo ya madini', 18);

INSERT INTO public.attribute_options(id, optiontext, attribute_id, optiontext_second_language, parent_id)
VALUES ((select max(id)+1 from public.attribute_options), 'Residential and agricultural', 16, 'Makazi na kilimo', 15);

INSERT INTO public.attribute_options(id, optiontext, attribute_id, optiontext_second_language, parent_id)
VALUES ((select max(id)+1 from public.attribute_options), 'Wildlife/Tourism', 16, 'Hifadhi za wanyama na Utaliio', 16);

INSERT INTO public.attribute_options(id, optiontext, attribute_id, optiontext_second_language, parent_id)
VALUES ((select max(id)+1 from public.attribute_options), 'Social services', 16, 'Huduma za jamii', 17);

INSERT INTO public.attribute_options(id, optiontext, attribute_id, optiontext_second_language, parent_id)
VALUES ((select max(id)+1 from public.attribute_options), 'Mining', 16, 'Maeneo ya madini', 18);



