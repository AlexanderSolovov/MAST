package com.rmsi.mast.studio.mobile.service.impl;

import com.rmsi.mast.studio.dao.AcquisitionTypeDao;
import com.rmsi.mast.studio.dao.ClaimTypeDao;
import com.rmsi.mast.studio.dao.DisputeDao;
import com.rmsi.mast.studio.dao.DisputeTypeDao;
import com.rmsi.mast.studio.dao.DocumentTypeDao;
import com.rmsi.mast.studio.dao.GroupTypeDAO;
import com.rmsi.mast.studio.dao.IdTypeDao;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

import com.rmsi.mast.studio.dao.PersonTypeDAO;
import com.rmsi.mast.studio.dao.RelationshipTypeDao;
import com.rmsi.mast.studio.dao.UserDAO;
import com.rmsi.mast.studio.domain.AttributeValues;
import com.rmsi.mast.studio.domain.Dispute;
import com.rmsi.mast.studio.domain.NaturalPerson;
import com.rmsi.mast.studio.domain.NonNaturalPerson;
import com.rmsi.mast.studio.domain.PersonType;
import com.rmsi.mast.studio.domain.SocialTenureRelationship;
import com.rmsi.mast.studio.domain.SourceDocument;
import com.rmsi.mast.studio.domain.SpatialUnit;
import com.rmsi.mast.studio.domain.SpatialUnitPersonWithInterest;
import com.rmsi.mast.studio.domain.Status;
import com.rmsi.mast.studio.domain.Surveyprojectattribute;
import com.rmsi.mast.studio.domain.User;
import com.rmsi.mast.studio.domain.WorkflowStatusHistory;
import com.rmsi.mast.studio.domain.fetch.SpatialUnitTable;
import com.rmsi.mast.studio.domain.fetch.SpatialunitDeceasedPerson;
import com.rmsi.mast.studio.mobile.dao.AttributeOptionsDao;
import com.rmsi.mast.studio.mobile.dao.AttributeValuesDao;
import com.rmsi.mast.studio.mobile.dao.CitizenshipDao;
import com.rmsi.mast.studio.mobile.dao.EducationLevelDao;
import com.rmsi.mast.studio.mobile.dao.GenderDao;
import com.rmsi.mast.studio.mobile.dao.LandTypeDao;
import com.rmsi.mast.studio.mobile.dao.LandUseTypeDao;
import com.rmsi.mast.studio.mobile.dao.MaritalStatusDao;
import com.rmsi.mast.studio.mobile.dao.NaturalPersonDao;
import com.rmsi.mast.studio.mobile.dao.NonNaturalPersonDao;
import com.rmsi.mast.studio.mobile.dao.OccupancyTypeDao;
import com.rmsi.mast.studio.mobile.dao.PersonDao;
import com.rmsi.mast.studio.mobile.dao.PersonTypeDao;
import com.rmsi.mast.studio.mobile.dao.ShareTypeDao;
import com.rmsi.mast.studio.mobile.dao.SlopeValuesDao;
import com.rmsi.mast.studio.mobile.dao.SocialTenureDao;
import com.rmsi.mast.studio.mobile.dao.SoilQualityValuesDao;
import com.rmsi.mast.studio.mobile.dao.SourceDocumentDao;
import com.rmsi.mast.studio.mobile.dao.SpatialUnitDao;
import com.rmsi.mast.studio.mobile.dao.SpatialUnitPersonWithInterestDao;
import com.rmsi.mast.studio.mobile.dao.StatusDao;
import com.rmsi.mast.studio.mobile.dao.TenureClassDao;
import com.rmsi.mast.studio.mobile.dao.UserDataDao;
import com.rmsi.mast.studio.mobile.dao.WorkflowStatusHistoryDao;
import com.rmsi.mast.studio.mobile.service.SurveyProjectAttributeService;
import com.rmsi.mast.studio.mobile.service.UserDataService;
import com.rmsi.mast.studio.mobile.transferobjects.Attribute;
import com.rmsi.mast.studio.mobile.transferobjects.DeceasedPerson;
import com.rmsi.mast.studio.mobile.transferobjects.Person;
import com.rmsi.mast.studio.mobile.transferobjects.PersonOfInterest;
import com.rmsi.mast.studio.mobile.transferobjects.Property;
import com.rmsi.mast.studio.mobile.transferobjects.Right;
import com.rmsi.mast.studio.util.GeometryConversion;
import com.rmsi.mast.viewer.dao.SpatialUnitDeceasedPersonDao;
import com.rmsi.mast.viewer.service.LandRecordsService;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Set;
import org.apache.batik.bridge.TextUtilities;
import org.eclipse.emf.common.util.ArrayDelegatingEList;

/**
 * @author shruti.thakur
 *
 */
@Service
public class UserDataServiceImpl implements UserDataService {

    @Autowired
    UserDAO userdao;

    @Autowired
    UserDataDao userData;

    @Autowired
    SpatialUnitDao spatialUnitDao;

    @Autowired
    NaturalPersonDao naturalPersonDao;

    @Autowired
    NonNaturalPersonDao nonNaturalPersonDao;

    @Autowired
    SocialTenureDao socialTenureDao;

    @Autowired
    PersonDao personDao;

    @Autowired
    GenderDao genderDao;

    @Autowired
    CitizenshipDao citizenshipDao;

    @Autowired
    DisputeTypeDao disputeTypeDao;

    @Autowired
    AcquisitionTypeDao acquisitionTypeDao;

    @Autowired
    DocumentTypeDao documentTypeDao;

    @Autowired
    MaritalStatusDao maritalStatusDao;

    @Autowired
    OccupancyTypeDao occupancyTypeDao;

    @Autowired
    TenureClassDao tenureClassDao;

    @Autowired
    ShareTypeDao tenureRelationTypeDao;

    @Autowired
    ClaimTypeDao claimTypeDAO;

    @Autowired
    LandTypeDao landTypeDao;

    @Autowired
    SlopeValuesDao slopeValuesDao;

    @Autowired
    SoilQualityValuesDao soilQualityValuesDao;

    @Autowired
    LandUseTypeDao landUseTypeDao;

    @Autowired
    PersonTypeDao personTypeDao;

    @Autowired
    EducationLevelDao educationLevelDao;

    @Autowired
    IdTypeDao idTypeDao;

    @Autowired
    GroupTypeDAO groupTypeDao;

    @Autowired
    ShareTypeDao shareTypeDao;

    @Autowired
    RelationshipTypeDao relationshipTypeDao;

    @Autowired
    StatusDao status;

    @Autowired
    AttributeValuesDao attributeValuesDao;

    @Autowired
    WorkflowStatusHistoryDao workflowStatusHistoryDao;

    @Autowired
    SurveyProjectAttributeService surveyProjectAttribute;

    @Autowired
    AttributeOptionsDao attributeOptionsDao;

    @Autowired
    SourceDocumentDao sourceDocumentDao;

    @Autowired
    SpatialUnitPersonWithInterestDao spatialUnitPersonWithInterestDao;

    @Autowired
    SpatialUnitDeceasedPersonDao spatialUnitDeceasedPersonDao;

    @Autowired 
    DisputeDao disputeDao;
    
    private static final Logger logger = Logger.getLogger(UserDataServiceImpl.class.getName());

    @Override
    public User authenticateByEmail(String email, String passwd) {

        User user = userdao.findByUniqueName(email);

        if (user != null) {
            String decryptedPass = decryptPassword(user.getPassword());
            if (decryptedPass.equals(passwd)) {
                user.setPassword(decryptedPass);
                return user;
            } else {
                System.out.println("Incorrect Password");
            }
        } else {
            System.out.println("Authentication Failed, username doesn't exist");
        }
        return null;
    }

    @Override
    public String getDefaultProjectByUserId(int userId) {

        User user = userData.getUserByUserId(userId);

        if (user != null) {
            return user.getDefaultproject();
        }

        return null;
    }

