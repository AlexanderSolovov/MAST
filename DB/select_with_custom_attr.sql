--CREATE EXTENSION tablefunc;

-- Parcel
SELECT su.usin, su.the_geom, parcel_custom.parcel_no, parcel_custom.claim_date
FROM spatial_unit su LEFT JOIN 
crosstab(
	'select a.parentuid, sp.uid, a.value
	from surveyprojectattributes sp inner join attribute a on sp.uid = a.uid
	where sp.uid in (830,831)
	order by 1,2', $$VALUES (830::character varying(3000)), (831::character varying(3000))$$)
AS parcel_custom(usin bigint, parcel_no character varying(3000), claim_date character varying(3000))
ON su.usin = parcel_custom.usin
WHERE su.project_name = 'Kinywangaanga';


-- Person
SELECT np.gid, np.first_name, np.last_name, person_custom.id_card, person_custom.date_of_birth
FROM natural_person np LEFT JOIN 
crosstab(
	'select a.parentuid, sp.uid, a.value
	from surveyprojectattributes sp inner join attribute a on sp.uid = a.uid
	where sp.uid in (832,833)
	order by 1,2', $$VALUES (832::character varying(3000)), (833::character varying(3000))$$)
AS person_custom(gid bigint, id_card character varying(3000), date_of_birth character varying(3000))
ON np.gid = person_custom.gid
WHERE np.gid IN (select person_gid from social_tenure_relationship t inner join spatial_unit u on t.usin = u.usin where u.project_name = 'Kinywangaanga');
