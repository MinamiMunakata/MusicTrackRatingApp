package com.minami.android.musictrackratingapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TrackActivity extends AppCompatActivity {
    String artist_id;
    ListView trackListView;
    ArrayList<Track> tracks;
    SeekBar mSeekBar;
    TrackAdapter adapter;
    String rate = String.valueOf(0);
    private DatabaseReference artistsRef;
    EditText track_et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);
        setContentView(R.layout.activity_track);
        Intent intent = getIntent();
        artist_id = intent.getStringExtra("artist_id");
        TextView artist_name_tv = findViewById(R.id.artist_name_tv);
        trackListView = findViewById(R.id.trackListView);
        tracks = new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        artistsRef = database.getReference("artists");
        track_et = findViewById(R.id.track_et);
        mSeekBar = findViewById(R.id.seekBar);
        mSeekBar.setMax(5);
        mSeekBar.setProgress(0);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                rate = String.valueOf(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        artistsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tracks.clear();
                for (DataSnapshot trackData: dataSnapshot.child(artist_id).child("tracks").getChildren()) {
                    Track track = trackData.getValue(Track.class);
                    tracks.add(track);
                }
//                for (int i = 0; i < tracks.size(); i++) {
//                    tracks.get(i).setIndex(i);
//                }

                adapter = new TrackAdapter(TrackActivity.this);
                adapter.setTracks(tracks);
                trackListView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void addTrack(View view) {
        int index = tracks.size();
        String title = track_et.getText().toString().trim();
        if (!TextUtils.isEmpty(title)) {
//            String track_id = artistsRef.child(artist_id).child("tracks").push().getKey();
            Track track = new Track(title, rate);
            tracks.add(track);



//            Track track = new Track(index, track_id, title, rate);


            artistsRef.child(artist_id).child("tracks").setValue(tracks);
//            artistsRef.child(artist_id).child("tracks").child(track_id).setValue(track);

            adapter.notifyDataSetChanged();
            track_et.setText("");

        } else {
            Toast.makeText(this, "Please Enter the title...", Toast.LENGTH_SHORT).show();

        }

    }
}

class TrackAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater = null;
    ArrayList<Track> tracks;

    public TrackAdapter(Context context) {
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setTracks(ArrayList<Track> tracks) {
        this.tracks = tracks;
    }

    @Override
    public int getCount() {
        return tracks.size();
    }

    @Override
    public Object getItem(int position) {
        return tracks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
//        return tracks.get(position).getIndex();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.track_list_layout, parent, false);
        ((TextView) convertView.findViewById(R.id.track_title)).setText(tracks.get(position).getTitle());
        ((TextView) convertView.findViewById(R.id.track_rate)).setText(tracks.get(position).getRate());
        return convertView;
    }
}
