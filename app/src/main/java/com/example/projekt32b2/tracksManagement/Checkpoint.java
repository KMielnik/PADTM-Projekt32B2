package com.example.projekt32b2.tracksManagement;

import com.example.projekt32b2.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class Checkpoint {
    public LatLng position;
    public double markerRadius;
    public CheckpointType type;

    private Marker marker;
    private Circle circle;

    public Checkpoint(LatLng position, double markerRadius) {
        this(position);
        this.markerRadius = markerRadius;
    }

    public Checkpoint(LatLng position) {
        this.position = position;
    }

    private BitmapDescriptor _getIcon(CheckpointType type) {
        switch (type) {
            case START:
                return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
            case CHECKPOINT:
                return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN);
            case FINISH:
                return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
            default:
                return BitmapDescriptorFactory.fromResource(android.R.drawable.btn_default);
        }
    }

    public void PlaceMarker(GoogleMap map, CheckpointType type) {
        MarkerOptions newMarker = new MarkerOptions()
                .position(position)
                .icon(_getIcon(type))
                .draggable(false);

        marker = map.addMarker(newMarker);

        CircleOptions circleOptions = new CircleOptions()
                .center(position)
                .fillColor(3)
                .radius(markerRadius);

        circle = map.addCircle(circleOptions);
    }

    public void RemoveMarker(){
        marker.remove();
        marker = null;

        circle.remove();
        circle = null;
    }
}
