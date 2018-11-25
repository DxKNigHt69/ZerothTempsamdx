package com.whereismytransport.zero;

import android.content.Context;
import android.graphics.Color;

import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import androidx.annotation.Nullable;
import transportapisdk.models.Hail;
import transportapisdk.models.Itinerary;
import transportapisdk.models.Leg;
import transportapisdk.models.LineString;
import transportapisdk.models.Point;
import transportapisdk.models.Stop;
import transportapisdk.models.Waypoint;

// This is where the magic happens.
public final class MapboxHelper {

    public static void drawItineraryOnMap(final Context context, final MapboxMap mapboxMap, final Itinerary itinerary) {

        // The width of all Polylines to be drawn on the map.
        final int mapLineWidthPixels = context.getResources().getDimensionPixelSize(R.dimen.waypoint_map_line_width);

        // Dimensions of markers that represent intermediate stop Waypoints on the map.
        final int markerWidth = context.getResources().getDimensionPixelSize(R.dimen.waypoint_intermediate_map_marker_width);
        final int markerHeight = context.getResources().getDimensionPixelSize(R.dimen.waypoint_intermediate_map_marker_height);

        // An Itinerary consists of Legs. A Leg can be either a Transit Leg, or a Walking Leg.
        // We'll loop over each Leg in the Itinerary and add it to a Mapbox Polyline for drawing
        // it on the map.
        for (Leg leg : itinerary.getLegs()) {

            // Polyline is a Mapbox construct that we need in order to draw our Leg on the line.
            final PolylineOptions legPolyline = convertLegGeometryToPolyline(leg);
            legPolyline.width(mapLineWidthPixels);

            // Let's draw Walking Legs and Transit Legs slightly differently. To do this, check
            // the Leg Type, either Walking or Transit.

            if (leg.getType().equalsIgnoreCase("walking")) {
                // Draw the Walking Leg.

                // We'll draw a Walking Leg is a simple black line.
                legPolyline.color(Color.BLACK);

            } else if (leg.getType().equalsIgnoreCase("transit")) {
                // Draw the Transit Leg.

                // We'll draw a Transit Leg in the colour of the Transit Leg's Line colour. We can
                // get a String representation of the colour from the Leg.
                String lineColourString = leg.getLine().getColour();

                // Convert the colour String to a packed int colour.
                int packedIntLineColour = Color.parseColor(lineColourString);

                // Colour the PolyLine.
                legPolyline.color(packedIntLineColour);

                // We'll draw all our intermediate Waypoints with the same colour as the Line colour.
                Icon waypointIcon = BitmapHelper.getIntermediateStopAsMapBoxIcon(context, markerWidth, markerHeight, packedIntLineColour);

                // We'll now loop over each Waypoint in the Leg and add all the Waypoints to be
                // drawn! A Waypoint can be one of three different types, either Walking,
                // Stop, or Hail. Since we're dealing with a Transit Leg here, no Walking Waypoints
                // will occur, so we can ignore them and focus only on Stop and Hail Waypoints.

                for (int i = 0; i < leg.getWaypoints().size(); i++) {

                    final Waypoint waypoint = leg.getWaypoints().get(i);

                    Date departureDate;
                    try {
                        departureDate = parseIsoDateString(waypoint.getDepartureTime());
                    } catch (ParseException e) {
                        departureDate = null;
                    }

                    if (waypoint.getStop() != null) {
                        // This Waypoint represents a formal Stop.

                        MarkerOptions stopMarker = convertStopToMarker(waypoint.getStop(), departureDate);
                        stopMarker.setIcon(waypointIcon);
                        mapboxMap.addMarker(stopMarker);

                    } else if (waypoint.getHail() != null) {
                        // This Waypoint represents an informal Hailing section.

                        MarkerOptions hailMarker = convertHailToMarker(context, waypoint.getHail(), departureDate);
                        hailMarker.setIcon(waypointIcon);

                        if (i == 0) {
                            // Is this Hail Waypoint on the start of the Leg?

                            hailMarker.setSnippet(context.getString(R.string.board_taxi));
                        } else if (i == leg.getWaypoints().size() - 1) {
                            // Is this Hail Waypoint on the end of the Leg?

                            hailMarker.setSnippet(context.getString(R.string.depart_taxi));
                        }

                        mapboxMap.addMarker(hailMarker);

                    }
                }
            }

            mapboxMap.addPolyline(legPolyline);
        }
    }

    private static PolylineOptions convertLegGeometryToPolyline(Leg leg) {
        // A LineString contains all the coordinates that represent this Leg. This is what we
        // need to draw a nice long line on the map.
        LineString lineString = leg.getGeometry();

        // Convert the LineString into a List LatLng points so we can draw it on our map.
        ArrayList<LatLng> coordinateList = new ArrayList<>();
        for (List<Double> coordinates : lineString.getCoordinates()) {
            // Latitude - Longitude :)
            coordinateList.add(new LatLng(coordinates.get(1), coordinates.get(0)));
        }

        PolylineOptions polyLine = new PolylineOptions();
        polyLine.addAll(coordinateList);

        return polyLine;
    }

    private static MarkerOptions convertStopToMarker(Stop stop, @Nullable Date departureDate) {
        String stopTime = departureDate != null ? formatDate(departureDate) : "(No time available)";

        LatLng stopLatLong = new LatLng(stop.getGeometry().getCoordinates()[1], stop.getGeometry().getCoordinates()[0]);

        MarkerOptions stopMarker = new MarkerOptions();
        stopMarker.setPosition(stopLatLong);
        stopMarker.setTitle(stop.getName() + " - " + stopTime);
        stopMarker.setSnippet(stop.getAgency().getName());

        return stopMarker;
    }

    private static MarkerOptions convertHailToMarker(Context context, Hail hail, @Nullable Date departureDate) {
        String hailTime = departureDate != null ? formatDate(departureDate) : "(No time available)";

        Point point = (Point) hail.getGeometry();
        LatLng stopLatLong = new LatLng(point.getCoordinates()[1], point.getCoordinates()[0]);

        MarkerOptions hailMarker = new MarkerOptions();
        hailMarker.setPosition(stopLatLong);
        hailMarker.setTitle(context.getString(R.string.share_taxi) + " - " + hailTime);

        return hailMarker;
    }

    /**
     * Parses a string representation of a date.
     *
     * @param isoDateString A date string in the format yyyy-MM-dd'T'HH:mm:ss'Z'}.
     * @return A {@link Date} instance in the UTC timezone.
     * @throws ParseException If the isoDateString is invalid.
     */
    private static Date parseIsoDateString(String isoDateString) throws ParseException {
        DateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        isoDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        if (isoDateString.length() > 21) {
            isoDateString = isoDateString.substring(0, 19) + "Z";
        }

        return isoDateFormat.parse(isoDateString);
    }

    private static String formatDate(Date dateTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        dateFormat.setTimeZone(TimeZone.getDefault());
        return dateFormat.format(dateTime);
    }
}
