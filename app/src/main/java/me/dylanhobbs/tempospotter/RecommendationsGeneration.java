package me.dylanhobbs.tempospotter;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.spotify.sdk.android.player.Spotify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.AudioFeaturesTrack;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.Playlist;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.Recommendations;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.UserPrivate;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by dylanhobbs on 15/10/2017.
 */

public class RecommendationsGeneration extends AppCompatActivity {
    final RecommendationAdapter[] recommendationAdapter = new RecommendationAdapter[1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recommendation_generation);
        Button goButton = (Button) findViewById(R.id.create_spotify_playlist_button);
        goButton.setEnabled(true);

        // List of tracks in list
        ArrayList<Track> trackList;

        // Set playlist created boolean of this instance to false
        //this.created = false;

        final SpotifyService spotify;
        final String[] seed;
        String selectedTrack;

        final Float[] max_tempo = new Float[1];
        final Float[] min_tempo = new Float[1];

        final Context current = this;
        spotify = MainActivity.spotify;
        seed = SeedGeneration.seed;
        selectedTrack = SeedGeneration.selectedTrack;

        // Get Tempo of Selected Song
        spotify.getTrackAudioFeatures(selectedTrack, new Callback<AudioFeaturesTrack>() {
            @Override
            public void success(AudioFeaturesTrack audioFeaturesTrack, Response response) {
                float tempo = audioFeaturesTrack.tempo;

                // Generate reccomendations
                String formattedSeed = createCommaSeperated(seed);

                HashMap<String, Object> options = new HashMap<>();
                options.put("target_tempo", tempo);
                options.put("seed_tracks", formattedSeed);

                spotify.getRecommendations(options, new Callback<Recommendations>() {
                    @Override
                    public void success(Recommendations recommendations, Response response) {
                        List<Track> trackList = recommendations.tracks;
                        ArrayList<Track> useableTrackList = (ArrayList) trackList;

                        // Get the list
                        ListView listView = (ListView) findViewById(R.id.recommendation_list);

                        // Add to adapter
                        recommendationAdapter[0] = new RecommendationAdapter(
                                current,
                                android.R.layout.simple_list_item_1,
                                useableTrackList);

                        // Set adapter
                        listView.setAdapter(recommendationAdapter[0]);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.d("Me failure", error.toString());
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("Me failure", error.toString());
            }
        });
    }

    public void generatePlaylist(View view){
        // Flash Confirm Dialog
//        CreatePlaylistDialogFragment newFragment = new CreatePlaylistDialogFragment();
//        newFragment.show(getFragmentManager(), "playlist_confirm");

        // Get confirm
        //if(newFragment.getGoAhead()){

        // Check if playlist has been created for this instance
//        if(getCreatedStatus() == false){
//            setCreatedStatus(true);
        final Context current = this;

        // Get user
        final String[] me = new String[1];
        MainActivity.spotify.getMe(new Callback<UserPrivate>() {
            @Override
            public void success(UserPrivate userPrivate, Response response) {
                me[0] = userPrivate.id;

                // Get optional name
                EditText editText = (EditText) findViewById(R.id.optional_playlist_name_field);
                String playlistName = "";

                // TODO: Add tempo to playlist name
                if(editText.getText().toString().equals("")){
                    playlistName = "TempoSpotter Playlist ";
                } else {
                    playlistName = editText.getText().toString();
                }

                // Create playlist
                HashMap<String, Object> options = new HashMap<>();
                options.put("name", playlistName);
                options.put("description", "Auto generated playlist made by TempoSpotter");
                MainActivity.spotify.createPlaylist(me[0], options, new Callback<Playlist>() {
                    @Override
                    public void success(Playlist playlist, Response response) {
                        //TODO: Flash success
                                /* Add tracks */
                        // Get list of tracks
                        ListView listView = (ListView) findViewById(R.id.recommendation_list);
                        final RecommendationAdapter a = (RecommendationAdapter) listView.getAdapter();

                        ArrayList<Track> trackList = new ArrayList<Track>();
                        for (int i=0;i<a.getCount();i++){
                            trackList.add((Track) a.getItem(i));
                        }

                        // Add them to the playlist
                        HashMap<String, Object> options1 = new HashMap<>();
                        HashMap<String, Object> body = new HashMap<>();

                        String formattedTracks = createCommaSeperated(trackList);
                        options1.put("uris", formattedTracks);

                        final Playlist playl = playlist;
                        final String me1 = me[0];

                        MainActivity.spotify.addTracksToPlaylist(me1, playl.id, options1, body, new Callback<Pager<PlaylistTrack>>() {
                            @Override
                            public void success(Pager<PlaylistTrack> playlistTrackPager, Response response) {
                                // Disable the button
                                Button goButton = (Button) findViewById(R.id.create_spotify_playlist_button);
                                goButton.setEnabled(false);

                                //Show congratulations?
                                new SweetAlertDialog(current, SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText("Playlist Created!")
                                        .setContentText("Head on over to Spotify to check it out!")
                                        .show(); 
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                // TODO: Feedback to user
                            }
                        });
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        //TODO: Flash error
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
                //TODO: Log failure
            }
        });
    }
    //}

    public String createCommaSeperated(String[] seeds){
        String toReturn = "";
        for (int i = 0; i < seeds.length; i++) {
            toReturn = toReturn + seeds[i] + ",";
        }
        return toReturn;
    }

    public String createCommaSeperated(ArrayList<Track> seeds){
        String toReturn = "";
        for (int i = 0; i < seeds.size(); i++) {
            toReturn = toReturn + seeds.get(i).uri + ",";
        }
        return toReturn;
    }

//    public boolean getCreatedStatus(){
//        return this.created;
//    }

//    private void setCreatedStatus(boolean created) {
//        this.created = created;
//    }
}
