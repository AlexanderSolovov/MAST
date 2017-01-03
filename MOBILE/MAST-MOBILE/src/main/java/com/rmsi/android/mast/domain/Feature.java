package com.rmsi.android.mast.domain;

import java.io.Serializable;

public class Feature implements Serializable {
    private Long id;
    private String coordinates;
    private String geomType;
    private String status;
    private Long serverId;
    private String polygonNumber;
    private String surveyDate;

    public static String TABLE_NAME = "SPATIAL_FEATURES";
    public static String COL_ID = "FEATURE_ID";
    public static String COL_SERVER_ID = "SERVER_FEATURE_ID";
    public static String COL_COORDINATES = "COORDINATES";
    public static String COL_GEOM_TYPE = "GEOMTYPE";
    public static String COL_STATUS = "STATUS";
    public static String COL_POLYGON_NUMBER = "POLYGON_NUMBER";
    public static String COL_SURVEY_DATE = "SURVEY_DATE";

    public static String GEOM_POINT = "Point";
    public static String GEOM_LINE = "Line";
    public static String GEOM_POLYGON = "Polygon";

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public String getGeomType() {
        return geomType;
    }

    public void setGeomType(String geomType) {
        this.geomType = geomType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getServerId() {
        return serverId;
    }

    public void setServerId(Long serverId) {
        this.serverId = serverId;
    }

    public String getPolygonNumber() {
        return polygonNumber;
    }

    public void setPolygonNumber(String polygonNumber) {
        this.polygonNumber = polygonNumber;
    }

    public String getSurveyDate() {
        return surveyDate;
    }

    public void setSurveyDate(String surveyDate) {
        this.surveyDate = surveyDate;
    }
}

