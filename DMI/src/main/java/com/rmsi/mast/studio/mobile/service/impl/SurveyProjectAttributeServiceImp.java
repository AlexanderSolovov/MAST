package com.rmsi.mast.studio.mobile.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.rmsi.mast.studio.dao.AttributeMasterDAO;
import com.rmsi.mast.studio.dao.DisputeDao;
import com.rmsi.mast.studio.dao.ProjectAdjudicatorDAO;
import com.rmsi.mast.studio.dao.ProjectHamletDAO;
import com.rmsi.mast.studio.domain.AttributeMaster;
import com.rmsi.mast.studio.domain.Dispute;
import com.rmsi.mast.studio.domain.NaturalPerson;
import com.rmsi.mast.studio.domain.NonNaturalPerson;
import com.rmsi.mast.studio.domain.Person;
import com.rmsi.mast.studio.domain.ProjectAdjudicator;
import com.rmsi.mast.studio.domain.ProjectHamlet;
import com.rmsi.mast.studio.domain.SocialTenureRelationship;
import com.rmsi.mast.studio.domain.SourceDocument;
import com.rmsi.mast.studio.domain.SpatialUnit;
import com.rmsi.mast.studio.domain.SpatialUnitPersonWithInterest;
import com.rmsi.mast.studio.domain.fetch.SpatialunitDeceasedPerson;
import com.rmsi.mast.studio.domain.fetch.SpatialunitPersonwithinterest;
import com.rmsi.mast.studio.mobile.dao.AttributeOptionsDao;
import com.rmsi.mast.studio.mobile.dao.AttributeValuesDao;
import com.rmsi.mast.studio.mobile.dao.NaturalPersonDao;
import com.rmsi.mast.studio.mobile.dao.NonNaturalPersonDao;
import com.rmsi.mast.studio.mobile.dao.PersonDao;
import com.rmsi.mast.studio.mobile.dao.SocialTenureDao;
import com.rmsi.mast.studio.mobile.dao.SourceDocumentDao;
import com.rmsi.mast.studio.mobile.dao.SurveyProjectAttributeDao;
import com.rmsi.mast.studio.mobile.dao.hibernate.SocialTenureHibernateDao;
import com.rmsi.mast.studio.mobile.dao.hibernate.SpatialUnitHibernateDao;
import com.rmsi.mast.studio.mobile.service.SurveyProjectAttributeService;
import com.rmsi.mast.studio.util.GeometryConversion;
import com.rmsi.mast.viewer.dao.SpatialUnitDeceasedPersonDao;
import com.rmsi.mast.studio.domain.Surveyprojectattribute;
import com.rmsi.mast.studio.mobile.dao.SpatialUnitPersonWithInterestDao;
import com.rmsi.mast.studio.mobile.transferobjects.Attribute;
import com.rmsi.mast.studio.mobile.transferobjects.DeceasedPerson;
import com.rmsi.mast.studio.mobile.transferobjects.Media;
import com.rmsi.mast.studio.mobile.transferobjects.PersonOfInterest;
import com.rmsi.mast.studio.mobile.transferobjects.Property;
import com.rmsi.mast.studio.mobile.transferobjects.Right;
import com.rmsi.mast.studio.util.StringUtils;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@Service
public class SurveyProjectAttributeServiceImp implements
        SurveyProjectAttributeService {

    @Autowired
    SurveyProjectAttributeDao attributes;

    @Autowired
    AttributeOptionsDao attributeOptions;

    @Autowired
    NaturalPersonDao naturalPersonDao;

    @Autowired
    SocialTenureDao socailTenure;

    @Autowired
    AttributeMasterDAO attributeMasterDao;

    @Autowired
    SpatialUnitHibernateDao spatialUnitiHibernateDao;

    @Autowired
    SocialTenureHibernateDao tenureDao;

    @Autowired
    PersonDao personDao;

    @Autowired
    AttributeValuesDao attributeValuesDao;

    @Autowired
    NonNaturalPersonDao nonNaturalPersonDao;

    @Autowired
    SourceDocumentDao sourceDocumentDao;

    @Autowired
    ProjectAdjudicatorDAO projectAdjudicatorDAO;

    @Autowired
    ProjectHamletDAO projectHamletDAO;

    @Autowired
    SpatialUnitPersonWithInterestDao spatialUnitPersonWithInterestDao;

    @Autowired
    SpatialUnitDeceasedPersonDao spatialUnitDeceasedPersonDao;

    @Autowired
    DisputeDao disputeDao;

    private static final Logger logger = Logger
            .getLogger(SurveyProjectAttributeServiceImp.class.getName());

    @Override
    public List<AttributeMaster> getSurveyAttributesByProjectId(String projectId) {

        List<AttributeMaster> attributeMasterList = attributes
                .getSurveyAttributes(projectId);
        try {
            Iterator<AttributeMaster> surveyProjectAttribItr = attributeMasterList
                    .iterator();

            while (surveyProjectAttribItr.hasNext()) {

                AttributeMaster attributeMaster = surveyProjectAttribItr.next();

                if (attributeMaster.getDatatypeIdBean().getDatatype()
                        .equalsIgnoreCase("dropdown")) {
                    attributeMaster.setAttributeOptions(attributeOptions
                            .getAttributeOptions(attributeMaster.getId()));
                }
            }

        } catch (Exception ex) {
            logger.error("Exception", ex);
            System.out.println("Exception ::: " + ex);
        }
        return attributeMasterList;

    }
    
    @Override
    public List<Property> getProperties(String projectId, int statusId) {
        List<Property> props = new ArrayList<>();
        List<SpatialUnit> spatialUnits;

        if (statusId > 0) {
            spatialUnits = (new GeometryConversion()
                    .converGeometryToString(spatialUnitiHibernateDao
                            .findSpatialUnitByStatusId(projectId, statusId)));
        } else {
            spatialUnits = (new GeometryConversion()
                    .converGeometryToString(spatialUnitiHibernateDao
                            .getSpatialUnitByProject(projectId)));
        }

        if (spatialUnits != null && spatialUnits.size() > 0) {
            SimpleDateFormat dfDateAndTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss a");
            SimpleDateFormat dfDate = new SimpleDateFormat("yyyy-MM-dd");

            for (SpatialUnit su : spatialUnits) {
                long usin = su.getUsin();

                Property prop = new Property();
                prop.setAdjudicator1(StringUtils.empty(su.getWitness1()));
                prop.setAdjudicator2(StringUtils.empty(su.getWitness2()));
                prop.setClaimTypeCode(su.getClaimType().getCode());
                prop.setCoordinates(StringUtils.empty(su.getGeometry()));
                if (su.getStatusUpdateTime() != null) {
                    prop.setCompletionDate(dfDateAndTime.format(su.getStatusUpdateTime()));
                }
                prop.setGeomType(su.getGtype());
                prop.setHamletId(su.getHamletId());
                prop.setId(usin);
                prop.setImei(StringUtils.empty(su.getImeiNumber()));
                prop.setUkaNumber(StringUtils.empty(su.getPropertyno()));
                prop.setPolygonNumber(StringUtils.empty(su.getPolygonNumber()));
                prop.setServerId(usin);
                prop.setStatus(su.getStatus().getWorkflowStatusId().toString());
                prop.setUserId(su.getUserid());
                if (su.getSurveyDate() != null) {
                    prop.setSurveyDate(dfDate.format(su.getSurveyDate()));
                }

                // Property attributes
                prop.setAttributes(new ArrayList<Attribute>());
                fillAttributes(prop.getAttributes(), usin, 1);
                fillAttributes(prop.getAttributes(), usin, 7);
                fillAttributes(prop.getAttributes(), usin, 6);

                // Right
                List<SocialTenureRelationship> rights = tenureDao.findSocailTenureByUsin(usin);
                Right propRight = null;

                if (rights != null && rights.size() > 0) {
                    for (SocialTenureRelationship right : rights) {
                        if (propRight == null) {
                            propRight = new Right();
                            if (right.getCertIssueDate() != null) {
                                propRight.setCertDate(dfDate.format(right.getCertIssueDate()));
                            }
                            propRight.setCertNumber(StringUtils.empty(right.getCertNumber()));
                            propRight.setId((long) right.getGid());
                            if (right.getJuridicalArea() != null) {
                                propRight.setJuridicalArea(right.getJuridicalArea());
                            }
                            if (right.getRelationshipType() != null) {
                                propRight.setRelationshipId(Integer.parseInt(right.getRelationshipType().getCode().toString()));
                            }
                            if (right.getTenureclassId() != null) {
                                propRight.setRightTypeId(right.getTenureclassId().getTenureId());
                            }
                            if (right.getShare_type() != null) {
                                propRight.setShareTypeId(right.getShare_type().getGid());
                            }
                            propRight.setAttributes(new ArrayList<Attribute>());
                            propRight.setNaturalPersons(new ArrayList<com.rmsi.mast.studio.mobile.transferobjects.Person>());
                            fillAttributes(propRight.getAttributes(), right.getGid(), 4);

                            prop.setRight(propRight);
                        }

                        // Persons
                        Person person = right.getPerson_gid();
                        NaturalPerson naturalPerson = null;

                        // Non natural person
                        if (person.getPerson_type_gid().getPerson_type_gid() == 2) {
                            NonNaturalPerson nonPerson = nonNaturalPersonDao.findById(person.getPerson_gid()).get(0);
                            com.rmsi.mast.studio.mobile.transferobjects.Person propNonPerson = new com.rmsi.mast.studio.mobile.transferobjects.Person();
                            propNonPerson.setIsNatural(0);
                            if (!StringUtils.isEmpty(person.getMobileGroupId())) {
                                propNonPerson.setId(Long.parseLong(person.getMobileGroupId()));
                            } else {
                                propNonPerson.setId(person.getPerson_gid());
                            }
                            propNonPerson.setResident(person.getResident() ? 1 : 0);
                            propNonPerson.setAttributes(new ArrayList<Attribute>());
                            fillAttributes(propNonPerson.getAttributes(), nonPerson.getPerson_gid(), 5);
                            propRight.setNonNaturalPerson(propNonPerson);

                            // Get natural person assosiated with non natural
                            if (nonPerson.getPoc_gid() > 0) {
                                naturalPerson = naturalPersonDao.findById(nonPerson.getPoc_gid()).get(0);
                            }
                        } else if (person.getPerson_type_gid().getPerson_type_gid() == 1) {
                            naturalPerson = naturalPersonDao.findById(person.getPerson_gid()).get(0);
                        }

                        if (naturalPerson != null) {
                            propRight.getNaturalPersons().add(createPropPerson(naturalPerson));
                        }
                    }
                }

                // POIs
                List<SpatialUnitPersonWithInterest> poiList = spatialUnitPersonWithInterestDao.findByUsin(usin);

                if (poiList != null && poiList.size() > 0) {
                    prop.setPersonOfInterests(new ArrayList<PersonOfInterest>());

                    for (SpatialUnitPersonWithInterest poi : poiList) {
                        PersonOfInterest propPoi = new PersonOfInterest();
                        if (poi.getDob() != null) {
                            propPoi.setDob(dfDate.format(poi.getDob()));
                        }
                        if (poi.getGender() != null) {
                            propPoi.setGenderId((int) poi.getGender().getGenderId());
                        }
                        propPoi.setId(poi.getId());
                        propPoi.setName(StringUtils.empty(poi.getPerson_name()));
                        if (poi.getRelationshipType() != null) {
                            propPoi.setRelationshipId(Integer.parseInt(poi.getRelationshipType().getCode().toString()));
                        }
                        prop.getPersonOfInterests().add(propPoi);
                    }
                }

                // Deceased person
                List<SpatialunitDeceasedPerson> deceasedlst = spatialUnitDeceasedPersonDao.findPersonByUsin(usin);

                if (deceasedlst != null && deceasedlst.size() > 0) {
                    DeceasedPerson deceasedPerson = new DeceasedPerson();
                    deceasedPerson.setId(deceasedlst.get(0).getId());
                    deceasedPerson.setFirstName(StringUtils.empty(deceasedlst.get(0).getFirstname()));
                    deceasedPerson.setLastName(StringUtils.empty(deceasedlst.get(0).getLastname()));
                    deceasedPerson.setMiddleName(StringUtils.empty(deceasedlst.get(0).getMiddlename()));
                    prop.setDeceasedPerson(deceasedPerson);
                }

                // Property media
                List<SourceDocument> docs = sourceDocumentDao.findByUsin(usin);

                if (docs != null && docs.size() > 0) {
                    prop.setMedia(new ArrayList<Media>());

                    for (SourceDocument doc : docs) {
                        if (doc.getPerson_gid() == null && doc.getSocial_tenure_gid() == null && doc.getDisputeId() == null) {
                            Media media = new Media();
                            media.setId((long) doc.getGid());
                            media.setType(doc.getMediaType());
                            media.setAttributes(new ArrayList<Attribute>());
                            fillAttributes(media.getAttributes(), doc.getGid(), 3);
                            prop.getMedia().add(media);
                        }
                    }
                }

                // Dispute. If dispue found set claim type to disputed
                List<Dispute> disputes = disputeDao.findByPropId(usin);
                if (disputes != null && disputes.size() > 0) {
                    for (Dispute dispute : disputes) {
                        if (dispute.getStatus() != null && dispute.getStatus().getCode() == 1) {
                            // Make sure property is disputed. set dispute type
                            prop.setClaimTypeCode("dispute");

                            // Add dispute information
                            com.rmsi.mast.studio.mobile.transferobjects.Dispute propDispute = new com.rmsi.mast.studio.mobile.transferobjects.Dispute();
                            propDispute.setDescription(StringUtils.empty(dispute.getDescription()));
                            if (dispute.getDisputeType() != null) {
                                propDispute.setDisputeTypeId(dispute.getDisputeType().getCode());
                            }
                            propDispute.setId(dispute.getId());
                            if (dispute.getRegDate() != null) {
                                propDispute.setRegDate(dfDate.format(dispute.getRegDate()));
                            }

                            // Add disputeing parties
                            if (dispute.getDisputingPersons() != null && dispute.getDisputingPersons().size() > 0) {
                                propDispute.setDisputingPersons(new ArrayList<com.rmsi.mast.studio.mobile.transferobjects.Person>());
                                for (NaturalPerson person : dispute.getDisputingPersons()) {
                                    propDispute.getDisputingPersons().add(createPropPerson(person));
                                }
                            }

                            // Add media
                            if (docs != null && docs.size() > 0) {
                                propDispute.setMedia(new ArrayList<Media>());

                                for (SourceDocument doc : docs) {
                                    if (doc.getPerson_gid() == null && doc.getSocial_tenure_gid() == null && doc.getDisputeId() != null) {
                                        Media media = new Media();
                                        media.setId((long) doc.getGid());
                                        media.setType(doc.getMediaType());
                                        media.setAttributes(new ArrayList<Attribute>());
                                        fillAttributes(media.getAttributes(), doc.getGid(), 3);
                                        propDispute.getMedia().add(media);
                                    }
                                }
                            }

                            prop.setDispute(propDispute);
                            break;
                        }
                    }
                }

                props.add(prop);
            }
        }
        return props;
    }

    private com.rmsi.mast.studio.mobile.transferobjects.Person createPropPerson(NaturalPerson naturalPerson) {
        com.rmsi.mast.studio.mobile.transferobjects.Person propPerson = new com.rmsi.mast.studio.mobile.transferobjects.Person();
        propPerson.setIsNatural(1);
        if (!StringUtils.isEmpty(naturalPerson.getMobileGroupId())) {
            propPerson.setId(Long.parseLong(naturalPerson.getMobileGroupId()));
        } else {
            propPerson.setId(naturalPerson.getPerson_gid());
        }
        if (naturalPerson.getAcquisitionType() != null) {
            propPerson.setAcquisitionTypeId(naturalPerson.getAcquisitionType().getCode());
        }
        propPerson.setShare(StringUtils.empty(naturalPerson.getShare()));
        if (naturalPerson.getPersonSubType() != null) {
            propPerson.setSubTypeId((int) naturalPerson.getPersonSubType().getPerson_type_gid());
        }
        propPerson.setResident(naturalPerson.getResident() ? 1 : 0);
        propPerson.setAttributes(new ArrayList<Attribute>());
        fillAttributes(propPerson.getAttributes(), naturalPerson.getPerson_gid(), 2);
        return propPerson;
    }

    private void fillAttributes(List<Attribute> attributes, long parentId, int categoryId) {
        if (attributes == null) {
            return;
        }

        List<Object> attrObjects = attributeValuesDao.getAttributeValueandId(parentId, categoryId);
        if (attrObjects == null || attrObjects.size() < 1) {
            return;
        }

        for (Object attrObj : attrObjects) {
            Object[] values = (Object[]) attrObj;

            Attribute attr = new Attribute();
            attr.setId(Long.parseLong(values[0].toString()));
            attr.setValue(values[1].toString());
            attributes.add(attr);
        }
    }

    @Override
    public Long getSurveyProjectAttributeId(long attributeId, String projectId) {
        return attributes.getSurveyProjectAttributeId(attributeId, projectId).getUid();
    }

    @Override
    public List<Surveyprojectattribute> getSurveyProjectAttributes(String projectId) {
        return attributes.getSurveyProjectAttributes(projectId);
    }

    @Override
    public List<ProjectAdjudicator> getProjectAdjudicatorByProjectId(String projectId) {
        return projectAdjudicatorDAO.findByProject(projectId);
    }

    @Override
    public List<ProjectHamlet> getProjectHamletsByProjectId(String projectId) {
        return projectHamletDAO.findHamlets(projectId);
    }
}
