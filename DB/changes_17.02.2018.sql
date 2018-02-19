ALTER TABLE public.project_area ADD COLUMN show_coatofarm boolean NOT NULL DEFAULT false;
COMMENT ON COLUMN public.project_area.show_coatofarm IS 'Boolean flag, indicating whether to show or hide coat of arm on the CCRO printing.';

ALTER TABLE public.spatial_unit ADD COLUMN witness_5 character varying(200);
COMMENT ON COLUMN public.spatial_unit.witness_5 IS 'Witness/trustee name';

ALTER TABLE public.social_tenure_relationship ADD COLUMN rental_fee double precision;
ALTER TABLE public.social_tenure_relationship ADD COLUMN term smallint;
COMMENT ON COLUMN public.social_tenure_relationship.rental_fee IS 'Rental fee for the parcel for non-residents';
COMMENT ON COLUMN public.social_tenure_relationship.term IS 'Term of using parcel in years.';

UPDATE public.attribute_master SET attributecategoryid = 4, alias = 'Witness 1 (for non-resident)', alias_second_language = 'Shahidi 1 (kwa asiye mkazi)', fieldname = 'witness_3' WHERE id = 50;
UPDATE public.attribute_master SET attributecategoryid = 4, alias = 'Witness 2 (for non-resident)', alias_second_language = 'Shahidi 2 (kwa asiye mkazi)', fieldname = 'witness_4' WHERE id = 51;
UPDATE public.attribute_master SET mandatory = 't' WHERE id = 5;

INSERT INTO public.attribute_master(id, alias, alias_second_language, fieldname, datatype_id, attributecategoryid, reftable, size, mandatory, listing, active, master_attrib)
VALUES (350, 'Witness 3 (for non-resident)', 'Shahidi 3 (kwa asiye mkazi)', 'witness_5', 1, 4, 'spatial_unit', 200, false, 90, true, true);

INSERT INTO public.attribute_master(id, alias, alias_second_language, fieldname, datatype_id, attributecategoryid, reftable, size, mandatory, listing, active, master_attrib)
VALUES (360, 'Term (for non-resident)', 'Kipindi (kwa asiye mkazi)', 'term', 4, 4, 'social_tenure_relationship', 5, false, 50, true, true);

INSERT INTO public.attribute_master(id, alias, alias_second_language, fieldname, datatype_id, attributecategoryid, reftable, size, mandatory, listing, active, master_attrib)
VALUES (370, 'Rental fee (for non-resident)', 'Malipo ya kila mwaka (kwa asiye mkazi)', 'rental_fee', 4, 4, 'social_tenure_relationship', 5, false, 60, true, true);

DROP VIEW public.view_claims;

CREATE OR REPLACE VIEW public.view_claims AS 
 SELECT su.usin,
    su.usin_str,
    su.uka_propertyno AS uka,
    round((st_area(st_transform(su.the_geom, 32736)) * 0.000247105::double precision)::numeric, 3) AS acres,
    h.hamlet_name_second_language AS hamlet_name,
    ux.land_use_type_sw AS existing_use,
    up.land_use_type_sw AS proposed_use,
    lt.landtype_value_sw AS land_type,
    su.neighbor_north,
    su.neighbor_south,
    su.neighbor_east,
    su.neighbor_west,
    su.witness_1 AS adjudicator1,
    get_adjudicator_signature(su.project_name, su.witness_1) AS adjudicator1_signature,
    su.witness_2 AS adjudicator2,
    get_adjudicator_signature(su.project_name, su.witness_2) AS adjudicator2_signature,
    su.witness_3 AS witness1,
    su.witness_4 AS witness2,
    su.witness_5 AS witness3,
    su.survey_date AS application_date,
    su.current_workflow_status_id AS status_id,
    su.project_name,
    su.claim_type,
    su.workflow_status_update_time AS status_date,
    sur.tenure_class,
    sur.ownership_type,
    sur.ownership_type_id,
    sur.duration,
    sur.cert_date,
    sur.cert_number,
    sur.acquisition_type,
    sur.file_number,
    sur.relationship_type,
    u.name AS recorder
   FROM spatial_unit su
     LEFT JOIN project_hamlets h ON su.hamlet_id = h.id
     LEFT JOIN land_use_type ux ON su.existing_use = ux.use_type_id
     LEFT JOIN land_use_type up ON su.proposed_use = up.use_type_id
     LEFT JOIN land_type lt ON su.type_name::integer = lt.landtype_id
     LEFT JOIN users u ON su.userid = u.id
     LEFT JOIN ( SELECT DISTINCT ON (r.usin) r.usin,
            t.tenure_class,
            sh.share_type_sw AS ownership_type,
            sh.gid AS ownership_type_id,
            r.tenure_duration AS duration,
            r.ccro_issue_date AS cert_date,
            r.cert_number,
            a.name_other_lang AS acquisition_type,
            r.file_number,
            rt.name_other_lang AS relationship_type
           FROM social_tenure_relationship r
             LEFT JOIN acquisition_type a ON r.acquisition_type = a.code
             LEFT JOIN relationship_type rt ON r.relationship_type = rt.code
             LEFT JOIN share_type sh ON r.share = sh.gid
             LEFT JOIN tenure_class t ON r.tenureclass_id = t.tenureclass_id
          WHERE r.isactive) sur ON su.usin = sur.usin
  WHERE su.active
  ORDER BY h.hamlet_name_second_language;

DROP VIEW public.view_natural_persons_with_right;

CREATE OR REPLACE VIEW public.view_natural_persons_with_right AS 
 SELECT p.gid,
    p.active AS is_active,
    r.usin,
    btrim(p.first_name::text) AS first_name,
    p.last_name,
    p.middle_name,
    pt.person_type_sw AS person_type,
    pt.person_type_gid AS person_type_id,
    g.gender_sw AS gender,
    ms.maritalstatus_sw AS marital_status,
    c.citizenname_sw AS citizenship,
    p.mobile,
    p.id_number,
    p.share,
    r.rental_fee,
    r.term,
    it.name_other_lang AS id_type,
        CASE
            WHEN p.dob IS NULL THEN p.age::double precision
            ELSE date_part('year'::text, now()) - date_part('year'::text, p.dob)
        END::integer AS age,
        CASE
            WHEN p.resident_of_village THEN 'Ndiyo'::text
            ELSE 'Hapana'::text
        END AS resident,
    COALESCE(p.resident_of_village, true) AS village_resident
   FROM social_tenure_relationship r
     JOIN (natural_person p
     LEFT JOIN person_type pt ON p.personsub_type = pt.person_type_gid
     LEFT JOIN gender g ON p.gender = g.gender_id
     LEFT JOIN marital_status ms ON p.marital_status = ms.maritalstatus_id
     LEFT JOIN citizenship c ON p.citizenship_id = c.id
     LEFT JOIN id_type it ON p.id_type = it.code) ON r.person_gid = p.gid
  WHERE r.isactive AND p.active
  ORDER BY pt.person_type_gid, (btrim(p.first_name::text));


