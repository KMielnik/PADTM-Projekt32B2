package com.example.projekt32b2;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Toast;

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
import java.util.Timer;

public class RunActivity extends FragmentActivity implements OnMapReadyCallback {

    LocationRequest _locationRequest;
    List<Checkpoint> trackCheckpoints;
    private GoogleMap mMap;
    private FusedLocationProviderClient _fused;
    private LocationCallback locationCallback;
    private UserMarker userMarker;
    private float zoomValue = 18.5f;
    private Track userTrack;
    private long startTime;
    private int currentCheckpoint;
    private List<Long> timesList = new ArrayList<>();
    private boolean isTrackFinished = false;
    private Track track;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        createLocationRequest();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    double result = 0;
                    if (userMarker != null && isTrackFinished == false) {
                        if (Utilities.distanceInMeters(latLng, trackCheckpoints.get(currentCheckpoint).position) < 12 && !(trackCheckpoints.get(currentCheckpoint).type == CheckpointType.CHECKPOINT_CHECKED)) {
                            long elapsedTime = (SystemClock.elapsedRealtime() - startTime) / 1000;
                            timesList.add(elapsedTime);
                            trackCheckpoints.get(currentCheckpoint).RemoveMarker();
                            trackCheckpoints.get(currentCheckpoint).PlaceMarker(mMap, CheckpointType.CHECKPOINT_CHECKED);
                            Toast.makeText(getApplicationContext(), "time elapsed: " + String.valueOf(elapsedTime), Toast.LENGTH_SHORT).show();
                            currentCheckpoint++;
                            if (trackCheckpoints.size() == currentCheckpoint) {
                                isTrackFinished = true;
                                findViewById(R.id.chronometer).setVisibility(View.INVISIBLE);
                                Button button = findViewById(R.id.button6);
                                button.setVisibility(View.VISIBLE);
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
                            .zoom(mMap.getCameraPosition().zoom < 14 ? zoomValue : mMap.getCameraPosition().zoom)
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
        this.track = track;
        _fused = LocationServices.getFusedLocationProviderClient(this);
        startLocationUpdates();
    }

    protected void createLocationRequest() {
        _locationRequest = LocationRequest.create();
        _locationRequest.setInterval(1500);
        _locationRequest.setFastestInterval(500);
        _locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    private void startLocationUpdates() {
        _fused.requestLocationUpdates(_locationRequest, locationCallback, Looper.getMainLooper());
    }

    public void StartTimer(View view) {
        Chronometer mChronometer=(Chronometer) findViewById(R.id.chronometer);
        mChronometer.setVisibility(View.VISIBLE);
        mChronometer.setBase(SystemClock.elapsedRealtime());
        mChronometer.start();
        startTime = SystemClock.elapsedRealtime();
        Toast.makeText(getApplicationContext(), "Timer has started", Toast.LENGTH_SHORT).show();
       // final Button button = (Button) findViewById(R.id.button4);
        //button.setVisibility(View.INVISIBLE);


    }

    public void saveTimes(View view) {
        Gson gson = new Gson();

        Intent returnIntent = new Intent();
        returnIntent.putExtra("trackName", track.Name);
        returnIntent.putExtra("times", gson.toJson(timesList));
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }


}
