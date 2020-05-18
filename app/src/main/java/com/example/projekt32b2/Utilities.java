package com.example.projekt32b2;

import com.google.android.gms.maps.model.LatLng;

public class Utilities {

    public static double distanceInMeters(double lat1, double lon1, double lat2, double lon2) {
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        } else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            dist = dist * 1.609344;
            return dist * 1000;
        }
    }

    public static double distanceInMeters(LatLng latlng1, LatLng latlng2) {
        return distanceInMeters(latlng1.latitude, latlng1.longitude, latlng2.latitude, latlng2.longitude);
    }
}
