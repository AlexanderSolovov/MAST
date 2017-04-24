package com.rmsi.mast.viewer.dao;

import java.util.List;

import com.rmsi.mast.studio.dao.GenericDAO;
import com.rmsi.mast.studio.domain.fetch.ClaimSummary;
import com.rmsi.mast.studio.domain.fetch.ProjectDetails;
import com.rmsi.mast.studio.domain.fetch.RegistryBook;
import com.rmsi.mast.studio.domain.fetch.SpatialUnitGeom;
import com.rmsi.mast.studio.domain.fetch.SpatialUnitTable;

public interface LandRecordsDao extends GenericDAO<SpatialUnitTable, Long> {

    List<SpatialUnitTable> findallspatialUnit(String defaultProject);

    boolean updateApprove(Long id);

    boolean rejectStatus(Long id);

    List<SpatialUnitTable> search(String usinStr, String ukaNumber, String projname, String dateto, String datefrom,
            Long status, String claimType, Integer startpos);

    List<SpatialUnitTable> findSpatialUnitById(Long id);

    String findBiggestUkaNumber(String ukaPrefix);

    SpatialUnitGeom getParcelGeometry(long usin);

    SpatialUnitTable getSpatialUnit(Long id);

    String findukaNumberByUsin(Long id);

    boolean updateFinal(Long id);

    boolean updateAdjudicated(Long id);

    boolean deleteSpatial(Long id);

    Integer searchSize(String usinStr, String ukaNumber, String projname,
            String dateto, String datefrom, Long status, String claimType);

    List<SpatialUnitTable> getSpatialUnitByBbox(String bbox, String project_name);

    boolean findExistingHamlet(long hamlet_id);

    boolean deleteAllVertexLabel();

    boolean addAllVertexLabel(int k, String lat, String lon);

    /** Returns list of claim summary based on usin, project name, status, results rage, and claim type */
    List<ClaimSummary> getClaimsSummary(Long usin, int startRecord, int endRecord, String projectName, int statusId, String claimType);
    
    List<RegistryBook> getRegistryBook(String projectName, long usin);
    
    ProjectDetails getProjectDetails(String projectName);
}
