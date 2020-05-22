package com.example.projekt32b2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.projekt32b2.tracksManagement.Track;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TrackListActivity extends AppCompatActivity {

    private final int LAUNCH_CREATE_TRACK_ACTIVITY = 1;
    private final int LAUNCH_RUN_ACTIVITY = 2;
    private final String SAVED_TRACKS_STRING = "savedTracks";
    private List<Track> trackList;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private Gson gson;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_list);

        sharedPreferences = getPreferences(Context.MODE_PRIVATE);

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
                startActivityForResult(i, LAUNCH_RUN_ACTIVITY);
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

        if (sharedPreferences.contains(SAVED_TRACKS_STRING)) {
            Track[] tracks = gson.fromJson(sharedPreferences.getString(SAVED_TRACKS_STRING, "{}"), Track[].class);

            trackList.addAll(new ArrayList<Track>(Arrays.asList(tracks)));
        }

        if (trackList.size() == 0)
            seedTrackList();
    }

    public void onNewTrackButtonClick(View view) {
        Intent i = new Intent(this, CreateTrackActivity.class);
        startActivityForResult(i, LAUNCH_CREATE_TRACK_ACTIVITY);
    }

    public void onClearTracksClicked(View view) {
        trackList.clear();
        mAdapter.notifyDataSetChanged();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SAVED_TRACKS_STRING, gson.toJson(trackList.toArray()));
        editor.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case LAUNCH_CREATE_TRACK_ACTIVITY: {
                if (resultCode == Activity.RESULT_OK) {
                    Track track = gson.fromJson(data.getStringExtra("track"), Track.class);
                    trackList.add(track);
                    mAdapter.notifyItemInserted(trackList.size() - 1);

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(SAVED_TRACKS_STRING, gson.toJson(trackList.toArray()));
                    editor.commit();
                }
                break;
            }

            case LAUNCH_RUN_ACTIVITY: {
                if (resultCode == Activity.RESULT_OK) {
                    ArrayList<Long> times = gson.fromJson(data.getStringExtra("times"), ArrayList.class);
                    String trackName = gson.fromJson(data.getStringExtra("trackName"), String.class);

                    Toast.makeText(getApplicationContext(), "Ukończono trase: " + trackName + " w " + times.get(times.size() - 1) + " s.", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    private void seedTrackList() {
        Track szczecinTrack = new Track("Szczecin");
        szczecinTrack.AddCheckpoint(new LatLng(53.428555, 14.532046), 10);
        szczecinTrack.AddCheckpoint(new LatLng(53.429154, 14.532199), 10);
        szczecinTrack.AddCheckpoint(new LatLng(53.430524, 14.532502), 10);
        szczecinTrack.AddCheckpoint(new LatLng(53.431670, 14.532698), 10);
        szczecinTrack.AddCheckpoint(new LatLng(53.431366, 14.534514), 10);
        trackList.add(szczecinTrack);
    }
}