    @Override
    @Transactional(noRollbackFor = Exception.class)
    public Map<String, String> saveClaims(List<Property> properties, String projectName, int userId) {
        Long featureId = 0L;
        Long serverPropId;
        Map<String, String> result = new IdentityHashMap<String, String>();

        if (properties == null || properties.size() < 1 || projectName == null || projectName.equals("") || userId < 1) {
            return null;
        }

        try {
            // Get list of all attributes defined for the project
            List<Surveyprojectattribute> projectAttributes = surveyProjectAttribute.getSurveyProjectAttributes(projectName);

            for (Property prop : properties) {

                featureId = prop.getId();
                Date creationDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss a").parse(prop.getCompletionDate());
                SpatialUnit spatialUnit = spatialUnitDao.findByImeiandTimeStamp(prop.getImei(), creationDate);

                if (spatialUnit != null) {
                    result.put(featureId.toString(), Long.toString(spatialUnit.getUsin()));
                    continue;
                }

                spatialUnit = new SpatialUnit();
                spatialUnit.setClaimType(claimTypeDAO.findById(prop.getClaimTypeCode(), true));
                spatialUnit.setPolygonNumber(prop.getPolygonNumber());
                spatialUnit.setSurveyDate(new SimpleDateFormat("yyyy-MM-dd").parse(prop.getSurveyDate()));
                spatialUnit.setStatusUpdateTime(creationDate);
                spatialUnit.setImeiNumber(prop.getImei());
                spatialUnit.setProject(projectName);
                spatialUnit.setUserid(userId);
                spatialUnit.setHamletId(prop.getHamletId());
                spatialUnit.setWitness1(prop.getAdjudicator1());
                spatialUnit.setWitness2(prop.getAdjudicator2());

                GeometryConversion geomConverter = new GeometryConversion();

                spatialUnit.setGtype(prop.getGeomType());

                // Setting geometry
                if (spatialUnit.getGtype().equalsIgnoreCase("point")) {
                    spatialUnit.setPoint(geomConverter.convertWktToPoint(prop.getCoordinates()));
                    spatialUnit.getPoint().setSRID(4326);
                    spatialUnit.setTheGeom(spatialUnit.getPoint());
                } else if (spatialUnit.getGtype().equalsIgnoreCase("line")) {
                    spatialUnit.setLine(geomConverter.convertWktToLineString(prop.getCoordinates()));
                    spatialUnit.getLine().setSRID(4326);
                    spatialUnit.setTheGeom(spatialUnit.getLine());
                } else if (spatialUnit.getGtype().equalsIgnoreCase("polygon")) {
                    spatialUnit.setPolygon(geomConverter.convertWktToPolygon(prop.getCoordinates()));
                    spatialUnit.setArea(spatialUnit.getPolygon().getArea());
                    spatialUnit.getPolygon().setSRID(4326);
                    spatialUnit.setPerimeter((float) spatialUnit.getPolygon().getLength());
                    spatialUnit.setTheGeom(spatialUnit.getPolygon());
                }

                spatialUnit.getTheGeom().setSRID(4326);
                spatialUnit.setActive(true);
                spatialUnit.setStatus(status.getStatusById(1));

                setPropAttibutes(spatialUnit, prop);

                serverPropId = spatialUnitDao.addSpatialUnit(spatialUnit).getUsin();
                spatialUnitDao.clear();

                // Save property attributes
                List<AttributeValues> attributes = createAttributesList(projectAttributes, prop.getAttributes());
                attributeValuesDao.addAttributeValues(attributes, serverPropId);

                // Save Natural persons
                if (prop.getRight() != null && prop.getRight().getNonNaturalPerson() != null) {
                    for (Person propPerson : prop.getRight().getNaturalPersons()) {
                        // Save natural person
                        NaturalPerson person = new NaturalPerson();
                        setNaturalPersonAttributes(person, propPerson);
                        person = naturalPersonDao.addNaturalPerson(person);
                        attributes = createAttributesList(projectAttributes, propPerson.getAttributes());
                        attributeValuesDao.addAttributeValues(attributes, person.getPerson_gid());

                        // Save right
                        SocialTenureRelationship right = new SocialTenureRelationship();
                        setRightAttributes(right, prop.getRight(), spatialUnit);
                        right.setUsin(serverPropId);
                        right.setPerson_gid(person);
                        long rightId = socialTenureDao.addSocialTenure(right).getGid();
                        attributes = createAttributesList(projectAttributes, prop.getRight().getAttributes());
                        attributeValuesDao.addAttributeValues(attributes, rightId);
                    }
                }

                // Save Non-natural person
                if (prop.getRight() != null && prop.getRight().getNonNaturalPerson() != null) {
                    for (Person propPerson : prop.getRight().getNaturalPersons()) {
                        NaturalPerson person = new NaturalPerson();
                        setNaturalPersonAttributes(person, propPerson);
                        // Natural person for non-natural is administrator
                        person.setPersonSubType(personTypeDao.findById(4L, false));

                        // Save natural person
                        Long personId = naturalPersonDao.addNaturalPerson(person).getPerson_gid();
                        attributes = createAttributesList(projectAttributes, propPerson.getAttributes());
                        attributeValuesDao.addAttributeValues(attributes, personId);

                        // Save non natural person
                        NonNaturalPerson nonPerson = new NonNaturalPerson();
                        setNonNaturalPersonAttributes(nonPerson, prop.getRight().getNonNaturalPerson());
                        nonPerson.setPoc_gid(personId);
                        nonPerson = nonNaturalPersonDao.addNonNaturalPerson(nonPerson);
                        attributes = createAttributesList(projectAttributes, prop.getRight().getNonNaturalPerson().getAttributes());
                        attributeValuesDao.addAttributeValues(attributes, nonPerson.getPerson_gid());

                        // Save right
                        SocialTenureRelationship right = new SocialTenureRelationship();
                        setRightAttributes(right, prop.getRight(), spatialUnit);
                        right.setUsin(serverPropId);
                        right.setPerson_gid(nonPerson);
                        long rightId = socialTenureDao.addSocialTenure(right).getGid();
                        attributes = createAttributesList(projectAttributes, prop.getRight().getAttributes());
                        attributeValuesDao.addAttributeValues(attributes, rightId);
                        // Only 1 natural person is allowed for non-natural
                        break;
                    }
                }

                // Save person of interests
                List<SpatialUnitPersonWithInterest> pois = new ArrayList<>();

                for (PersonOfInterest propPoi : prop.getPersonOfInterests()) {
                    SpatialUnitPersonWithInterest poi = new SpatialUnitPersonWithInterest();
                    if (!StringUtils.isEmpty(propPoi.getDob())) {
                        poi.setDob(new SimpleDateFormat("yyyy-MM-dd").parse(propPoi.getDob()));
                    }
                    if (propPoi.getRelationshipId() > 0) {
                        poi.setRelationshipType(relationshipTypeDao.findById(propPoi.getRelationshipId(), false));
                    }
                    if (propPoi.getGenderId() > 0) {
                        poi.setGender(genderDao.getGenderById(propPoi.getGenderId()));
                    }
                    poi.setPerson_name(propPoi.getName());
                    pois.add(poi);
                }

                if (pois.size() > 0) {
                    spatialUnitPersonWithInterestDao.addNextOfKin(pois, serverPropId);
                }

                // Save decesed person
                if (prop.getDeceasedPerson() != null) {
                    SpatialunitDeceasedPerson deadPerson = new SpatialunitDeceasedPerson();
                    deadPerson.setFirstname(prop.getDeceasedPerson().getFirstName());
                    deadPerson.setLastname(prop.getDeceasedPerson().getLastName());
                    deadPerson.setMiddlename(prop.getDeceasedPerson().getMiddleName());
                    deadPerson.setUsin(serverPropId);

                    List<SpatialunitDeceasedPerson> deadPersons = new ArrayList();
                    deadPersons.add(deadPerson);

                    spatialUnitDeceasedPersonDao.addDeceasedPerson(deadPersons, serverPropId);
                }

                // Save dispute
                if(prop.getDispute() != null){
                    Dispute dispute = new Dispute();
                    dispute.setDescription(prop.getDispute().getDescription());
                    dispute.setDisputeType(disputeTypeDao.findById(prop.getDispute().getDisputeTypeId(), false));
                    dispute.setRegDate(new SimpleDateFormat("yyyy-MM-dd").parse(prop.getDispute().getRegDate()));
                    dispute.setDisputingPersons(new ArrayList());
                    for(Person propPerson : prop.getDispute().getDisputingPersons()){
                        NaturalPerson person = new NaturalPerson();
                        setNaturalPersonAttributes(person, propPerson);
                        person = naturalPersonDao.addNaturalPerson(person);
                        attributes = createAttributesList(projectAttributes, propPerson.getAttributes());
                        attributeValuesDao.addAttributeValues(attributes, person.getPerson_gid());
                        dispute.getDisputingPersons().add(person);
                    }
                    disputeDao.save(dispute);
                }
                
                // Add workflow record
                WorkflowStatusHistory workflowStatusHistory = new WorkflowStatusHistory();

                workflowStatusHistory.setUsin(serverPropId);
                workflowStatusHistory.setWorkflow_status_id(spatialUnit.getStatus().getWorkflowStatusId());
                workflowStatusHistory.setUserid(spatialUnit.getUserid());
                workflowStatusHistory.setStatus_change_time(
                        new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date())
                        )
                );

                workflowStatusHistoryDao.addWorkflowStatusHistory(workflowStatusHistory);

                // Add server property ID to the result
                result.put(featureId.toString(), Long.toString(serverPropId));
            }

