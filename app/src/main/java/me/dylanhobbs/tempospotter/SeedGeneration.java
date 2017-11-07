package me.dylanhobbs.tempospotter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.Playlist;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.PlaylistTracksInformation;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.TracksPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by dylanhobbs on 14/10/2017.
 */

public class SeedGeneration extends AppCompatActivity {
    SpotifyService spotify;
    //TODO: Send this information with an intent
    public static String[] seed;
    public static String selectedTrack;
    private String userID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seed_generation);

        final Context current = this;

        Intent intent = getIntent();
        selectedTrack = intent.getStringExtra(MainActivity.TRACK_MESSAGE);
        userID = intent.getStringExtra(MainActivity.USER_ID_MESSAGE);

        spotify = MainActivity.spotify;

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

        final ListView listView = (ListView) findViewById(R.id.playlist_result_list);
        // Set Clickable event
        assert listView != null;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get selected playlist
                Object o = listView.getItemAtPosition(position);
                PlaylistSimple playlist = (PlaylistSimple) o;
                String chosenPlaylistID = playlist.id;
                String chosedUserID = playlist.owner.id;

                // Create a real playlist from the ID
                spotify.getPlaylistTracks(chosedUserID, chosenPlaylistID, new Callback<Pager<PlaylistTrack>>() {
                    @Override
                    public void success(Pager<PlaylistTrack> playlistTrackPager, Response response) {
                        // Get PlaylistTrack from playlist
                        List<PlaylistTrack> platListTrackList = playlistTrackPager.items;

                        // Get tracks from Playlist Tracks
                        ArrayList<Track> trackList = new ArrayList<>();
                        for (int i = 0; i < platListTrackList.size(); i++) {
                            trackList.add(platListTrackList.get(i).track);
                        }

                        String[] trackSeedGeneration = translateAndShuffle(trackList);
                        goToRecGeneration(trackSeedGeneration);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.d("Failed to get tracks", error.toString());
                    }
                });
            }
        });
    }

    protected void longTermTop(View view){
        HashMap<String, Object> options = new HashMap<>();
        options.put("limit", 20);
        options.put("time_range", "long_term");
        spotify.getTopTracks(options, new Callback<Pager<Track>>() {
            @Override
            public void success(Pager<Track> trackPager, Response response) {
                // Get tracks from pager
                List<Track> trackList = trackPager.items;

                String[] trackSeedGeneration = translateAndShuffle(trackList);

                goToRecGeneration(trackSeedGeneration);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("Me failure", error.toString());
            }
        });
    }

    protected void mediumTermTop(View view){
        HashMap<String, Object> options = new HashMap<>();
        options.put("limit", 20);
        options.put("time_range", "medium_term");
        spotify.getTopTracks(options, new Callback<Pager<Track>>() {
            @Override
            public void success(Pager<Track> trackPager, Response response) {
                // Get tracks from pager
                List<Track> trackList = trackPager.items;

                String[] trackSeedGeneration = translateAndShuffle(trackList);

                goToRecGeneration(trackSeedGeneration);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("Me failure", error.toString());
            }
        });
    }

    protected void shortTermTop(View view){
        HashMap<String, Object> options = new HashMap<>();
        options.put("limit", 20);
        options.put("time_range", "short_term");
        spotify.getTopTracks(options, new Callback<Pager<Track>>() {
            @Override
            public void success(Pager<Track> trackPager, Response response) {
                // Get tracks from pager
                List<Track> trackList = trackPager.items;

                String[] trackSeedGeneration = translateAndShuffle(trackList);

                goToRecGeneration(trackSeedGeneration);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("Me failure", error.toString());
            }
        });
    }

    public String[] translateAndShuffle(List<Track> trackList){
        // Translate to array of IDs
        ArrayList<String> trackIDs = new ArrayList<>();
        for (int i = 0; i < trackList.size(); i++) {
            Track t = trackList.get(i);
            trackIDs.add(t.id);
        }

        // Shuffle and select 5 random ones
        String[] trackSeedGeneration = new String[5];
        Collections.shuffle(trackIDs);
        for (int i=0; i < 5; i++){
            trackSeedGeneration[i] = trackIDs.get(i);
        }

        return trackSeedGeneration;
    }

    public void goToRecGeneration(String[] seedGeneration){
        seed = seedGeneration;
        Intent intent = new Intent(this, RecommendationsGeneration.class);
        startActivity(intent);
    }


}
