package com.example.projekt32b2;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

import com.example.projekt32b2.tracksManagement.Checkpoint;
import com.example.projekt32b2.tracksManagement.CheckpointType;
import com.example.projekt32b2.tracksManagement.Track;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class RunActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient _fused;
    LocationRequest _locationRequest;
    private LocationCallback locationCallback;
    private List<Checkpoint> checkpoints;
    private float zoomValue=14f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        createLocationRequest();


        checkpoints=new ArrayList<Checkpoint>();
        locationCallback=new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult==null) {
                    return;
                }
                for ( Location location: locationResult.getLocations()) {
                    LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
                    if(!checkpoints.isEmpty())
                    {
                        checkpoints.get(0).RemoveMarker();
                        checkpoints.remove(0);
                    }

                    checkpoints.add(new Checkpoint(latLng));
                    checkpoints.get(0).PlaceMarker(mMap, CheckpointType.HUMAN);
                    CameraPosition bottomPos = new CameraPosition.Builder()
                            .bearing(location.getBearing())
                            .target(latLng)
                            .tilt(0)
                            .zoom(zoomValue)
                            .build();

                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(bottomPos));
                }
            }
        };
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


       /* Gson gson = new Gson();
        String trackJson = getIntent().getExtras().getString("track");
        Track track = gson.fromJson(trackJson, Track.class);

        track.PlaceTrackOnMap(googleMap, true);*/
        _fused=LocationServices.getFusedLocationProviderClient(this);
        startLocationUpdates();
    }

    protected void createLocationRequest() {
        _locationRequest=LocationRequest.create();
        _locationRequest.setInterval(10000);
        _locationRequest.setFastestInterval(5000);
        _locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }



    private void startLocationUpdates() {
        _fused.requestLocationUpdates(_locationRequest,locationCallback, Looper.getMainLooper());
    }

}
