package com.example.projekt32b2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projekt32b2.tracksManagement.Track;

import org.w3c.dom.Text;

import java.util.List;

public class TrackListAdapter extends RecyclerView.Adapter<TrackListAdapter.MyViewHolder> {

    private List<Track> tracks;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Track item);
        void onItemLongClick(Track item);
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView textView;
        public MyViewHolder(View v) {
            super(v);

            textView = v.findViewById(R.id.track_name);
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

        holder.textView.setText(track.Name);
        holder.bind(tracks.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return tracks.size();
    }
}