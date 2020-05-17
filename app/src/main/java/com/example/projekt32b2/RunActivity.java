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

    private GoogleMap mMap;
    private FusedLocationProviderClient _fused;
    LocationRequest _locationRequest;
    private LocationCallback locationCallback;
    private List<Checkpoint> checkpoints;
    private UserMarker userMarker;
    private float zoomValue=20f;
    private Track userTrack;
    List<Checkpoint> szczecinCheckpoints;

    private static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        }
        else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            dist = dist * 1.609344;
            return (dist);
        }
    }

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
                    double result=0;
                    if(userMarker!=null) {
                        for (int i=0;i<szczecinCheckpoints.size()-1;i++)
                        {
                            result = distance(latLng.latitude, latLng.longitude, szczecinCheckpoints.get(i).position.latitude,  szczecinCheckpoints.get(i).position.longitude, "K")*1000;
                            Log.d("odleglosc do "+String.valueOf(i),String.valueOf(result));
                            if (result<10)
                            {
                                szczecinCheckpoints.get(i).RemoveMarker();
                                szczecinCheckpoints.get(i).PlaceMarker(mMap,CheckpointType.CHECKPOINT_CHECKED);
                                Log.d("success","Zblizyles sie blisko");
                            }
                        }
                    }
                    if(userMarker==null) {
                        userMarker=new UserMarker(latLng);
                    }
                    else {
                        userMarker.RemoveMarker();
                        userMarker.position=latLng;
                    }
                    userMarker.PlaceMarker(mMap);

                    if (userTrack==null) {
                        userTrack = new Track("userTrack");
                    }

                    userTrack.AddCheckpoint(latLng,0);
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
        Track szczecinTrack=new Track("szczecin");
        szczecinTrack.AddCheckpoint(new LatLng(53.428555, 14.532046),10);
        szczecinTrack.AddCheckpoint(new LatLng(53.429154, 14.532199),10);
        szczecinTrack.AddCheckpoint(new LatLng(53.430524, 14.532502),10);
        szczecinTrack.AddCheckpoint(new LatLng(53.431670, 14.532698),10);
        szczecinTrack.AddCheckpoint(new LatLng(53.431366, 14.534514 ),10);
        szczecinTrack.PlaceTrackOnMap(mMap,true);
        szczecinCheckpoints=szczecinTrack.getCheckpoints();

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
