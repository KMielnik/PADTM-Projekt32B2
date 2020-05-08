package com.example.projekt32b2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.projekt32b2.tracksManagement.Track;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class TrackListActivity extends AppCompatActivity {

    private List<Track> trackList;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_list);

        gson = new Gson();
        trackList = new ArrayList<>();
        seedTrackList();
    }

    public void onFirstTrackButtonClick(View view) {
        Intent i = new Intent(this, RunActivity.class);
        i.putExtra("track", gson.toJson(trackList.get(0)));
        startActivity(i);
    }

    private void seedTrackList() {
        for (int i = 0; i < 5; i++) {
            Track track = new Track("Trasa: " + (i + 1));
            track.AddCheckpoint(new LatLng(i + 5, 3));
            track.AddCheckpoint(new LatLng(i + 2, 3 + i));
            track.AddCheckpoint(new LatLng(5, 1));
            track.AddCheckpoint(new LatLng(7, 3 + i + i));

            trackList.add(track);
        }
    }
}
