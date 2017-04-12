package com.rmsi.mast.viewer.dao.hibernate;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.rmsi.mast.studio.dao.hibernate.GenericHibernateDAO;
import com.rmsi.mast.studio.domain.Status;
import com.rmsi.mast.studio.domain.fetch.SpatialUnitTable;
import com.rmsi.mast.viewer.dao.LandRecordsDao;

@Repository
public class LandRecordsHibernateDAO extends GenericHibernateDAO<SpatialUnitTable, Long>
        implements LandRecordsDao {

    private static final Logger logger = Logger.getLogger(LandRecordsHibernateDAO.class);

    @SuppressWarnings("unchecked")
    @Override
    public List<SpatialUnitTable> findallspatialUnit(String defaultProject) {

        try {
            Query query = getEntityManager().createQuery("Select su from SpatialUnitTable su where su.project = :project_name and su.active=true order by su.usin desc");
            List<SpatialUnitTable> spatialUnit = query.setParameter("project_name", defaultProject).getResultList();

            if (spatialUnit.size() > 0) {
                return spatialUnit;
            } else {
                return null;
            }
        } catch (Exception e) {

            logger.error(e);
            return null;
        }

    }

    @Override
    public boolean updateApprove(Long id) {

        try {
            Query query = getEntityManager().createQuery("UPDATE SpatialUnitTable su SET su.status.workflowStatusId = :statusId  where su.usin = :usin");
            int updateFinal = query.setParameter("statusId", Status.STATUS_APPROVED).setParameter("usin", id).executeUpdate();

            if (updateFinal > 0) {
                return true;
            }

        } catch (Exception e) {

            logger.error(e);
            return false;
        }

        return false;

    }

    @Override
    public boolean rejectStatus(Long id) {

        try {
            int finalstatus = 5;
            Query query = getEntityManager().createQuery("UPDATE SpatialUnitTable su SET su.status.workflowStatusId = :statusId  where su.usin = :usin");
            int rejectStatus = query.setParameter("statusId", finalstatus).setParameter("usin", id).executeUpdate();

            if (rejectStatus > 0) {
                return true;
            }
        } catch (Exception e) {

            logger.error(e);
            return false;
        }

        return false;

    }

    @Override
    public List<SpatialUnitTable> search(String usinStr, String ukaNumber,
            String projname, String dateto, String datefrom,
            Long status, String claimType, Integer startpos) {

        ArrayList<Long> newUsin = new ArrayList<>();
        try {
            StringBuilder queryStr = new StringBuilder("Select su from SpatialUnitTable su where su.project = :project_name and su.active=true ");
            if (!"".equals(ukaNumber)) {
                queryStr.append("and su.propertyno like :propertyno ");
            }
            if (!"".equals(usinStr)) {
                queryStr.append("and su.usin in :usin ");
            }
            if (!dateto.isEmpty() || !datefrom.isEmpty()) {
                queryStr.append("and (str(su.surveyDate) BETWEEN :stDate AND :edDate) ");
            }
            if (claimType != null && claimType.length() > 0) {
                queryStr.append("and su.claimType.code=:claimType ");
            }
            if (status != 0) {
                queryStr.append("and su.status.workflowStatusId=:workflowStatusId ");
            }

            queryStr.append("order by su.usin desc ");

            Query query = getEntityManager().createQuery(queryStr.toString());
            query.setParameter("project_name", projname);

            if (!"".equals(ukaNumber)) {
                query.setParameter("propertyno", "%" + ukaNumber.trim() + "%");
            }
            if (!"".equals(usinStr)) {
                for (String retval : usinStr.split(",")) {
                    newUsin.add(Long.parseLong(retval.trim()));
                }
                query.setParameter("usin", newUsin);
            }
            if (!dateto.isEmpty() || !datefrom.isEmpty()) {
                query.setParameter("stDate", datefrom).setParameter("edDate", dateto);
            }
            if (status != 0) {
                query.setParameter("workflowStatusId", status.intValue());
            }
            if (claimType != null && claimType.length() > 0) {
                query.setParameter("claimType", claimType);
            }

            @SuppressWarnings("unchecked")
            List<SpatialUnitTable> spatialUnit = query.setFirstResult(startpos).setMaxResults(20).getResultList();
            if (spatialUnit.size() > 0) {
                return spatialUnit;
            } else {
                return null;
            }
        } catch (Exception e) {
            logger.error(e);
            return null;
        }
    }

    @Override
    public List<SpatialUnitTable> findSpatialUnitById(Long id) {
        try {
            Query query = getEntityManager().createQuery("Select su from SpatialUnitTable su where su.usin = :usin and su.active = true");
            @SuppressWarnings("unchecked")
            List<SpatialUnitTable> spatialUnitlst = query.setParameter("usin", id).getResultList();

            if (spatialUnitlst.size() > 0) {
                return spatialUnitlst;
            } else {
                return null;
            }
        } catch (Exception e) {
            logger.error(e);
            return null;
        }
    }
    
    @Override
    public String findBiggestUkaNumber(String ukaPrefix){
        try {
            Query query = getEntityManager().createQuery("Select max(su.propertyno) from SpatialUnitTable su where su.propertyno like :ukaPrefix");
            return (String)query.setParameter("ukaPrefix", ukaPrefix + "%").getSingleResult();
        } catch (Exception e) {
            logger.error(e);
            return null;
        }
    }

    @Override
    public SpatialUnitTable getSpatialUnit(Long id) {
        try {
            Query query = getEntityManager().createQuery("Select su from SpatialUnitTable su where su.usin = :usin and su.active = true");
            @SuppressWarnings("unchecked")
            List<SpatialUnitTable> spatialUnitlst = query.setParameter("usin", id).getResultList();

            if (spatialUnitlst.size() > 0) {
                return spatialUnitlst.get(0);
            } else {
                return null;
            }
        } catch (Exception e) {
            logger.error(e);
            return null;
        }
    }

    @Override
    public String findukaNumberByUsin(Long id) {

        try {
            Query query = getEntityManager().createQuery("Select su from SpatialUnitTable su where su.usin = :usin and su.active = true");
            @SuppressWarnings("unchecked")
            List<SpatialUnitTable> spatialUnitlst = query.setParameter("usin", id).getResultList();
            String uka = spatialUnitlst.get(0).getPropertyno();

            if (spatialUnitlst.size() > 0) {
                return uka;
            } else {
                return "";
            }
        } catch (Exception e) {

            logger.error(e);
            return "";
        }

    }

    @Override
    public boolean updateFinal(Long id) {

        try {
            int finalstatus = 7;
            Query query = getEntityManager().createQuery("UPDATE SpatialUnitTable su SET su.status.workflowStatusId = :statusId  where su.usin = :usin");
            int updateFinal = query.setParameter("statusId", finalstatus).setParameter("usin", id).executeUpdate();

            if (updateFinal > 0) {
                return true;
            }
        } catch (Exception e) {

            logger.error(e);
            return false;
        }

        return false;

    }

    @Override
    public boolean updateAdjudicated(Long id) {

        try {
            int finalstatus = 2;
            Query query = getEntityManager().createQuery("UPDATE SpatialUnitTable su SET su.status.workflowStatusId = :statusId  where su.usin = :usin");
            int updateFinal = query.setParameter("statusId", finalstatus).setParameter("usin", id).executeUpdate();

            if (updateFinal > 0) {
                return true;
            }
        } catch (Exception e) {

            logger.error(e);
            return false;
        }

        return false;

    }

    @Override
    public boolean deleteSpatial(Long id) {

        try {
            Query query = getEntityManager().createQuery("UPDATE SpatialUnitTable su SET su.active = false  where su.usin = :usin");
            int updateFinal = query.setParameter("usin", id).executeUpdate();

            if (updateFinal > 0) {
                return true;
            }
        } catch (Exception e) {

            logger.error(e);
            return false;
        }
        return false;
    }

    @Override
    public Integer searchSize(String usinStr, String ukaNumber,
            String projname, String dateto, String datefrom, Long status, String claimType) {

        Integer count = 0;

        ArrayList<Long> newUsin = new ArrayList<Long>();
        try {
            // and (str(su.surveyDate) BETWEEN :stDate AND :edDate) and su.project = :project_name ")
            StringBuffer queryStr = new StringBuffer("Select count(*) from SpatialUnitTable su where su.project = :project_name and su.active=true ");
            if (ukaNumber != "") {
                queryStr.append("and su.propertyno like :propertyno ");
            }
            if (usinStr != "") {

                queryStr.append("and su.usin in :usin ");

            }
            if (!dateto.isEmpty() || !datefrom.isEmpty()) {
                queryStr.append("and (str(su.surveyDate) BETWEEN :stDate AND :edDate) ");
            }
            if (status != 0) {
                queryStr.append("and su.status.workflowStatusId=:workflowStatusId ");
            }
            if (claimType != null && claimType.length() > 0) {
                queryStr.append("and su.claimType.code=:claimType ");
            }

            Query query = getEntityManager().createQuery(queryStr.toString());
            query.setParameter("project_name", projname);

            if (ukaNumber != "") {
                query.setParameter("propertyno", "%" + ukaNumber + "%");
            }
            if (usinStr != "") {
                for (String retval : usinStr.split(",")) {
                    newUsin.add(Long.parseLong(retval));
                }
                query.setParameter("usin", newUsin);
            }
            if (!dateto.isEmpty() || !datefrom.isEmpty()) {
                query.setParameter("stDate", datefrom).setParameter("edDate", dateto);
            }
            if (status != 0) {
                query.setParameter("workflowStatusId", status.intValue());
            }
            if (claimType != null && claimType.length() > 0) {
                query.setParameter("claimType", claimType);
            }

            @SuppressWarnings("unchecked")
            List<?> spatialUnit = query.getResultList();
            if (spatialUnit.size() > 0) {
                count = Integer.valueOf(spatialUnit.get(0).toString());
            }

        } catch (Exception e) {

            logger.error(e);

        }

        return count;

    }

    @Override
    public List<SpatialUnitTable> getSpatialUnitByBbox(String bbox, String project_name) {

        List<SpatialUnitTable> spatialUnit = new ArrayList<SpatialUnitTable>();
        try {

            Query query = getEntityManager().createNativeQuery("SELECT * from spatial_unit where ST_WITHIN(the_geom, ST_MakeEnvelope(" + bbox + ",4326)) and (project_name=" + "'" + project_name + "'" + " and active=true) ", SpatialUnitTable.class);
            spatialUnit = query.getResultList();
        } catch (Exception e) {
            logger.error(e);
        }

        return spatialUnit;

    }

    @Override
    public boolean findExistingHamlet(long hamlet_id) {
        try {
            Query query = getEntityManager().createQuery("Select su from SpatialUnitTable su where su.hamlet_Id.id = :hamlet_id");
            @SuppressWarnings("unchecked")
            List<SpatialUnitTable> spatialUnitlst = query.setParameter("hamlet_id", hamlet_id).getResultList();
            if (spatialUnitlst.size() > 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {

            logger.error(e);
            return false;
        }
    }

    @Override
    public boolean deleteAllVertexLabel() {

        try {
            Query query = getEntityManager().createNativeQuery("DELETE FROM vertexlabel");
            int spatialUnit = query.executeUpdate();

            if (spatialUnit > 0) {
                return true;
            } else {
                return true;
            }
        } catch (Exception e) {

            logger.error(e);
            return false;
        }
    }

    @Override
    public boolean addAllVertexLabel(int k, String lat, String lon) {
        try {
            Query query = getEntityManager().createNativeQuery("insert into vertexlabel(gid,the_geom) values(" + k + ",ST_SetSRID(ST_MakePoint(" + lon + "," + lat + "), 4326));");
            int spatialUnit = query.executeUpdate();

            if (spatialUnit > 0) {
                return true;
            } else {
                return true;
            }
        } catch (Exception e) {

            logger.error(e);
            return false;
        }
    }

}