            return result;

        } catch (Exception e) {
            logger.error("Failed to save property: ID " + featureId.toString(), e);
            return result;
        }
    }

    private void setRightAttributes(SocialTenureRelationship right, Right propRight, SpatialUnit parcel) throws ParseException {
        if (right == null || propRight == null || propRight.getAttributes() == null || propRight.getAttributes().size() < 1) {
            return;
        }

        if (!StringUtils.isEmpty(propRight.getCertDate())) {
            right.setCertIssueDate(new SimpleDateFormat("yyyy-MM-dd").parse(propRight.getCertDate()));
        }
        right.setCertNumber(propRight.getCertNumber());
        right.setJuridicalArea(propRight.getJuridicalArea());
        if (propRight.getRightTypeId() > 0) {
            right.setTenureclassId(tenureClassDao.getTenureClassById(propRight.getRightTypeId()));
        }
        if (propRight.getRelationshipId() > 0) {
            right.setRelationshipType(relationshipTypeDao.findById(propRight.getRelationshipId(), false));
        }
        if (propRight.getShareTypeId() > 0) {
            right.setShare_type(shareTypeDao.findById(propRight.getShareTypeId(), false));
        }
        right.setIsActive(true);

        for (Attribute attribute : propRight.getAttributes()) {
            String value = attribute.getValue();
            Long id = attribute.getId();

            if (id == 9) {
                parcel.setProposedUse(landUseTypeDao.getLandUseTypeById(Integer.parseInt(attribute.getValue())));
            } else if (id == 31) {
                right.setShare_type(shareTypeDao.findById(Integer.parseInt(value), false));
            } else if (id == 24) {
                right.setOccupancyTypeId(occupancyTypeDao.getOccupancyTypeById(Integer.parseInt(value)));
            } else if (id == 23) {
                right.setTenureclassId(tenureClassDao.getTenureClassById(Integer.parseInt(value)));
            } else if (id == 32) {
                try {
                    right.setSocial_tenure_startdate(new SimpleDateFormat("yyyy-MM-dd").parse(value.trim()));
                } catch (java.text.ParseException e) {
                }
            } else if (id == 33) {
                try {
                    right.setSocial_tenure_enddate(new SimpleDateFormat("yyyy-MM-dd").parse(value.trim()));
                } catch (java.text.ParseException e) {
                }
            } else if (id == 13) {
                right.setTenureDuration(Float.parseFloat(value));
            } else if (id == 300) {
                right.setAcquisitionType(acquisitionTypeDao.findById(Integer.parseInt(value), false));
            }
        }
    }

    private void setNonNaturalPersonAttributes(NonNaturalPerson person, Person propPerson) throws ParseException {
        if (person == null || propPerson == null || propPerson.getAttributes() == null || propPerson.getAttributes().size() < 1) {
            return;
        }

        person.setMobileGroupId(propPerson.getId().toString());
        person.setResident(propPerson.getResident() == 1);
        person.setMobileGroupId(propPerson.getId().toString());
        person.setActive(true);

        for (Attribute attribute : propPerson.getAttributes()) {
            String value = attribute.getValue();
            Long id = attribute.getId();

            if (id == 6) {
                person.setInstitutionName(value);
            } else if (id == 7) {
                person.setAddress(value);
            } else if (id == 8) {
                person.setPhoneNumber(value);
            } else if (id == 52) {
                person.setGroupType(groupTypeDao.findById(Integer.parseInt(value), false));
            }
        }
    }

    private void setNaturalPersonAttributes(NaturalPerson naturalPerson, Person propPerson) throws ParseException {
        if (naturalPerson == null || propPerson == null || propPerson.getAttributes() == null || propPerson.getAttributes().size() < 1) {
            return;
        }

        naturalPerson.setMobileGroupId(propPerson.getId().toString());
        naturalPerson.setResident(propPerson.getResident() == 1);
        naturalPerson.setResident_of_village(naturalPerson.getResident());
        naturalPerson.setShare(propPerson.getShare());
        if (propPerson.getSubTypeId() > 0) {
            naturalPerson.setPersonSubType(personTypeDao.getPersonTypeById(propPerson.getSubTypeId()));
        }
        if (propPerson.getAcquisitionTypeId() > 0) {
            naturalPerson.setAcquisitionType(acquisitionTypeDao.findById(propPerson.getAcquisitionTypeId(), true));
        }
        naturalPerson.setActive(true);

        for (Attribute attribute : propPerson.getAttributes()) {
            String value = attribute.getValue();
            Long id = attribute.getId();

            if (id == 1) {
                naturalPerson.setFirstName(value);
                naturalPerson.setAlias(value);
            } else if (id == 2) {
                naturalPerson.setLastName(value);
            } else if (id == 3) {
                naturalPerson.setMiddleName(value);
            } else if (id == 29) {
                naturalPerson.setAlias(value);
            } else if (id == 4) {
                naturalPerson.setGender(genderDao.getGenderById(Long.parseLong(value)));
            } else if (id == 5) {
                naturalPerson.setMobile(value);
            } else if (id == 30) {
                naturalPerson.setIdentity(value);
            } else if (id == 21) {
                naturalPerson.setAge(Integer.parseInt(value));
            } else if (id == 19) {
                naturalPerson.setOccupation(value);
            } else if (id == 20) {
                naturalPerson.setEducation(educationLevelDao.getEducationLevelById(Integer.parseInt(value)));
            } else if (id == 25) {
                naturalPerson.setTenure_Relation(value);
            } else if (id == 26) {
                naturalPerson.setHouseholdRelation(value);
            } else if (id == 27) {
                naturalPerson.setWitness(value);
            } else if (id == 22) {
                naturalPerson.setMarital_status(maritalStatusDao.getMaritalStatusById(Integer.parseInt(value)));
            } else if (id == 40) {
                if (value.equalsIgnoreCase("yes")) {
                    naturalPerson.setOwner(true);
                } else {
                    naturalPerson.setOwner(false);
                }
            } else if (id == 41) {
                naturalPerson.setAdministator(value);
            } else if (id == 42) {
                naturalPerson.setCitizenship_id(citizenshipDao.getCitizensbyId(Integer.parseInt(value)));
            } else if (id == 310) {
                naturalPerson.setIdNumber(value);
            } else if (id == 320) {
                naturalPerson.setIdType(idTypeDao.findById(Integer.getInteger(value), true));
            } else if (id == 330 && value != null && !value.equals("")) {
                naturalPerson.setDob(new SimpleDateFormat("yyyy-MM-dd").parse(value));
            }
        }
    }

    private List<AttributeValues> createAttributesList(List<Surveyprojectattribute> projectAttributes, List<Attribute> propAttributes) {
        List<AttributeValues> attributes = new ArrayList<>();

        if (propAttributes == null || propAttributes.size() < 1 || projectAttributes == null || projectAttributes.size() < 1) {
            return attributes;
        }

        for (Attribute propAttribute : propAttributes) {
            if (propAttribute.getValue() != null && propAttribute.getValue().equalsIgnoreCase("null")) {
                AttributeValues attribute = new AttributeValues();
                attribute.setValue(propAttribute.getValue());
                for (Surveyprojectattribute projectAttribute : projectAttributes) {
                    if (projectAttribute.getAttributeMaster().getId() == propAttribute.getId()) {
                        attribute.setUid(projectAttribute.getUid());
                        break;
                    }
                }
                attributes.add(attribute);
            }
        }
        return attributes;
    }

    /**
     * Sets Spatial unit object attributes based on property object
     */
    private void setPropAttibutes(SpatialUnit parcel, Property prop) {
        if (parcel == null || prop == null || prop.getAttributes() == null || prop.getAttributes().size() < 1) {
            return;
        }

        for (Attribute attribute : prop.getAttributes()) {
            if (attribute.getId() == 9) {
                parcel.setProposedUse(landUseTypeDao.getLandUseTypeById(Integer.parseInt(attribute.getValue())));
            } else if (attribute.getId() == 15) {
                parcel.setHousehidno(Integer.parseInt(attribute.getValue()));
            } else if (attribute.getId() == 16) {
                parcel.setExistingUse(landUseTypeDao.getLandUseTypeById(Integer.parseInt(attribute.getValue())));
            } else if (attribute.getId() == 17) {
                parcel.setComments(attribute.getValue());
            } else if (attribute.getId() == 28) {
                parcel.setLandOwner(attribute.getValue());
            } else if (attribute.getId() == 34) {
                parcel.setAddress1(attribute.getValue());
            } else if (attribute.getId() == 35) {
                parcel.setAddress2(attribute.getValue());
            } else if (attribute.getId() == 36) {
                parcel.setPostal_code(attribute.getValue());
            } else if (attribute.getId() == 37) {
                parcel.setTypeName(landTypeDao.getLandTypeById(Integer.parseInt(attribute.getValue())));
            } else if (attribute.getId() == 38) {
                parcel.setSoilQuality(soilQualityValuesDao.getSoilQualityValuesById(Integer.parseInt(attribute.getValue())));
            } else if (attribute.getId() == 39) {
                parcel.setSlope(slopeValuesDao.getSlopeValuesById(Integer.parseInt(attribute.getValue())));
            } else if (attribute.getId() == 44) {
                parcel.setNeighborNorth(attribute.getValue());
            } else if (attribute.getId() == 45) {
                parcel.setNeighborSouth(attribute.getValue());
            } else if (attribute.getId() == 46) {
                parcel.setNeighborEast(attribute.getValue());
            } else if (attribute.getId() == 47) {
                parcel.setNeighborWest(attribute.getValue());
            } else if (attribute.getId() == 53) {
                parcel.setOtherUseType(attribute.getValue());
            }
        }
    }

    @Override
    @Transactional(noRollbackFor = Exception.class)
    public SourceDocument uploadMultimedia(SourceDocument sourceDocument,
            MultipartFile mpFile, File documentsDir) {

        /**
         * 1) Insert source document
         */
        sourceDocument = sourceDocumentDao.addSourceDocument(sourceDocument);

        /**
         * 2) Insert values in AttributeValues
         */
        AttributeValues attributeValues;

        List<AttributeValues> attributeValuesList = new ArrayList<AttributeValues>();

        if ((sourceDocument.getComments() != null)) {

            attributeValues = new AttributeValues();

            attributeValues.setUid(surveyProjectAttribute
                    .getSurveyProjectAttributeId(10, spatialUnitDao
                            .getSpatialUnitByUsin(sourceDocument.getUsin())
                            .getProject()));

            attributeValues.setValue(sourceDocument.getComments());

            attributeValuesList.add(attributeValues);
        }
        if ((sourceDocument.getEntity_name() != null)) {

            attributeValues = new AttributeValues();

            attributeValues.setUid(surveyProjectAttribute
                    .getSurveyProjectAttributeId(11, spatialUnitDao
                            .getSpatialUnitByUsin(sourceDocument.getUsin())
                            .getProject()));

            attributeValues.setValue(sourceDocument.getEntity_name());

            attributeValuesList.add(attributeValues);
        }

        attributeValuesDao.addAttributeValues(attributeValuesList,
                Long.valueOf(sourceDocument.getGid()));

        /**
         * 3) Save file on server *
         */
        try {
            byte[] document = mpFile.getBytes();

            if (sourceDocument.getGid() != 0) {

                String fileExtension = sourceDocument
                        .getScanedSourceDoc()
                        .substring(
                                sourceDocument.getScanedSourceDoc()
                                .indexOf(".") + 1,
                                sourceDocument.getScanedSourceDoc().length())
                        .toLowerCase();

                /**
                 * Create the file on Server
                 */
                File serverFile = new File(documentsDir + File.separator
                        + sourceDocument.getGid() + "." + fileExtension);

                if (serverFile.length() <= 0) {
                    logger.error("file not exist");

                } else {
                    BufferedOutputStream outputStream = new BufferedOutputStream(
                            new FileOutputStream(serverFile));

                    outputStream.write(document);

                    outputStream.close();
                }

            }
        } catch (MultipartException | IOException ex) {

            logger.error("Exception", ex);

        }
        return sourceDocument;

    }

    @Override
    public SourceDocument findMultimedia(String fileName, Long usin) {

        return sourceDocumentDao.findByUsinandFile(fileName, usin);

    }

    @Override
    public Long findPersonByMobileGroupIdandUsin(String mobileGroupId, Long usin) {

        try {
            List<SocialTenureRelationship> tenureList = socialTenureDao
                    .findSocailTenureByUsin(usin);

            Iterator<SocialTenureRelationship> tenureItr = tenureList
                    .iterator();

            while (tenureItr.hasNext()) {

                SocialTenureRelationship tenure = tenureItr.next();

                com.rmsi.mast.studio.domain.Person person = personDao.findPersonById(tenure.getPerson_gid()
                        .getPerson_gid());

                if (person.getPerson_type_gid().getPerson_type_gid() == 2) {

                    /**
                     * if person is non-natural
                     */
                    return nonNaturalPersonDao.findById(person.getPerson_gid())
                            .get(0).getPoc_gid();

                } else if (person.getPerson_type_gid().getPerson_type_gid() == 1) {

                    /**
                     * if person is natural
                     */
                    if (person.getMobileGroupId().equals(mobileGroupId)) {
                        return person.getPerson_gid();
                    }
                }
            }
        } catch (Exception ex) {

            logger.error("Exception", ex);
            System.out.println("Exception while finding PERSON:: " + ex);
            throw ex;
        }
        return null;
    }

    /**
     * This methods decrypts the password
     *
     * @param enycPasswd : Encrypted Password
     * @return: Returns the decrypted password
     */
    private String decryptPassword(String enycPasswd) {

        final String ENCRYPT_KEY = "HG58YZ3CR9";
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(ENCRYPT_KEY);
        encryptor.setAlgorithm("PBEWithMD5AndTripleDES");
        String decPasswd = encryptor.decrypt(enycPasswd);

        return decPasswd;
    }

    @Override
    public boolean updateNaturalPersonAttribValues(NaturalPerson naturalPerson,
            String project) {
        try {
            List<AttributeValues> attribsList = new ArrayList<AttributeValues>();
            AttributeValues attributeValues = new AttributeValues();

            if (StringUtils.isNotEmpty(naturalPerson.getFirstName())) {
                long attributeId = 1;
                attributeValues.setParentuid(naturalPerson.getPerson_gid());
                attributeValues.setValue(naturalPerson.getFirstName());
                // attributeValues.setAttributevalueid(attributeId);
                attributeValues.setUid(surveyProjectAttribute
                        .getSurveyProjectAttributeId(attributeId, project));
                attribsList.add(attributeValues);
                attributeValues = new AttributeValues();
            }
            if (StringUtils.isNotEmpty(naturalPerson.getLastName())) {
                long attributeId = 2;
                attributeValues.setParentuid(naturalPerson.getPerson_gid());
                attributeValues.setValue(naturalPerson.getLastName());
                // attributeValues.setAttributevalueid(attributeId);
                attributeValues.setUid(surveyProjectAttribute
                        .getSurveyProjectAttributeId(attributeId, project));
                attribsList.add(attributeValues);
                attributeValues = new AttributeValues();
            }
            if (StringUtils.isNotEmpty(naturalPerson.getMiddleName())) {
                long attributeId = 3;
                attributeValues.setParentuid(naturalPerson.getPerson_gid());
                attributeValues.setValue(naturalPerson.getMiddleName());
                // attributeValues.setAttributevalueid(attributeId);
                attributeValues.setUid(surveyProjectAttribute
                        .getSurveyProjectAttributeId(attributeId, project));
                attribsList.add(attributeValues);
                attributeValues = new AttributeValues();
            }
            if (StringUtils.isNotEmpty(naturalPerson.getAlias())) {
                long attributeId = 29;
                attributeValues.setParentuid(naturalPerson.getPerson_gid());
                attributeValues.setValue(naturalPerson.getAlias());
                // attributeValues.setAttributevalueid(attributeId);
                attributeValues.setUid(surveyProjectAttribute
                        .getSurveyProjectAttributeId(attributeId, project));
                attribsList.add(attributeValues);
                attributeValues = new AttributeValues();
            }
            if (naturalPerson.getGender() != null) {
                Long attributeId = 4L;
                String value = attributeOptionsDao.getAttributeOptionsId(
                        attributeId.intValue(), (int) naturalPerson.getGender()
                        .getGenderId());
                if (value == null) {
                    System.out.println("Null value for AttributeID:"
                            + attributeId);
                    throw new NullPointerException();
                }
                attributeValues.setParentuid(naturalPerson.getPerson_gid());
                attributeValues.setValue(value);
                // attributeValues.setAttributevalueid(attributeId);
                attributeValues.setUid(surveyProjectAttribute
                        .getSurveyProjectAttributeId(attributeId, project));
                attribsList.add(attributeValues);
                attributeValues = new AttributeValues();
            }
            if (StringUtils.isNotEmpty(naturalPerson.getMobile())) {
                long attributeId = 5;
                attributeValues.setParentuid(naturalPerson.getPerson_gid());
                attributeValues.setValue(naturalPerson.getMobile());
                // attributeValues.setAttributevalueid(attributeId);
                attributeValues.setUid(surveyProjectAttribute
                        .getSurveyProjectAttributeId(attributeId, project));
                attribsList.add(attributeValues);
                attributeValues = new AttributeValues();
            }
            if (StringUtils.isNotEmpty(naturalPerson.getIdentity())) {
                long attributeId = 30;
                attributeValues.setParentuid(naturalPerson.getPerson_gid());
                attributeValues.setValue(naturalPerson.getIdentity());
                // attributeValues.setAttributevalueid(attributeId);
                attributeValues.setUid(surveyProjectAttribute
                        .getSurveyProjectAttributeId(attributeId, project));
                attribsList.add(attributeValues);
                attributeValues = new AttributeValues();
            }
            if (naturalPerson.getAge() != 0) {
                long attributeId = 21;
                attributeValues.setParentuid(naturalPerson.getPerson_gid());
                attributeValues.setValue(naturalPerson.getAge() + "");
                // attributeValues.setAttributevalueid(attributeId);
                attributeValues.setUid(surveyProjectAttribute
                        .getSurveyProjectAttributeId(attributeId, project));
                attribsList.add(attributeValues);
                attributeValues = new AttributeValues();
            }
            if (StringUtils.isNotEmpty(naturalPerson.getOccupation())) {
                long attributeId = 19;
                attributeValues.setValue(naturalPerson.getOccupation());
                // attributeValues.setAttributevalueid(attributeId);
                attributeValues.setUid(surveyProjectAttribute
                        .getSurveyProjectAttributeId(attributeId, project));
                attribsList.add(attributeValues);
                attributeValues = new AttributeValues();
            }
            if (naturalPerson.getEducation() != null) {
                Long attributeId = 20L;
                String value = attributeOptionsDao.getAttributeOptionsId(
                        attributeId.intValue(), (int) naturalPerson
                        .getEducation().getLevelId());
                if (value == null) {
                    System.out.println("Null value for AttributeID:"
                            + attributeId);
                    throw new NullPointerException();
                }
                attributeValues.setParentuid(naturalPerson.getPerson_gid());
                attributeValues.setValue(value);
                // attributeValues.setAttributevalueid(attributeId);
                attributeValues.setUid(surveyProjectAttribute
                        .getSurveyProjectAttributeId(attributeId, project));
                attribsList.add(attributeValues);
                attributeValues = new AttributeValues();
            }
            if (StringUtils.isNotEmpty(naturalPerson.getTenure_Relation())) {
                long attributeId = 25;
                attributeValues.setValue(naturalPerson.getTenure_Relation());
                // attributeValues.setAttributevalueid(attributeId);
                attributeValues.setUid(surveyProjectAttribute
                        .getSurveyProjectAttributeId(attributeId, project));
                attribsList.add(attributeValues);
                attributeValues = new AttributeValues();
            }
            if (StringUtils.isNotEmpty(naturalPerson.getHouseholdRelation())) {
                long attributeId = 26;
                attributeValues.setParentuid(naturalPerson.getPerson_gid());
                attributeValues.setValue(naturalPerson.getHouseholdRelation());
                // attributeValues.setAttributevalueid(attributeId);
                attributeValues.setUid(surveyProjectAttribute
                        .getSurveyProjectAttributeId(attributeId, project));
                attribsList.add(attributeValues);
                attributeValues = new AttributeValues();
            }
            if (StringUtils.isNotEmpty(naturalPerson.getWitness())) {
                long attributeId = 27;
                attributeValues.setParentuid(naturalPerson.getPerson_gid());
                attributeValues.setValue(naturalPerson.getWitness());
                // attributeValues.setAttributevalueid(attributeId);
                attributeValues.setUid(surveyProjectAttribute
                        .getSurveyProjectAttributeId(attributeId, project));
                attribsList.add(attributeValues);
                attributeValues = new AttributeValues();
            }
            if (naturalPerson.getMarital_status() != null) {
                Long attributeId = 22L;
                String value = attributeOptionsDao.getAttributeOptionsId(
                        attributeId.intValue(), (int) naturalPerson
                        .getMarital_status().getMaritalStatusId());
                if (value == null) {
                    System.out.println("Null value for AttributeID:"
                            + attributeId);
                    throw new NullPointerException();
                }
                attributeValues.setParentuid(naturalPerson.getPerson_gid());
                attributeValues.setValue(value);
                // attributeValues.setAttributevalueid(attributeId);
                attributeValues.setUid(surveyProjectAttribute
                        .getSurveyProjectAttributeId(attributeId, project));
                attribsList.add(attributeValues);
                attributeValues = new AttributeValues();
            }
            if (StringUtils.isNotEmpty(naturalPerson.getOwner().toString())) {
                long attributeId = 40;
                attributeValues.setParentuid(naturalPerson.getPerson_gid());
                attributeValues.setValue(naturalPerson.getOwner().toString());
                attributeValues.setUid(surveyProjectAttribute
                        .getSurveyProjectAttributeId(attributeId, project));
                attribsList.add(attributeValues);
                attributeValues = new AttributeValues();
            }
            if (StringUtils.isNotEmpty(naturalPerson.getAdministator())) {
                long attributeId = 41;
                attributeValues.setParentuid(naturalPerson.getPerson_gid());
                attributeValues.setValue(naturalPerson.getAdministator());
                attributeValues.setUid(surveyProjectAttribute
                        .getSurveyProjectAttributeId(attributeId, project));
                attribsList.add(attributeValues);
                attributeValues = new AttributeValues();
            }
            if (StringUtils.isNotEmpty(naturalPerson.getCitizenship_id().toString())) {
                Long attributeId = 42L;

                String value = attributeOptionsDao.getAttributeOptionsId(
                        attributeId.intValue(), (int) naturalPerson
                        .getCitizenship_id().getId());
                if (value == null) {
                    System.out.println("Null value for AttributeID:"
                            + attributeId);
                    throw new NullPointerException();
                }

                attributeValues.setParentuid(naturalPerson.getPerson_gid());
                attributeValues.setValue(value);
                attributeValues.setUid(surveyProjectAttribute
                        .getSurveyProjectAttributeId(attributeId, project));
                attribsList.add(attributeValues);
                attributeValues = new AttributeValues();
            }
            if (StringUtils.isNotEmpty(naturalPerson.getResident_of_village()
                    .toString())) {
                long attributeId = 43;
                attributeValues.setParentuid(naturalPerson.getPerson_gid());
                attributeValues.setValue(naturalPerson.getResident_of_village()
                        .toString());
                attributeValues.setUid(surveyProjectAttribute
                        .getSurveyProjectAttributeId(attributeId, project));
                attribsList.add(attributeValues);
                attributeValues = new AttributeValues();
            }
            if (StringUtils.isNotEmpty(naturalPerson.getPersonSubType()
                    .toString())) {
                Long attributeId = 54L;
                String value = attributeOptionsDao.getAttributeOptionsId(
                        attributeId.intValue(), (int) naturalPerson
                        .getPersonSubType().getPerson_type_gid());
                if (value == null) {
                    System.out.println("Null value for AttributeID:"
                            + attributeId);
                    throw new NullPointerException();
                }
                attributeValues.setParentuid(naturalPerson.getPerson_gid());
                attributeValues.setValue(value);
                attributeValues.setUid(surveyProjectAttribute
                        .getSurveyProjectAttributeId(attributeId, project));
                attribsList.add(attributeValues);
                attributeValues = new AttributeValues();
            }

            attributeValuesDao.updateAttributeValues(attribsList);
        } catch (Exception e) {
            logger.error("Exception", e);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean updateTenureAttribValues(
            SocialTenureRelationship socialTenure, String project) {
        try {
            List<AttributeValues> attribsList = new ArrayList<AttributeValues>();
            AttributeValues attributeValues = new AttributeValues();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            if (socialTenure.getSocial_tenure_startdate() != null) {
                long attributeId = 32;
                attributeValues.setParentuid(Long.parseLong(socialTenure
                        .getGid() + ""));
                attributeValues.setValue(dateFormat.format(socialTenure
                        .getSocial_tenure_startdate()));
                // attributeValues.setAttributevalueid(attributeId);
                attributeValues.setUid(surveyProjectAttribute
                        .getSurveyProjectAttributeId(attributeId, project));
                attribsList.add(attributeValues);
                attributeValues = new AttributeValues();
            }
            if (socialTenure.getSocial_tenure_enddate() != null) {
                long attributeId = 33;
                attributeValues.setParentuid(Long.parseLong(socialTenure
                        .getGid() + ""));
                attributeValues.setValue(dateFormat.format(socialTenure
                        .getSocial_tenure_enddate()));
                // attributeValues.setAttributevalueid(attributeId);
                attributeValues.setUid(surveyProjectAttribute
                        .getSurveyProjectAttributeId(attributeId, project));
                attribsList.add(attributeValues);
                attributeValues = new AttributeValues();
            }
            if (socialTenure.getTenureDuration() != 0) {
                long attributeId = 13;
                attributeValues.setParentuid(Long.parseLong(socialTenure
                        .getGid() + ""));
                attributeValues
                        .setValue(socialTenure.getTenureDuration() + "");
                // attributeValues.setAttributevalueid(attributeId);
                attributeValues.setUid(surveyProjectAttribute
                        .getSurveyProjectAttributeId(attributeId, project));
                attribsList.add(attributeValues);
                attributeValues = new AttributeValues();
            }
            if (socialTenure.getShare_type() != null) {
                Long attributeId = 31L;
                String value = attributeOptionsDao.getAttributeOptionsId(
                        attributeId.intValue(), socialTenure.getShare_type().getGid());
                /*
				 * String value = attributeOptionsDao.getAttributeOptionsId(
				 * attributeId.intValue(), 1);
                 */

                if (value == null) {
                    System.out.println("Null value for AttributeID:"
                            + attributeId);
                    throw new NullPointerException();
                }
                attributeValues.setParentuid(Long.parseLong(socialTenure
                        .getGid() + ""));
                attributeValues.setValue(value);
                // attributeValues.setAttributevalueid(attributeId);
                attributeValues.setUid(surveyProjectAttribute
                        .getSurveyProjectAttributeId(attributeId, project));
                attribsList.add(attributeValues);
                attributeValues = new AttributeValues();
            }
            if (socialTenure.getOccupancyTypeId() != null) {
                Long attributeId = 24L;
                String value = attributeOptionsDao.getAttributeOptionsId(
                        attributeId.intValue(), socialTenure
                        .getOccupancyTypeId().getOccId());
                if (value == null) {
                    System.out.println("Null value for AttributeID:"
                            + attributeId);
                    throw new NullPointerException();
                }
                attributeValues.setParentuid(Long.parseLong(socialTenure
                        .getGid() + ""));
                attributeValues.setValue(value);
                // attributeValues.setAttributevalueid(attributeId);
                attributeValues.setUid(surveyProjectAttribute
                        .getSurveyProjectAttributeId(attributeId, project));
                attribsList.add(attributeValues);
                attributeValues = new AttributeValues();
            }
            if (socialTenure.getTenureclassId() != null) {
                Long attributeId = 23L;
                String value = attributeOptionsDao.getAttributeOptionsId(
                        attributeId.intValue(), socialTenure
                        .getTenureclassId().getTenureId());
                if (value == null) {
                    System.out.println("Null value for AttributeID:"
                            + attributeId);
                    throw new NullPointerException();
                }
                attributeValues.setParentuid(Long.parseLong(socialTenure
                        .getGid() + ""));
                attributeValues.setValue(value);
                // attributeValues.setAttributevalueid(attributeId);
                attributeValues.setUid(surveyProjectAttribute
                        .getSurveyProjectAttributeId(attributeId, project));
                attribsList.add(attributeValues);
                attributeValues = new AttributeValues();
            }

            attributeValuesDao.updateAttributeValues(attribsList);
        } catch (Exception e) {
            logger.error("Exception", e);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean updateNonNaturalPersonAttribValues(
            NonNaturalPerson nonnaturalPerson, String project) {
        try {
            List<AttributeValues> attribsList = new ArrayList<AttributeValues>();
            AttributeValues attributeValues = new AttributeValues();

            if (StringUtils.isNotEmpty(nonnaturalPerson.getAddress())) {
                long attributeId = 7;
                attributeValues.setParentuid(nonnaturalPerson.getPerson_gid());
                attributeValues.setValue(nonnaturalPerson.getAddress());
                // attributeValues.setAttributevalueid(attributeId);
                attributeValues.setUid(surveyProjectAttribute
                        .getSurveyProjectAttributeId(attributeId, project));
                attribsList.add(attributeValues);
                attributeValues = new AttributeValues();
            }
            if (StringUtils.isNotEmpty(nonnaturalPerson.getInstitutionName())) {
                long attributeId = 6;
                attributeValues.setParentuid(nonnaturalPerson.getPerson_gid());
                attributeValues.setValue(nonnaturalPerson.getInstitutionName());
                // attributeValues.setAttributevalueid(attributeId);
                attributeValues.setUid(surveyProjectAttribute
                        .getSurveyProjectAttributeId(attributeId, project));
                attribsList.add(attributeValues);
                attributeValues = new AttributeValues();
            }
            if (StringUtils.isNotEmpty(nonnaturalPerson.getPhoneNumber())) {
                long attributeId = 8;
                attributeValues.setParentuid(nonnaturalPerson.getPerson_gid());
                attributeValues.setValue(nonnaturalPerson.getPhoneNumber());
                // attributeValues.setAttributevalueid(attributeId);
                attributeValues.setUid(surveyProjectAttribute
                        .getSurveyProjectAttributeId(attributeId, project));
                attribsList.add(attributeValues);
                attributeValues = new AttributeValues();
            }
            if (nonnaturalPerson.getGroupType() != null) {
                Long attributeId = 52L;
                attributeValues.setParentuid(nonnaturalPerson.getPerson_gid());
                String value = attributeOptionsDao.getAttributeOptionsId(
                        attributeId.intValue(), (int) nonnaturalPerson
                        .getGroupType().getGroupId());
                if (value == null) {
                    System.out.println("Null value for AttributeID:"
                            + attributeId);
                    throw new NullPointerException();
                }
                attributeValues.setValue(value);
                attributeValues.setUid(surveyProjectAttribute
                        .getSurveyProjectAttributeId(attributeId, project));
                attribsList.add(attributeValues);
                attributeValues = new AttributeValues();
            }
            attributeValuesDao.updateAttributeValues(attribsList);
        } catch (Exception e) {
            logger.error("Exception", e);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean updateGeneralAttribValues(SpatialUnitTable spatialunit,
            String project) {
        try {
            List<AttributeValues> attribsList = new ArrayList<AttributeValues>();
            AttributeValues attributeValues = new AttributeValues();


            /*			if (StringUtils.isNotEmpty(spatialunit.getTypeName())) {
				long attributeId = 14;

			if (spatialunit.getLandType() != null) {
				Long attributeId = 37L;

				attributeValues.setParentuid(spatialunit.getUsin());
				String value = attributeOptionsDao.getAttributeOptionsId(
						attributeId.intValue(), (int) spatialunit.getLandType()
								.getLandTypeId());
				if (value == null) {
					System.out.println("Null value for AttributeID:"
							+ attributeId);
					throw new NullPointerException();
				}
				attributeValues.setValue(value);
				attributeValues.setUid(surveyProjectAttribute
						.getSurveyProjectAttributeId(attributeId, project));
				attribsList.add(attributeValues);
				attributeValues = new AttributeValues();
			}*/
            if (spatialunit.getHousehidno() != 0) {
                long attributeId = 15;
                attributeValues.setParentuid(spatialunit.getUsin());
                attributeValues.setValue(spatialunit.getHousehidno() + "");
                // //attributeValues.setAttributevalueid(attributeId);
                attributeValues.setUid(surveyProjectAttribute
                        .getSurveyProjectAttributeId(attributeId, project));
                attribsList.add(attributeValues);
                attributeValues = new AttributeValues();
            }
            if (StringUtils.isNotEmpty(spatialunit.getComments())) {
                long attributeId = 17;
                attributeValues.setParentuid(spatialunit.getUsin());
                attributeValues.setValue(spatialunit.getComments());
                // //attributeValues.setAttributevalueid(attributeId);
                attributeValues.setUid(surveyProjectAttribute
                        .getSurveyProjectAttributeId(attributeId, project));
                attribsList.add(attributeValues);
                attributeValues = new AttributeValues();
            }
            if (StringUtils.isNotEmpty(spatialunit.getAddress1())) {
                long attributeId = 34;
                attributeValues.setParentuid(spatialunit.getUsin());
                attributeValues.setValue(spatialunit.getAddress1());
                // //attributeValues.setAttributevalueid(attributeId);
                attributeValues.setUid(surveyProjectAttribute
                        .getSurveyProjectAttributeId(attributeId, project));
                attribsList.add(attributeValues);
                attributeValues = new AttributeValues();
            }
            if (StringUtils.isNotEmpty(spatialunit.getAddress2())) {
                long attributeId = 35;
                attributeValues.setParentuid(spatialunit.getUsin());
                attributeValues.setValue(spatialunit.getAddress2());
                // attributeValues.setAttributevalueid(attributeId);
                attributeValues.setUid(surveyProjectAttribute
                        .getSurveyProjectAttributeId(attributeId, project));
                attribsList.add(attributeValues);
                attributeValues = new AttributeValues();
            }
            if (StringUtils.isNotEmpty(spatialunit.getPostal_code())) {
                long attributeId = 36;
                attributeValues.setParentuid(spatialunit.getUsin());
                attributeValues.setValue(spatialunit.getPostal_code());
                // attributeValues.setAttributevalueid(attributeId);
                attributeValues.setUid(surveyProjectAttribute
                        .getSurveyProjectAttributeId(attributeId, project));
                attribsList.add(attributeValues);
                attributeValues = new AttributeValues();
            }
            if (spatialunit.getProposedUse() != null) {
                Long attributeId = 9L;
                String value = attributeOptionsDao.getAttributeOptionsId(
                        attributeId.intValue(), (int) spatialunit
                        .getProposedUse().getLandUseTypeId());
                if (value == null) {
                    System.out.println("Null value for AttributeID:"
                            + attributeId);
                    throw new NullPointerException();
                }
                attributeValues.setParentuid(spatialunit.getUsin());
                attributeValues.setValue(value);
                // attributeValues.setAttributevalueid(attributeId);
                attributeValues.setUid(surveyProjectAttribute
                        .getSurveyProjectAttributeId(attributeId, project));
                attribsList.add(attributeValues);
                attributeValues = new AttributeValues();
            }
            if (spatialunit.getExistingUse() != null) {
                Long attributeId = 16L;
                String value = attributeOptionsDao.getAttributeOptionsId(
                        attributeId.intValue(), (int) spatialunit
                        .getExistingUse().getLandUseTypeId());
                if (value == null) {
                    System.out.println("Null value for AttributeID:"
                            + attributeId);
                    throw new NullPointerException();
                }
                attributeValues.setParentuid(spatialunit.getUsin());
                attributeValues.setValue(value);
                // attributeValues.setAttributevalueid(attributeId);
                attributeValues.setUid(surveyProjectAttribute
                        .getSurveyProjectAttributeId(attributeId, project));
                attribsList.add(attributeValues);
                attributeValues = new AttributeValues();
            }
            if (spatialunit.getSoilQualityValues() != null) {
                Long attributeId = 38L;
                attributeValues.setParentuid(spatialunit.getUsin());
                String value = attributeOptionsDao.getAttributeOptionsId(
                        attributeId.intValue(), (int) spatialunit
                        .getSlopeValues().getId());
                if (value == null) {
                    System.out.println("Null value for AttributeID:"
                            + attributeId);
                    throw new NullPointerException();
                }
                attributeValues.setValue(value);
                attributeValues.setUid(surveyProjectAttribute
                        .getSurveyProjectAttributeId(attributeId, project));
                attribsList.add(attributeValues);
                attributeValues = new AttributeValues();
            }
            if (spatialunit.getSlopeValues() != null) {
                Long attributeId = 39L;
                attributeValues.setParentuid(spatialunit.getUsin());
                String value = attributeOptionsDao.getAttributeOptionsId(
                        attributeId.intValue(), (int) spatialunit
                        .getSlopeValues().getId());
                if (value == null) {
                    System.out.println("Null value for AttributeID:"
                            + attributeId);
                    throw new NullPointerException();
                }
                attributeValues.setValue(value);
                attributeValues.setUid(surveyProjectAttribute
                        .getSurveyProjectAttributeId(attributeId, project));
                attribsList.add(attributeValues);
                attributeValues = new AttributeValues();
            }
            if (StringUtils.isNotEmpty(spatialunit.getNeighbor_north())) {
                long attributeId = 44;
                attributeValues.setParentuid(spatialunit.getUsin());
                attributeValues.setValue(spatialunit.getNeighbor_north());
                attributeValues.setUid(surveyProjectAttribute
                        .getSurveyProjectAttributeId(attributeId, project));
                attribsList.add(attributeValues);
                attributeValues = new AttributeValues();
            }
            if (StringUtils.isNotEmpty(spatialunit.getNeighbor_south())) {
                long attributeId = 45;
                attributeValues.setParentuid(spatialunit.getUsin());
                attributeValues.setValue(spatialunit.getNeighbor_south());
                attributeValues.setUid(surveyProjectAttribute
                        .getSurveyProjectAttributeId(attributeId, project));
                attribsList.add(attributeValues);
                attributeValues = new AttributeValues();
            }
            if (StringUtils.isNotEmpty(spatialunit.getNeighbor_east())) {
                long attributeId = 46;
                attributeValues.setParentuid(spatialunit.getUsin());
                attributeValues.setValue(spatialunit.getNeighbor_east());
                attributeValues.setUid(surveyProjectAttribute
                        .getSurveyProjectAttributeId(attributeId, project));
                attribsList.add(attributeValues);
                attributeValues = new AttributeValues();
            }
            if (StringUtils.isNotEmpty(spatialunit.getNeighbor_west())) {
                long attributeId = 47;
                attributeValues.setParentuid(spatialunit.getUsin());
                attributeValues.setValue(spatialunit.getNeighbor_west());
                attributeValues.setUid(surveyProjectAttribute
                        .getSurveyProjectAttributeId(attributeId, project));
                attribsList.add(attributeValues);
                attributeValues = new AttributeValues();
            }
            if (StringUtils.isNotEmpty(spatialunit.getWitness_1())) {
                long attributeId = 48;
                attributeValues.setParentuid(spatialunit.getUsin());
                attributeValues.setValue(spatialunit.getWitness_1());
                attributeValues.setUid(surveyProjectAttribute
                        .getSurveyProjectAttributeId(attributeId, project));
                attribsList.add(attributeValues);
                attributeValues = new AttributeValues();
            }
            if (StringUtils.isNotEmpty(spatialunit.getWitness_2())) {
                long attributeId = 49;
                attributeValues.setParentuid(spatialunit.getUsin());
                attributeValues.setValue(spatialunit.getWitness_2());
                attributeValues.setUid(surveyProjectAttribute
                        .getSurveyProjectAttributeId(attributeId, project));
                attribsList.add(attributeValues);
                attributeValues = new AttributeValues();
            }
            if (StringUtils.isNotEmpty(spatialunit.getWitness_3())) {
                long attributeId = 50;
                attributeValues.setParentuid(spatialunit.getUsin());
                attributeValues.setValue(spatialunit.getWitness_3());
                attributeValues.setUid(surveyProjectAttribute
                        .getSurveyProjectAttributeId(attributeId, project));
                attribsList.add(attributeValues);
                attributeValues = new AttributeValues();
            }
            if (StringUtils.isNotEmpty(spatialunit.getWitness_4())) {
                long attributeId = 51;
                attributeValues.setParentuid(spatialunit.getUsin());
                attributeValues.setValue(spatialunit.getWitness_4());
                attributeValues.setUid(surveyProjectAttribute
                        .getSurveyProjectAttributeId(attributeId, project));
                attribsList.add(attributeValues);
                attributeValues = new AttributeValues();
            }
            if (StringUtils.isNotEmpty(spatialunit.getOtherUseType())) {
                long attributeId = 53;
                attributeValues.setParentuid(spatialunit.getUsin());
                attributeValues.setValue(spatialunit.getOtherUseType());
                attributeValues.setUid(surveyProjectAttribute
                        .getSurveyProjectAttributeId(attributeId, project));
                attribsList.add(attributeValues);
                attributeValues = new AttributeValues();
            }
            attributeValuesDao.updateAttributeValues(attribsList);
        } catch (Exception e) {
            logger.error("Exception", e);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean updateMultimediaAttribValues(SourceDocument sourcedocument,
            String project) {
        try {
            List<AttributeValues> attribsList = new ArrayList<AttributeValues>();
            AttributeValues attributeValues = new AttributeValues();

            if (StringUtils.isNotEmpty(sourcedocument.getScanedSourceDoc())) {
                long attributeId = 10;
                attributeValues.setParentuid(Long.parseLong(sourcedocument
                        .getGid() + ""));
                attributeValues.setValue(sourcedocument.getScanedSourceDoc());
                // attributeValues.setAttributevalueid(attributeId);
                attributeValues.setUid(surveyProjectAttribute
                        .getSurveyProjectAttributeId(attributeId, project));
                attribsList.add(attributeValues);
                attributeValues = new AttributeValues();
            }
            if (StringUtils.isNotEmpty(sourcedocument.getComments())) {
                long attributeId = 11;
                attributeValues.setParentuid(Long.parseLong(sourcedocument
                        .getGid() + ""));
                attributeValues.setValue(sourcedocument.getComments());
                // attributeValues.setAttributevalueid(attributeId);
                attributeValues.setUid(surveyProjectAttribute
                        .getSurveyProjectAttributeId(attributeId, project));
                attribsList.add(attributeValues);
                attributeValues = new AttributeValues();
            }

            attributeValuesDao.updateAttributeValues(attribsList);
        } catch (Exception e) {
            logger.error("Exception", e);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    @Transactional(noRollbackFor = Exception.class)
    public List<Long> updateAdjudicatedData(Long userId, List<Long> usinList) {

        try {

            List<Long> sucessfulUpdateList = new ArrayList<Long>();

            WorkflowStatusHistory statusHistory;

            Iterator<Long> usinIter = usinList.iterator();

            // Get Spatial Unit by usin
            while (usinIter.hasNext()) {

                /**
                 * 1) Updating Status and Stautsv update time in Spatial Unit
                 */
                Long usin = usinIter.next();
                Status statusId = status.getStatusById(2);

                SpatialUnit spatialUnit = spatialUnitDao
                        .getSpatialUnitByUsin(usin);

                Date statusUpdateTime = new SimpleDateFormat(
                        "dd/MM/yyyy HH:mm:ss").parse(new SimpleDateFormat(
                        "dd/MM/yyyy HH:mm:ss").format(new Date()));
                spatialUnit.setStatus(statusId);
                spatialUnit.setStatusUpdateTime(statusUpdateTime);

                /**
                 * 2) Updating Status History in Workflow Status History
                 */
                statusHistory = new WorkflowStatusHistory();

                statusHistory.setStatus_change_time(statusUpdateTime);
                statusHistory.setUserid(userId);
                statusHistory.setUsin(usin);
                statusHistory.setWorkflow_status_id(statusId
                        .getWorkflowStatusId());

                sucessfulUpdateList.add(spatialUnitDao.addSpatialUnit(
                        spatialUnit).getUsin());
                workflowStatusHistoryDao
                        .addWorkflowStatusHistory(statusHistory);
            }
            return sucessfulUpdateList;
        } catch (Exception e) {
            logger.error("Exception in saving STATUS UPDATE in SPATIAL UNIT and STATUS HISTORY::: "
                    + e);
            e.printStackTrace();
            return null;
        }
    }

}
