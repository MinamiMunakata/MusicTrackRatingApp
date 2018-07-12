package com.minami.android.musictrackratingapp;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText mEditText;
    private Spinner mSpinner;
    private ListView mListView;
    private ArrayList<Artist> mArtists;
    private ArrayAdapter<Artist> mArtistArrayAdapter;
    private DatabaseReference artistsRef;

    private Artist tempArtist;

    private int id = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEditText = findViewById(R.id.artistET);
        mSpinner = findViewById(R.id.genres);
        mListView = findViewById(R.id.artistListView);
        mArtists = new ArrayList<>();
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        artistsRef = database.getReference("artists");
        // TODO tracksRef
        mArtistArrayAdapter= new ArrayAdapter<Artist>(
                MainActivity.this,
                android.R.layout.simple_list_item_1,
                mArtists
        );

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                // 1. which artist is clicked?
                Artist artist = mArtists.get(position);
                tempArtist = artist;
                // 2. show the dialog
                showUpdateDialog(artist);
                return true;  // TODO
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Artist artist = mArtists.get(position);
                Intent intent = new Intent(MainActivity.this, TrackActivity.class);
                intent.putExtra("artist_id", artist.getId());
                MainActivity.this.startActivity(intent);

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        artistsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mArtists.clear();
                for (DataSnapshot artistSnapshot: dataSnapshot.getChildren()) {

                    Artist artist = artistSnapshot.getValue(Artist.class);// {id..., name..., genre...} => put it in a "class"
                    mArtists.add(artist);
                }

                mArtistArrayAdapter.notifyDataSetChanged();
                mListView.setAdapter(mArtistArrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // when there are some errors
                if(databaseError != null){
                    // must make the context MainActivity.this because
                    Toast.makeText(MainActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private int getIndexForGenre(String genre){
        switch (genre){
            case "Pop":
                return 0;
            case "Rock":
                return 1;
            case "Hip-Hop":
                return 2;
            case "Classics":
                return 3;
            case "Samba":
                return 4;
            case "Reggae":
                return 5;
            case "K-Pop":
                return 6;
            case "EDM":
                return 7;
            default:
                return 0;
        }

    }
    private void showUpdateDialog(final Artist artist){
        // 1. build the dialog with the custom layout
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.edit_artist_dialog, null);
        builder.setView(dialogView);

        final EditText editText = dialogView.findViewById(R.id.dialog_et);
        editText.setText(artist.getName());
        final Spinner spinner = dialogView.findViewById(R.id.dialog_spinner);
        spinner.setSelection(getIndexForGenre(artist.getGenre()));
        builder.setTitle("Update " + artist.getName());
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        // 2. set click listener for update and delete buttons
        Button update_btn = dialogView.findViewById(R.id.dialog_update_btn);
        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                artist.setName(editText.getText().toString());
                artist.setGenre(spinner.getSelectedItem().toString());
                if (TextUtils.isEmpty(editText.getText().toString())){
                    return;
                }
                artist.setTracks(tempArtist.getTracks());
                artistsRef.child(artist.getId()).setValue(artist, new DatabaseReference.CompletionListener(){

                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        Toast.makeText(MainActivity.this,
                                "Successfully updated the Artist",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                alertDialog.dismiss();


            }
        });
        Button delete_btn = dialogView.findViewById(R.id.dialog_delete_btn);
        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                artistsRef.child(artist.getId()).removeValue(new DatabaseReference.CompletionListener(){

                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        if (databaseError == null){
                            Toast.makeText(MainActivity.this,
                                    "Successfully removed the Artist",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                alertDialog.dismiss();

            }
        });


    }

    public void addArtist(View view) {
        String artist_name = mEditText.getText().toString().trim(); // it's always a good idea to have a trim method.
        String artist_genre = mSpinner.getSelectedItem().toString();
        if (!TextUtils.isEmpty(artist_name)){
            // 1. Generate an unique id
            String artist_id = artistsRef.push().getKey();

            // 2. Create an Artist object using the id
            Artist artist = new Artist(artist_id, artist_name, artist_genre);
            // 3. Adding the Artist as a child of the artists
            artistsRef.child(artist_id).setValue(artist);

            mArtists.add(artist);
            mArtistArrayAdapter.notifyDataSetChanged();
            mEditText.setText("");
        } else {
            Toast.makeText(this, "Please Enter the Artist name...", Toast.LENGTH_SHORT).show();
        }



    }
}
