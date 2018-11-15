ALTER TABLE public.project_area ADD COLUMN va_meeting_date date;
COMMENT ON COLUMN public.project_area.va_meeting_date IS 'Village assembly meeting date. The date when CCRO requests were prepared (decided for adjudication).';

UPDATE public.project_area SET va_meeting_date = vc_meeting_date;