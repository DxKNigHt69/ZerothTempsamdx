package com.whereismytransport.sdktemplateapp;

import android.content.Context;
import android.graphics.Color;

import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import transportapisdk.models.Itinerary;
import transportapisdk.models.Leg;
import transportapisdk.models.Point;
import transportapisdk.models.Waypoint;

public final class MapboxHelper {

    public static void drawItineraryOnMap(Context context, MapboxMap map, Itinerary itinerary) {
        final int lineWidthPixels = context.getResources().getDimensionPixelSize(R.dimen.waypoint_map_line_width);
        final int markerWidth = context.getResources().getDimensionPixelSize(R.dimen.waypoint_intermediate_map_marker_width);
        final int markerHeight = context.getResources().getDimensionPixelSize(R.dimen.waypoint_intermediate_map_marker_height);
        final int transferMarkerWidth = context.getResources().getDimensionPixelSize(R.dimen.waypoint_transfer_map_marker_width);
        final int transferMarkerHeight = context.getResources().getDimensionPixelSize(R.dimen.waypoint_transfer_map_marker_height);

        for (Leg leg : itinerary.getLegs()) {
            ArrayList<LatLng> legList = new ArrayList<>();
            for (List<Double> p : leg.getGeometry().getCoordinates()) {
                legList.add(new LatLng(p.get(1), p.get(0)));
            }

            PolylineOptions line = new PolylineOptions();
            line.addAll(legList);
            if (leg.getType().equalsIgnoreCase("walking")) {
                line.color(Color.BLACK);
                line.width(lineWidthPixels);
            } else {
                line.color(Color.parseColor(leg.getLine().getColour()));
                line.width(lineWidthPixels);

                Icon transferIcon = BitmapHelper.getVectorAsMapBoxIcon(context, R.drawable.ic_start_and_end_of_leg_icon, transferMarkerWidth, transferMarkerHeight);
                Icon stopIcon = BitmapHelper.getIntermediateStopAsMapBoxIcon(context, markerWidth, markerHeight, Color.parseColor(leg.getLine().getColour()));

                for (int i = 0; i < leg.getWaypoints().size(); i++) {
                    Waypoint waypoint = leg.getWaypoints().get(i);

                    Date dateTime = null;
                    try {
                        dateTime = DateHelper.parseIsoDateString(waypoint.getDepartureTime());
                    } catch (ParseException e) { }

                    MarkerOptions stopMarker = new MarkerOptions();
                    if (waypoint.getStop() != null) {
                        stopMarker.setPosition(new LatLng(waypoint.getStop().getGeometry().getCoordinates()[1], waypoint.getStop().getGeometry().getCoordinates()[0]));
                        stopMarker.setTitle(waypoint.getStop().getName() + " - " + TimeFormattingHelper.formatDate(dateTime));
                        stopMarker.setSnippet(waypoint.getStop().getAgency().getName());
                        stopMarker.setIcon(stopIcon);
                        if (i == 0 || i == leg.getWaypoints().size() - 1) {
                            //stopMarker.setIcon(transferIcon);
                        }
                    } else if (waypoint.getHail() != null) {
                        Point point = (Point) waypoint.getHail().getGeometry();
                        stopMarker.setPosition(new LatLng(point.getCoordinates()[1], point.getCoordinates()[0]));
                        stopMarker.setTitle(context.getString(R.string.minibus_taxi) + " - " + TimeFormattingHelper.formatDate(dateTime));
                        stopMarker.setSnippet(context.getString(R.string.depart_taxi));
                        stopMarker.setIcon(transferIcon);
                    } else if (waypoint.getDropOffType() != null) {
                        Point point = (Point) waypoint.getLocation().getGeometry();
                        stopMarker.setPosition(new LatLng(point.getCoordinates()[1], point.getCoordinates()[0]));
                        stopMarker.setTitle(context.getString(R.string.minibus_taxi) + " - " + TimeFormattingHelper.formatDate(dateTime));
                        stopMarker.setSnippet(context.getString(R.string.board_taxi));
                        stopMarker.setIcon(transferIcon);
                    }

                    map.addMarker(stopMarker);
                }
            }

            map.addPolyline(line);
        }
    }

//    public static Map<String, Marker> drawStepsOnMap(Context context, MapboxMap map, List<StepModel> steps) {
//
//        HashMap<String, Marker> markerMap = new HashMap<>();
//
//        for (StepModel step : steps) {
//            if (step.getStepType() == StepModel.StepType.TRANSIT) {
//                markerMap.putAll(drawTransitStepOnMap(context, map, (TransitStepModel) step));
//            } else if (step.getStepType() == StepModel.StepType.WALKING) {
//                drawWalkingStepOnMap(context, map, (WalkingStepModel) step);
//            }
//        }
//
//        return markerMap;
//    }
//
//    public static Map<String, Marker> drawTransitStepOnMap(Context context, MapboxMap map, TransitStepModel transitStep) {
//        final int lineWidthPixels = context.getResources().getDimensionPixelSize(R.dimen.waypoint_map_line_width);
//        final int markerWidth = context.getResources().getDimensionPixelSize(R.dimen.waypoint_intermediate_map_marker_width);
//        final int markerHeight = context.getResources().getDimensionPixelSize(R.dimen.waypoint_intermediate_map_marker_height);
//        final int transferMarkerWidth = context.getResources().getDimensionPixelSize(R.dimen.waypoint_transfer_map_marker_width);
//        final int transferMarkerHeight = context.getResources().getDimensionPixelSize(R.dimen.waypoint_transfer_map_marker_height);
//
//        Map<String, Marker> markerMap = new HashMap<>();
//
//        PolylineOptions line = new PolylineOptions();
//        line.addAll(convertPointsToLatLng(transitStep.getTrip().getGeometryList()));
//
//        int lineColourPackedInt = Color.parseColor(transitStep.getTrip().getLine().getColour());
//
//        line.color(lineColourPackedInt);
//        line.width(lineWidthPixels);
//
//        Icon transferIcon = BitmapHelper.getVectorAsMapBoxIcon(context, R.drawable.ic_start_and_end_of_leg_icon, transferMarkerWidth, transferMarkerHeight);
//        Icon stopIcon = BitmapHelper.getIntermediateStopAsMapBoxIcon(context, markerWidth, markerHeight, lineColourPackedInt);
//
//        final int numWaypoints = transitStep.getTrip().getWaypoints().size();
//        for (int i = 0; i < numWaypoints; i++) {
//            final WaypointModel waypoint = transitStep.getTrip().getWaypoints().get(i);
//
//            Date dateTime = waypoint.getDepartureTime();
//
//            MarkerOptions stopMarker = new MarkerOptions();
//            stopMarker.setPosition(new LatLng(waypoint.getGeometry().getLatitude(), waypoint.getGeometry().getLongitude()));
//
//            if (waypoint.getWaypointType() == WaypointModel.WaypointType.STOP) {
//                final StopWaypointModel stopWaypoint = (StopWaypointModel) waypoint;
//
//                stopMarker.setTitle(stopWaypoint.getStop().getName());
//                stopMarker.setSnippet(stopWaypoint.getStop().getAgency().getName());
//                stopMarker.setIcon(stopIcon);
//                if (i == 0 || i == numWaypoints - 1) {
//                    stopMarker.setIcon(transferIcon);
//                }
//                markerMap.put(stopWaypoint.getStop().getId(), map.addMarker(stopMarker));
//            } else if (waypoint.getWaypointType() == WaypointModel.WaypointType.HAIL) {
//
//                stopMarker.setTitle(context.getString(R.string.minibus_taxi) + " - " + TimeFormattingHelper.formatDate(dateTime));
//
//                if (i == 0) {
//                    stopMarker.setSnippet(context.getString(R.string.board_taxi));
//                } else if (i == numWaypoints - 1) {
//                    stopMarker.setSnippet(context.getString(R.string.depart_taxi));
//                }
//
//                stopMarker.setIcon(transferIcon);
//                map.addMarker(stopMarker);
//            }
//        }
//
//        map.addPolyline(line);
//
//        return markerMap;
//    }
//
//    public static void drawWalkingStepOnMap(Context context, MapboxMap map, WalkingStepModel walkingStep) {
//        final int lineWidthPixels = context.getResources().getDimensionPixelSize(R.dimen.waypoint_map_line_width);
//
//        PolylineOptions line = new PolylineOptions();
//        line.addAll(convertPointsToLatLng(walkingStep.getTrip().getGeometryList()));
//        line.color(Color.BLACK);
//        line.width(lineWidthPixels);
//
//        map.addPolyline(line);
//    }
//
//    public static List<LatLng> convertPointsToLatLng(List<Point> points) {
//        ArrayList<LatLng> latLngList = new ArrayList<>();
//        for (Point p : points) {
//            latLngList.add(fromPoint(p));
//        }
//        return latLngList;
//    }
//
//    public static LatLng fromPoint(Point point) {
//        return new LatLng(point.getLatitude(), point.getLongitude());
//    }
}
