package com.rmsi.android.mast.util;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.rmsi.android.mast.activity.R;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;

import java.util.List;

/**
 * Contains various GIS functions
 */

public class GisUtility {

    /**
     * Returns distance between two points represented by Point coordinates
     */
    public static double getDistanceBetweenPoints(android.graphics.Point point1, android.graphics.Point point2){
        return Math.sqrt(Math.pow(point2.x-point1.x, 2) + Math.pow(point2.y - point1.y, 2));
    }

    /**
     * Returns distance between two points represented by LatLng coordinates
     */
    public static double getDistanceBetweenPoints(LatLng point1, LatLng point2){
        return Math.sqrt(Math.pow(point2.longitude-point1.longitude, 2) + Math.pow(point2.latitude - point1.latitude, 2));
    }

    /**
     * Returns distance between two points represented by x, y coordinates
     */
    public static double getDistanceBetweenPoints(double x1, double y1, double x2, double y2){
        return Math.sqrt(Math.pow(x2-x1, 2) + Math.pow(y2-y1, 2));
    }

    /**
     * Returns distance from the point represented by Point to the segment with Point coordinates
     * @param point The point from where to search the distance
     * @param segmentPoint1 First point of the segment
     * @param segmentPoint2 Second point of the segment
     * @return
     */
    public static double getDistanceToSegment(android.graphics.Point point, android.graphics.Point segmentPoint1, android.graphics.Point segmentPoint2) {
        return getDistanceToSegment(point.x, point.y, segmentPoint1.x, segmentPoint1.y, segmentPoint2.x, segmentPoint2.y);
    }

    /**
     * Returns distance from the point represented by LatLng to the segment with LatLng coordinates
     * @param point The point from where to search the distance
     * @param segmentPoint1 First point of the segment
     * @param segmentPoint2 Second point of the segment
     * @return
     */
    public static double getDistanceToSegment(LatLng point, LatLng segmentPoint1, LatLng segmentPoint2) {
        return getDistanceToSegment(point.longitude, point.latitude, segmentPoint1.longitude,
                segmentPoint1.latitude, segmentPoint2.longitude, segmentPoint2.latitude);
    }

