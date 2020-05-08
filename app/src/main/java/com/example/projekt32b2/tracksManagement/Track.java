package com.example.projekt32b2.tracksManagement;

import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class Track {
    public String Name;

    private List<Checkpoint> checkpoints;
    private Polyline polyline;

    public Track(String name) {
        Name = name;
        checkpoints = new ArrayList<>();
    }

    public void AddCheckpoint(LatLng position) {
        checkpoints.add(new Checkpoint(position));
    }

    public void PlaceTrackOnMap(GoogleMap map, boolean showPath) {
        List<LatLng> positions = new ArrayList<>();

        for(int i=0;i<checkpoints.size();i++)
        {
            CheckpointType type;
            if(i==0)
                type = CheckpointType.START;
            else if (i==checkpoints.size()-1)
                type = CheckpointType.FINISH;
            else
                type = CheckpointType.CHECKPOINT;

            checkpoints.get(i).PlaceMarker(map, type);

            positions.add(checkpoints.get(i).position);
        }

        if(showPath) {
            polyline = map.addPolyline(new PolylineOptions()
                    .addAll(positions)
                    .width(5)
                    .color(Color.RED));
        }
    }

    public void RemoveTrackFromMap() {
        for(Checkpoint checkpoint : checkpoints)
            checkpoint.RemoveMarker();
        checkpoints.clear();

        polyline.remove();
        polyline = null;
    }
}
