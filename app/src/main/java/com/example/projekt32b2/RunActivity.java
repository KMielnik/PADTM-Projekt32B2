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
import com.example.projekt32b2.tracksManagement.UserMarker;
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

    LocationRequest _locationRequest;
    List<Checkpoint> trackCheckpoints;
    private GoogleMap mMap;
    private FusedLocationProviderClient _fused;
    private LocationCallback locationCallback;
    private List<Checkpoint> checkpoints;
    private UserMarker userMarker;
    private float zoomValue = 20f;
    private Track userTrack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        createLocationRequest();


        checkpoints = new ArrayList<Checkpoint>();
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    double result = 0;
                    if (userMarker != null) {
                        for (int i = 0; i < trackCheckpoints.size() - 1; i++) {
                            result = Utilities.distanceInMeters(latLng, trackCheckpoints.get(i).position);
                            Log.d("odleglosc do " + String.valueOf(i), String.valueOf(result));
                            if (result < 15) {
                                trackCheckpoints.get(i).RemoveMarker();
                                trackCheckpoints.get(i).PlaceMarker(mMap, CheckpointType.CHECKPOINT_CHECKED);
                                Log.d("success", "Zblizyles sie blisko");
                            }
                        }
                    }
                    if (userMarker == null) {
                        userMarker = new UserMarker(latLng);
                    } else {
                        userMarker.RemoveMarker();
                        userMarker.position = latLng;
                    }
                    userMarker.PlaceMarker(mMap);

                    if (userTrack == null) {
                        userTrack = new Track("userTrack");
                    }

                    userTrack.AddCheckpoint(latLng, 0);
                    userTrack.RefreshUserTrack(mMap);
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

        Gson gson = new Gson();
        String trackJson = getIntent().getExtras().getString("track");
        Track track = gson.fromJson(trackJson, Track.class);

        track.PlaceTrackOnMap(googleMap, true);
        trackCheckpoints = track.getCheckpoints();

        _fused = LocationServices.getFusedLocationProviderClient(this);
        startLocationUpdates();
    }

    protected void createLocationRequest() {
        _locationRequest = LocationRequest.create();
        _locationRequest.setInterval(10000);
        _locationRequest.setFastestInterval(5000);
        _locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    private void startLocationUpdates() {
        _fused.requestLocationUpdates(_locationRequest, locationCallback, Looper.getMainLooper());
    }

}
