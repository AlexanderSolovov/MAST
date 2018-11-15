
CREATE OR REPLACE FUNCTION public.f_for_trg_generate_ccro()
  RETURNS trigger AS
$BODY$
DECLARE 
  ccro_number bigint;
  village_code character varying(10);

BEGIN
   IF (NEW.current_workflow_status_id = 2 AND NEW.claim_type in ('newClaim','existingClaim') AND EXISTS(SELECT 1 FROM public.social_tenure_relationship WHERE (cert_number IS NULL OR cert_number = '') AND usin = NEW.usin AND isactive = 't')) THEN

      SELECT upper(pa.village_code) INTO village_code FROM public.project_area pa WHERE pa.name = NEW.project_name LIMIT 1;
      
      IF(village_code IS NULL) THEN
	RAISE EXCEPTION 'Village code must be assigned in the project settings';
      ELSE
        SELECT nextval('ccro_number_seq') INTO ccro_number;
        UPDATE public.social_tenure_relationship SET cert_number = village_code || '/' || ccro_number, ccro_issue_date = now(), file_number = 'IRD/HW/' || ccro_number 
	WHERE (cert_number IS NULL OR cert_number = '') AND usin = NEW.usin AND isactive = 't';
      END IF;
      
   END IF; 
   RETURN NEW;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION public.f_for_trg_generate_ccro()
  OWNER TO postgres;
COMMENT ON FUNCTION public.f_for_trg_generate_ccro() IS 'This function generates CCRO and file number upon approval of the new claim.';
