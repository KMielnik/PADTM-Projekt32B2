package com.example.projekt32b2.tracksManagement;

import android.graphics.Color;

import com.example.projekt32b2.Utilities;
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

    public void AddCheckpoint(LatLng position, double markerRadius) {
        checkpoints.add(new Checkpoint(position, markerRadius));
    }

    public void PlaceTrackOnMap(GoogleMap map, boolean showPath) {
        List<LatLng> positions = new ArrayList<>();

        for (int i = 0; i < checkpoints.size(); i++) {
            CheckpointType type;
            if (i == 0)
                type = CheckpointType.START;
            else if (i == checkpoints.size() - 1)
                type = CheckpointType.FINISH;
            else
                type = CheckpointType.CHECKPOINT;

            checkpoints.get(i).PlaceMarker(map, type);

            positions.add(checkpoints.get(i).position);
        }

        if (showPath) {
            polyline = map.addPolyline(new PolylineOptions()
                    .addAll(positions)
                    .width(5)
                    .color(Color.RED));
        }
    }

    public void RefreshUserTrack(GoogleMap map) {
        List<LatLng> positions = new ArrayList<>();

        for (int i = 0; i < checkpoints.size(); i++) {
            positions.add(checkpoints.get(i).position);
        }

        if (checkpoints.size() > 2)
            polyline.remove();

        polyline = map.addPolyline(new PolylineOptions()
                .addAll(positions)
                .width(5)
                .color(Color.BLUE));

    }

    public void DeleteLastCheckpoint() {
        if (checkpoints.size() < 1)
            return;

        checkpoints.get(checkpoints.size() - 1).RemoveMarker();
        checkpoints.remove((checkpoints.size() - 1));


    }

    public double getTrackLength() {
        double length = 0;
        for (int i = 0; i < checkpoints.size() - 1; i++) {
            length += Utilities.distanceInMeters(checkpoints.get(i).position, checkpoints.get(i + 1).position);
        }
        return length;
    }

    public LatLng getStartingLocation() {
        if (checkpoints.size()>0)
            return checkpoints.get(0).position;
        return null;
    }

    public void RemoveTrackFromMap() {
        for (Checkpoint checkpoint : checkpoints)
            checkpoint.RemoveMarker();
        checkpoints.clear();

        if (polyline != null)
            polyline.remove();
        polyline = null;
    }

    public void RefreshTrack(GoogleMap mmap) {
        List<LatLng> positions = new ArrayList<>();

        for (int i = 0; i < checkpoints.size(); i++) {
            positions.add(checkpoints.get(i).position);
        }

        RemoveTrackFromMap();

        for (LatLng position : positions)
            AddCheckpoint(position);

        PlaceTrackOnMap(mmap, true);
    }

    public List<Checkpoint> getCheckpoints() {
        return checkpoints;
    }
}
