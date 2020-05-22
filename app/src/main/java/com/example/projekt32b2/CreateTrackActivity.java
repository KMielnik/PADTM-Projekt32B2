package com.example.projekt32b2;

import androidx.fragment.app.FragmentActivity;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.projekt32b2.tracksManagement.Checkpoint;
import com.example.projekt32b2.tracksManagement.Track;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

import java.util.List;

public class CreateTrackActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private Track newTrack;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_track);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        newTrack = new Track("track");
    }

    public void onSaveClicked(View view) {
        String track_name = ((EditText) findViewById(R.id.track_name_textedit)).getText().toString();

        if (track_name.equals("")) {
            Toast.makeText(this, "Enter track name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newTrack.getCheckpoints().size() < 2) {
            Toast.makeText(this, "Place at least two points on track.", Toast.LENGTH_SHORT).show();
            return;
        }

        Gson gson = new Gson();

        Intent returnIntent = new Intent();

        Track shallowTrack = new Track(track_name);

        for (Checkpoint checkpoint : newTrack.getCheckpoints()) {
            shallowTrack.AddCheckpoint(checkpoint.position);
        }

        returnIntent.putExtra("track", gson.toJson(shallowTrack));
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    public void onClearButtonClick(View view) {
        newTrack.RemoveTrackFromMap();
        newTrack.RefreshTrack(mMap);
    }

    public void onUndoButtonClick(View view) {
        newTrack.DeleteLastCheckpoint();
        newTrack.RefreshTrack(mMap);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                newTrack.AddCheckpoint(latLng);
                newTrack.RefreshTrack(mMap);
            }
        });

        moveCameraToCurrentLocation();
    }

    private void moveCameraToCurrentLocation() {
        CameraPosition bottomPos = new CameraPosition.Builder()
                .bearing(0)
                .target(new LatLng(53.428555, 14.532046))
                .tilt(0)
                .zoom(17)
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(bottomPos));
    }
}
