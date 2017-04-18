package com.rmsi.mast.studio.domain.fetch;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;

/**
 * Returns claim summary from the view
 */
@Entity
@Table(name = "view_claims")
public class ClaimSummary implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private long usin;

    @Column(name = "usin_str")
    private String usinStr;

    @Column
    private String uka;

    @Column
    private Double acres;

    @Column(name = "hamlet_name")
    private String hamletName;

    @Column(name = "existing_use")
    private String existingUse;

    @Column(name = "proposed_use")
    private String proposedUse;

    @Column(name = "land_type")
    private String landType;

    @Column(name = "neighbor_north")
    private String neighborNorth;

    @Column(name = "neighbor_south")
    private String neighborSouth;

    @Column(name = "neighbor_east")
    private String neighborEast;

    @Column(name = "neighbor_west")
    private String neighborWest;

    @Column
    private String adjudicator1;

    @Column
    private String adjudicator2;

    @Column(name = "application_date")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date applicationDate;

    @Column(name = "status_id")
    private Integer statusId;

    @Column(name = "project_name")
    private String projectName;

    @Column(name = "claim_type")
    private String claimType;

    @Column(name = "tenure_class")
    private String tenureClass;

    @Column(name = "ownership_type")
    private String ownershipType;

    @Column
    private Double duration;

    @Column(name = "cert_date")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date certDate;

    @Column(name = "cert_number")
    private String certNumber;

    @Column(name = "acquisition_type")
    private String acquisitionType;

    @Column(name = "file_number")
    private String fileNumber;

    @Column(name = "relationship_type")
    private String relationshipType;

    @Column
    private String recorder;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name="usin")
    List<PersonWithRightSummary> naturalOwners;
    
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name="usin")
    List<InstitutionSummary> nonNaturalOwners;
    
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name="usin")
    List<Poi> pois;
    
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name="usin")
    List<SpatialunitDeceasedPerson> deceasedPersons;
    
    public ClaimSummary() {
    }

    public long getUsin() {
        return usin;
    }

    public void setUsin(long usin) {
        this.usin = usin;
    }
    
    public String getUka() {
        return uka;
    }

    public void setUka(String uka) {
        this.uka = uka;
    }

    public Double getAcres() {
        return acres;
    }

    public void setAcres(Double acres) {
        this.acres = acres;
    }

    public String getHamletName() {
        return hamletName;
    }

    public void setHamletName(String hamletName) {
        this.hamletName = hamletName;
    }

    public String getExistingUse() {
        return existingUse;
    }

    public void setExistingUse(String existingUse) {
        this.existingUse = existingUse;
    }

    public String getProposedUse() {
        return proposedUse;
    }

    public void setProposedUse(String proposedUse) {
        this.proposedUse = proposedUse;
    }

    public String getLandType() {
        return landType;
    }

    public void setLandType(String landType) {
        this.landType = landType;
    }

    public String getNeighborNorth() {
        return neighborNorth;
    }

    public void setNeighborNorth(String neighborNorth) {
        this.neighborNorth = neighborNorth;
    }

    public String getNeighborSouth() {
        return neighborSouth;
    }

    public void setNeighborSouth(String neighborSouth) {
        this.neighborSouth = neighborSouth;
    }

    public String getNeighborEast() {
        return neighborEast;
    }

    public void setNeighborEast(String neighborEast) {
        this.neighborEast = neighborEast;
    }

    public String getNeighborWest() {
        return neighborWest;
    }

    public void setNeighborWest(String neighborWest) {
        this.neighborWest = neighborWest;
    }

    public String getAdjudicator1() {
        return adjudicator1;
    }

    public void setAdjudicator1(String adjudicator1) {
        this.adjudicator1 = adjudicator1;
    }

    public String getAdjudicator2() {
        return adjudicator2;
    }

    public void setAdjudicator2(String adjudicator2) {
        this.adjudicator2 = adjudicator2;
    }

    public Date getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(Date applicationDate) {
        this.applicationDate = applicationDate;
    }

    public String getUsinStr() {
        return usinStr;
    }

    public void setUsinStr(String usinStr) {
        this.usinStr = usinStr;
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getClaimType() {
        return claimType;
    }

    public void setClaimType(String claimType) {
        this.claimType = claimType;
    }

    public String getTenureClass() {
        return tenureClass;
    }

    public void setTenureClass(String tenureClass) {
        this.tenureClass = tenureClass;
    }

    public String getOwnershipType() {
        return ownershipType;
    }

    public void setOwnershipType(String ownershipType) {
        this.ownershipType = ownershipType;
    }

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }

    public Date getCertDate() {
        return certDate;
    }

    public void setCertDate(Date certDate) {
        this.certDate = certDate;
    }

    public String getCertNumber() {
        return certNumber;
    }

    public void setCertNumber(String certNumber) {
        this.certNumber = certNumber;
    }

    public String getAcquisitionType() {
        return acquisitionType;
    }

    public void setAcquisitionType(String acquisitionType) {
        this.acquisitionType = acquisitionType;
    }

    public String getFileNumber() {
        return fileNumber;
    }

    public void setFileNumber(String fileNumber) {
        this.fileNumber = fileNumber;
    }

    public String getRelationshipType() {
        return relationshipType;
    }

    public void setRelationshipType(String relationshipType) {
        this.relationshipType = relationshipType;
    }

    public String getRecorder() {
        return recorder;
    }

    public void setRecorder(String recorder) {
        this.recorder = recorder;
    }

    public List<PersonWithRightSummary> getNaturalOwners() {
        return naturalOwners;
    }
   
    public void setNaturalOwners(List<PersonWithRightSummary> naturalOwners) {
        this.naturalOwners = naturalOwners;
    }

    public List<InstitutionSummary> getNonNaturalOwners() {
        return nonNaturalOwners;
    }

    public void setNonNaturalOwners(List<InstitutionSummary> nonNaturalOwners) {
        this.nonNaturalOwners = nonNaturalOwners;
    }

    public List<Poi> getPois() {
        return pois;
    }
    
    public void setPois(List<Poi> pois) {
        this.pois = pois;
    }

    public List<SpatialunitDeceasedPerson> getDeceasedPersons() {
        return deceasedPersons;
    }
    
    public void setDeceasedPersons(List<SpatialunitDeceasedPerson> deceasedPersons) {
        this.deceasedPersons = deceasedPersons;
    }
}