    /**
     * Returns distance from point with x, y coordinates to the segment with points x1, y1 and x2, y2
     * @param px X coordinate of the point
     * @param py Y coordinate of the point
     * @param sx1 X coordinate of the first segment point
     * @param sy1 Y coordinate of the first segment point
     * @param sx2 X coordinate of the second segment point
     * @param sy2 Y coordinate of the second segment point
     * @return
     */
    public static double getDistanceToSegment(double px, double py, double sx1, double sy1, double sx2, double sy2) {
        double A = px - sx1;
        double B = py - sy1;
        double C = sx2 - sx1;
        double D = sy2 - sy1;

        double dot = A * C + B * D;
        double len_sq = C * C + D * D;
        double param = -1;
        if (len_sq != 0) //in case of 0 length line
            param = dot / len_sq;

        double xx, yy;

        if (param < 0) {
            xx = sx1;
            yy = sy1;
        }
        else if (param > 1) {
            xx = sx2;
            yy = sy2;
        }
        else {
            xx = sx1 + param * C;
            yy = sy1 + param * D;
        }

        double dx = px - xx;
        double dy = py - yy;
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Returns the closest point on the segment from provided point
     * @param point The point from where to search
     * @param segmentPoint1 First segment point
     * @param segmentPoint2 Second segment point
     * @return
     */
    public static LatLng getClosestPointOnSegment(LatLng point, LatLng segmentPoint1, LatLng segmentPoint2)
    {
        return getClosestPointOnSegment(point.longitude, point.latitude,
                segmentPoint1.longitude, segmentPoint1.latitude,
                segmentPoint2.longitude, segmentPoint2.latitude);
    }

    /**
     * Returns the closest point on the segment from provided point
     * @param px X coordinate of the point
     * @param py Y coordinate of the point
     * @param sx1 X coordinate of the first segment point
     * @param sy1 Y coordinate of the first segment point
     * @param sx2 X coordinate of the second segment point
     * @param sy2 Y coordinate of the second segment point
     * @return
     */
    public static LatLng getClosestPointOnSegment(double px, double py, double sx1, double sy1, double sx2, double sy2)
    {
        double xDelta = sx2 - sx1;
        double yDelta = sy2 - sy1;

        if ((xDelta == 0) && (yDelta == 0))
        {
            return new LatLng(sy1, sx1);
        }

        double u = ((px - sx1) * xDelta + (py - sy1) * yDelta) / (xDelta * xDelta + yDelta * yDelta);

        final LatLng closestPoint;
        if (u < 0)
        {
            closestPoint = new LatLng(sy1, sx1);
        }
        else if (u > 1)
        {
            closestPoint = new LatLng(sy2, sx2);
        }
        else
        {
            closestPoint = new LatLng(sy1 + u * yDelta, sx1 + u * xDelta);
        }

        return closestPoint;
    }

    public static boolean IsPointInPolygon(Point ptClicked, Polygon polygon )
    {
        if (polygon.contains(ptClicked))
        {
            return true;
        }else
        {
            return false;
        }
    }

    public static boolean IsPointIntersectsLine(Point ptClicked, LineString polyline)
    {
        //0.00005 = Approx 5 meter
        if (polyline.intersects(ptClicked.buffer(0.00015)))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public static boolean IsPointIntersectsPoint(Point ptClicked, Point featurePoint)
    {
        double distance = featurePoint.distance(ptClicked);

        if (distance < 0.00010)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public static String getWKTfromPoints(String geomtype, List<LatLng> pointslist)
    {
        String WKTString="";
        if(geomtype.equalsIgnoreCase(CommonFunctions.GEOM_POINT))
        {
            LatLng point = pointslist.get(0);

            //WKTString = "POINT ("+point.longitude+" "+point.latitude+")";
            WKTString = point.longitude+" "+point.latitude;
        }
        else if(geomtype.equalsIgnoreCase(CommonFunctions.GEOM_LINE))
        {
            String WKTSubStr="";

            for (int i = 0; i < pointslist.size(); i++)
            {
                LatLng latLng = pointslist.get(i);

                if(i > 0)
                {
                    WKTSubStr = WKTSubStr +",";
                }

                WKTSubStr = WKTSubStr + latLng.longitude+" "+latLng.latitude;
            }
            //WKTString = "LINESTRING ("+WKTSubStr+")";
            WKTString = WKTSubStr;
        }
        else if(geomtype.equalsIgnoreCase(CommonFunctions.GEOM_POLYGON))
        {
            String WKTSubStr="";

            for (int i = 0; i < pointslist.size(); i++)
            {
                LatLng latLng = pointslist.get(i);

                if(i > 0)
                {
                    WKTSubStr = WKTSubStr +",";
                }

                WKTSubStr = WKTSubStr + latLng.longitude+" "+latLng.latitude;
            }
            //WKTString = "POLYGON (("+WKTSubStr+"))";
            WKTString = WKTSubStr;
        }
        return WKTString;
    }

    /** Vertex type enumeration. */
    public static enum VERTEX_TYPE {NORMAL, SNAPPED, MOVING};

    /**
     * Processes string representing bounding box with 2 coordinate and returns LatLngBounds
     * @param strBounds Bounding box string
     * @return
     */
    public static LatLngBounds getBoundsFromString(String strBounds){
        if(strBounds == null || strBounds.equals("")){
            return null;
        }
        String[] xy = strBounds.replace(" ", "").split(",");
        if(xy == null || xy.length !=4){
            return null;
        }

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(new LatLng(Double.parseDouble(xy[1]), Double.parseDouble(xy[0])));
        builder.include(new LatLng(Double.parseDouble(xy[3]), Double.parseDouble(xy[2])));
        return builder.build();
    }

    /**
     * Returns map extent
     */
    public static LatLngBounds getMapExtent(){
        return getBoundsFromString(CommonFunctions.getInstance().getMapExtent());
    }

    /**
     * Zooms to the default map extent
     * @param googleMap Google map component to zoom on
     */
    public static void zoomToMapExtent(GoogleMap googleMap){
        LatLngBounds mapExtent = GisUtility.getMapExtent();

        if(mapExtent != null){
            int padding = 0;
            com.google.android.gms.maps.CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(mapExtent, padding);
            googleMap.moveCamera(cu);
            googleMap.animateCamera(cu);
        } else {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(CommonFunctions.latitude, CommonFunctions.longitude), 15)
            );
        }
    }

    /**
     * Creates vertex marker from provided point
     * @param point Point to be used as vertex location
     * @param vertexType Type of the vertex
     */
    public static MarkerOptions makeVertex(LatLng point, VERTEX_TYPE vertexType){
        MarkerOptions m = new MarkerOptions();
        m.position(point);
        m.anchor(0.5f, 0.5f);
        switch (vertexType){
            case NORMAL:
                m.icon(BitmapDescriptorFactory.fromResource(R.drawable.green_circle_vertex));
            default:
                m.icon(BitmapDescriptorFactory.fromResource(R.drawable.green_circle_vertex));
        }
        return m;
    }
}
