DROP VIEW public.view_claims;

CREATE OR REPLACE VIEW public.view_claims AS 
 SELECT su.usin,
    su.usin_str,
    su.uka_propertyno AS uka,
    round((st_area(st_transform(su.the_geom, 32736)) * 0.000247105::double precision)::numeric, 3) AS acres,
    (case when st_area(st_transform(su.the_geom, 32736)) > 1000 then 
      'ekari ' || cast(round((st_area(st_transform(su.the_geom, 32736)) * 0.000247105::double precision)::numeric, 3) as varchar(30)) else
      'mita za mraba ' || cast(round((st_area(st_transform(su.the_geom, 32736)))::numeric) as varchar(30)) end) AS area,
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

ALTER TABLE public.view_claims
  OWNER TO postgres;

