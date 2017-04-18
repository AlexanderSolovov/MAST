package com.rmsi.mast.viewer.report;

import com.rmsi.mast.studio.domain.NaturalPerson;
import com.rmsi.mast.studio.domain.NonNaturalPerson;
import com.rmsi.mast.studio.domain.PersonType;
import com.rmsi.mast.studio.domain.Project;
import com.rmsi.mast.studio.domain.SocialTenureRelationship;
import com.rmsi.mast.studio.domain.Status;
import com.rmsi.mast.studio.domain.fetch.ClaimSummary;
import com.rmsi.mast.studio.domain.fetch.ProjectDetails;
import com.rmsi.mast.studio.domain.fetch.SpatialUnitTable;
import com.rmsi.mast.studio.service.ProjectService;
import com.rmsi.mast.studio.util.StringUtils;
import com.rmsi.mast.viewer.service.LandRecordsService;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanArrayDataSource;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This class contains methods to generate various reports
 */
@Service
public class ReportsServiceImpl implements ReportsSerivce {

    @Autowired
    private LandRecordsService landRecordsService;
    
    private static final Logger logger = Logger.getLogger(ReportsServiceImpl.class.getName());

    public ReportsServiceImpl() {
    }

    /**
     * Returns denial letter for claim
     *
     * @param usin Claim USIN number
     * @return
     */
    @Override
    public JasperPrint getDenialLetter(long usin) {
        try {
            List<SocialTenureRelationship> rights = landRecordsService.findAllSocialTenureByUsin(usin);
            SpatialUnitTable claim = landRecordsService.getSpatialUnit(usin);

            if (claim == null || rights == null || rights.size() < 1 || rights.get(0).getPerson_gid() == null) {
                return null;
            }

            ProjectDetails project = landRecordsService.getProjectDetails(claim.getProject());
            String village = project.getVillage();
            String hamlet = claim.getHamlet_Id().getHamletName();
            String claimantName;

            if (rights.get(0).getPerson_gid().getPerson_type_gid().getPerson_type_gid() == PersonType.TYPE_NATURAL) {
                claimantName = getPersonName((NaturalPerson) rights.get(0).getPerson_gid());
            } else {
                NonNaturalPerson nonPerson = (NonNaturalPerson) rights.get(0).getPerson_gid();
                if (nonPerson.getPoc_gid() != null && nonPerson.getPoc_gid() > 0) {
                    claimantName = getPersonName((NaturalPerson) landRecordsService.findPersonGidById(nonPerson.getPoc_gid()));
                } else {
                    claimantName = StringUtils.empty(nonPerson.getInstitutionName());
                }
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            HashMap params = new HashMap();
            params.put("CLAIMANT_NAME", claimantName);
            params.put("VILLAGE", village);
            params.put("HAMLET", hamlet);
            params.put("CLAIM_NUMBER", StringUtils.empty(claim.getClaimNumber()));
            params.put("CLAIM_DATE", dateFormat.format(claim.getSurveyDate()));

            Object[] beans = new Object[1];
            beans[0] = new Object();
            JRDataSource jds = new JRBeanArrayDataSource(beans);

            return JasperFillManager.fillReport(
                    ReportsServiceImpl.class.getResourceAsStream("/reports/DenialLetter.jasper"),
                    params, jds);
        } catch (Exception ex) {
            logger.error(ex);
            return null;
        }
    }

    @Override
    public JasperPrint getAdjudicationForms(String projectName, long startUsin, long endUsin, String appUrl) {
        try {
            ProjectDetails project = landRecordsService.getProjectDetails(projectName);
            int statusId = Status.STATUS_VALIDATED;
            if(startUsin == endUsin){
                // Any status if only 1 usin provided
                statusId = 0;
            }
            
            List<ClaimSummary> claims = landRecordsService.getClaimsForAdjudicationForms(startUsin, endUsin, statusId, projectName);

            if (project == null || claims == null || claims.size() < 1) {
                return null;
            }

            // Order claims by first person name
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String vcDate = "";
            if(project.getVcMeetingDate() != null){
                vcDate = dateFormat.format(project.getVcMeetingDate());
            }
            
            HashMap params = new HashMap();
            params.put("VILLAGE", project.getVillage());
            params.put("APP_URL", appUrl);
            params.put("VC_DATE", vcDate);

            ClaimSummary[] beans = claims.toArray(new ClaimSummary[claims.size()]);
            JRDataSource jds = new JRBeanArrayDataSource(beans);

            return JasperFillManager.fillReport(
                    ReportsServiceImpl.class.getResourceAsStream("/reports/AdjudicationForm.jasper"),
                    params, jds);
        } catch (Exception ex) {
            logger.error(ex);
            return null;
        }
    }

    private String getPersonName(NaturalPerson person) {
        String name = "";
        if (!StringUtils.isEmpty(person.getFirstName())) {
            name = person.getFirstName();
        }
        if (!StringUtils.isEmpty(person.getMiddleName())) {
            if (name.length() > 0) {
                name = name + " " + person.getMiddleName();
            } else {
                name = person.getMiddleName();
            }
        }
        if (!StringUtils.isEmpty(person.getLastName())) {
            if (name.length() > 0) {
                name = name + " " + person.getLastName();
            } else {
                name = person.getLastName();
            }
        }
        return name;
    }
}
