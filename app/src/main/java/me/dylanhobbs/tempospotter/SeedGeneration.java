package me.dylanhobbs.tempospotter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.TracksPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by dylanhobbs on 14/10/2017.
 */

public class SeedGeneration extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seed_generation);

        final Context current = this;

        Intent intent = getIntent();
        String selectedTrack = intent.getStringExtra(MainActivity.TRACK_MESSAGE);
        SpotifyService spotify = MainActivity.spotify;

        spotify.getMyPlaylists(new Callback<Pager<PlaylistSimple>>() {
            @Override
            public void success(Pager<PlaylistSimple> playlistSimplePager, Response response) {
                List<PlaylistSimple> list = playlistSimplePager.items;
                ArrayList useableList = (ArrayList) list;

                // Get the list
                ListView listView = (ListView) findViewById(R.id.playlist_result_list);

                // Add to adapter
                PlaylistArrayAdapter playlistArrayAdapter = new PlaylistArrayAdapter(
                        current,
                        android.R.layout.simple_list_item_1,
                        useableList);

                // Set adapter
                listView.setAdapter(playlistArrayAdapter);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("Me failure", error.toString());
            }
        });
    }

    protected void longTermTop(View view){

    }

    protected void mediumTermTop(View view){

    }

    protected void shortTermTop(View view){

    }


}
