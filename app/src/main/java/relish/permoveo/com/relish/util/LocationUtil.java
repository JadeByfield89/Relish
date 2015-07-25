package relish.permoveo.com.relish.util;

import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;

import relish.permoveo.com.relish.model.Place;

/**
 * Created by imacrasko on 18.11.14.
 */
public class LocationUtil {

    public static final int meterConversion = 1609;
    public static final double milesConversion = 0.000621371192;
    public static final double earthRadius = 3958.75;

    public static double distance(double lat_a, double lng_a, double lat_b, double lng_b) {
        double latDiff = Math.toRadians(lat_b - lat_a);
        double lngDiff = Math.toRadians(lng_b - lng_a);
        double a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2) + Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) * Math.sin(lngDiff / 2) * Math.sin(lngDiff / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = earthRadius * c;
        return (distance * meterConversion * milesConversion);
    }

    public static LatLng latLngFromDistance(LatLng latLng, double d, float bearing) {
        double angular = d / 6378.1;
        double latRadians = Math.toRadians(latLng.latitude);
        double lonRadians = Math.toRadians(latLng.longitude);
        double bearingRadians = bearing;

        double lat = Math.asin(Math.sin(latRadians) * Math.cos(angular) + Math.cos(latRadians) * Math.sin(angular) * Math.cos(bearingRadians));
        double lon = lonRadians + Math.atan2(Math.sin(bearingRadians) * Math.sin(angular) * Math.cos(latRadians), Math.cos(angular) - Math.sin(latRadians) * Math.sin(lat));
        return new LatLng(Math.toDegrees(lat), Math.toDegrees(lon));
    }

    public static String distance(Place.Location geoPos, Place.Location geoPos2) {
        double earthRadius = 3958.75;
        double latDiff = Math.toRadians(geoPos.lat - geoPos2.lat);
        double lngDiff = Math.toRadians(geoPos.lng - geoPos2.lng);
        double a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2) + Math.cos(Math.toRadians(geoPos.lat)) * Math.cos(Math.toRadians(geoPos2.lat)) * Math.sin(lngDiff / 2) * Math.sin(lngDiff / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = earthRadius * c;
        float d = (float) (distance * meterConversion);
        return distanceToString(d);
    }

    public static String distanceToString(double d) {
        String dist = "";
        DecimalFormat twoDForm = new DecimalFormat("#.#");
        if (d < 1000) {
            dist = String.valueOf(twoDForm.format(d)) + "m";
        } else {
            double km = d * 0.001d;
            dist = String.valueOf(twoDForm.format(km)) + "km";
        }
        return dist;
    }

    public static boolean isNull(Place.Location location) {
        return location == null || (location.lat == 0 && location.lng == 0);
    }

}
