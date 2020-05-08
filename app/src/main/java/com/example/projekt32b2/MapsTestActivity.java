package com.example.projekt32b2;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class MapsTestActivity extends FragmentActivity implements OnMapReadyCallback {

    private Marker runnerMarker;
    private Polyline runnersPath;
    private final List<Marker> markers = new ArrayList<>();
    private final List<Circle> markerCircles = new ArrayList<>();

    private final double MARKER_RADIUS = 15.;
    private final float MIN_ZOOM = 14.f;
    private final float MAX_ZOOM = 19.f;
    private GoogleMap mMap;
    private float actuallySelectedColor = BitmapDescriptorFactory.HUE_VIOLET;

    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_test);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void onClearButtonClick(View view) {
        for(Marker marker : markers)
            marker.remove();
        markers.clear();

        for(Circle circle : markerCircles)
            circle.remove();
        markerCircles.clear();
    }

    public void onUndoButtonClick(View view) {
        if(markers.size()>0)
        {
            markers.get(markers.size()-1).remove();
            markers.remove(markers.size()-1);
        }

        if(markerCircles.size()>0)
        {
            markerCircles.get(markerCircles.size()-1).remove();
            markerCircles.remove(markerCircles.size()-1);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        runnerMarker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(0,0))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.person)));

        runnersPath = mMap.addPolyline(new PolylineOptions()
                .width(5)
                .color(Color.RED));

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                MarkerOptions newMarker = new MarkerOptions()
                        .position(latLng)
                        .title("Checkpoint: " + (markers.size() + 1))
                        .flat(true)
                        .icon(BitmapDescriptorFactory.defaultMarker(actuallySelectedColor));

                markers.add(mMap.addMarker(newMarker));

                CircleOptions circleOptions = new CircleOptions();
                circleOptions.center(latLng);
                circleOptions.fillColor((int) actuallySelectedColor);
                circleOptions.radius(MARKER_RADIUS);

                markerCircles.add(mMap.addCircle(circleOptions));

                if(actuallySelectedColor == BitmapDescriptorFactory.HUE_VIOLET)
                    actuallySelectedColor = BitmapDescriptorFactory.HUE_GREEN;
                else
                    actuallySelectedColor = BitmapDescriptorFactory.HUE_VIOLET;
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                runnerMarker.setPosition(latLng);
                List<LatLng> runnersPathPoints = runnersPath.getPoints();
                runnersPathPoints.add(latLng);
                runnersPath.setPoints(runnersPathPoints);
            }
        });

        moveCameraToCurrentLocation();

        mMap.setMinZoomPreference(MIN_ZOOM);
        mMap.setMaxZoomPreference(MAX_ZOOM);
    }

    private void moveCameraToCurrentLocation() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        LatLng startingLocation;
                        if (location != null)
                            startingLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        else
                            startingLocation = new LatLng(53.4515, 14.5281);

                        runnerMarker.setPosition(startingLocation);

                        List<LatLng> runnersPathPoints = runnersPath.getPoints();
                        runnersPathPoints.add(startingLocation);
                        runnersPath.setPoints(runnersPathPoints);
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(startingLocation));
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] persmissions, int[] grantResults) {
        switch (requestCode) {
            case 0: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    moveCameraToCurrentLocation();
                }
            }
        }
    }
}
