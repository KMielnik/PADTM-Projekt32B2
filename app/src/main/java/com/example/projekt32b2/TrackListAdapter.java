package com.example.projekt32b2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projekt32b2.tracksManagement.Track;
import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Text;

import java.util.List;

public class TrackListAdapter extends RecyclerView.Adapter<TrackListAdapter.MyViewHolder> {

    private List<Track> tracks;
    private final OnItemClickListener listener;
    private LatLng userLocation;

    public interface OnItemClickListener {
        void onItemClick(Track item);
        void onItemLongClick(Track item);
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView track_name_textview;
        public TextView track_length_textview;
        public TextView distance_from_start_textview;

        public MyViewHolder(View v) {
            super(v);

            track_name_textview = v.findViewById(R.id.track_name);
            track_length_textview = v.findViewById(R.id.length_textview);
            distance_from_start_textview = v.findViewById(R.id.distance_from_start_textview);
        }

        public void bind(final Track item, final OnItemClickListener listener) {
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.onItemLongClick(item);
                    return true;
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }

    }

    public void setUserLocation(LatLng userLocation){
        this.userLocation = userLocation;
    }

    public TrackListAdapter(List<Track> tracks, OnItemClickListener clickListener) {
        this.tracks = tracks;
        this.listener = clickListener;
    }

    @NonNull
    @Override
    public TrackListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_track, parent, false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackListAdapter.MyViewHolder holder, int position) {
        Track track = tracks.get(position);

        holder.track_name_textview.setText(track.Name);
        holder.track_length_textview.setText(String.format("%.2f", track.getTrackLength()) + "m");
        if(userLocation==null)
            holder.distance_from_start_textview.setText("Unkown distance");
        else
            holder.distance_from_start_textview.setText(String.format("%.2f", Utilities.distanceInMeters(userLocation, track.getStartingLocation())) + "m");
        holder.bind(tracks.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return tracks.size();
    }
}
