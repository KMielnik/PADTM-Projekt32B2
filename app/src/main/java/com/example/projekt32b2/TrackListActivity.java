package com.example.projekt32b2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.projekt32b2.tracksManagement.Track;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class TrackListActivity extends AppCompatActivity {

    private List<Track> trackList;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private Gson gson;

    private int LAUNCH_CREATE_TRACK_ACTIVITY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_list);

        trackList = new ArrayList<>();

        recyclerView = findViewById(R.id.tracks_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new TrackListAdapter(trackList, new TrackListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Track item) {
                Intent i = new Intent(getApplicationContext(), RunActivity.class);
                i.putExtra("track", gson.toJson(item));
                startActivity(i);
            }

            @Override
            public void onItemLongClick(Track item) {
                int i = trackList.indexOf(item);
                trackList.remove(i);
                mAdapter.notifyItemRemoved(i);
            }
        });
        recyclerView.setAdapter(mAdapter);

        gson = new Gson();

        seedTrackList();
    }

    public void onNewTrackButtonClick(View view) {
        Intent i = new Intent(this, CreateTrackActivity.class);
        startActivityForResult(i, LAUNCH_CREATE_TRACK_ACTIVITY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LAUNCH_CREATE_TRACK_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                Track track = gson.fromJson(data.getStringExtra("track"), Track.class);
                trackList.add(track);
                mAdapter.notifyItemInserted(trackList.size() - 1);
            }
        }
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

        Track szczecinTrack=new Track("Szczecin");
        szczecinTrack.AddCheckpoint(new LatLng(53.428555, 14.532046),10);
        szczecinTrack.AddCheckpoint(new LatLng(53.429154, 14.532199),10);
        szczecinTrack.AddCheckpoint(new LatLng(53.430524, 14.532502),10);
        szczecinTrack.AddCheckpoint(new LatLng(53.431670, 14.532698),10);
        szczecinTrack.AddCheckpoint(new LatLng(53.431366, 14.534514 ),10);
        trackList.add(szczecinTrack);
    }
}
