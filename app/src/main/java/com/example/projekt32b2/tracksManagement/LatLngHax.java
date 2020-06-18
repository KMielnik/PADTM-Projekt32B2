package com.example.projekt32b2.tracksManagement;

import com.google.android.gms.maps.model.LatLng;

public class LatLngHax {
    private Double latitude;
    private Double longitude;



    public LatLngHax(LatLng original) {
        setLatitude(original.latitude);
        setLongitude(original.longitude);
    }
    public LatLngHax(){}

    public LatLng getLatLng() {
        return new LatLng(getLatitude(), getLongitude());
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
