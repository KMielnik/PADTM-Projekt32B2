package com.example.projekt32b2.tracksManagement;

import com.example.projekt32b2.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class UserMarker {
    public LatLng position;
    public double markerRadius;
    private Marker marker;

    public UserMarker(LatLng position) {
        this.position = position;
        markerRadius = 10.;
    }

    public void PlaceMarker(GoogleMap map) {
        MarkerOptions newMarker = new MarkerOptions()
                .position(position)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.person))
                .draggable(false);

        marker = map.addMarker(newMarker);
    }

    public void RemoveMarker(){
        marker.remove();
        marker = null;
    }
}
