package me.dylanhobbs.tempospotter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.spotify.sdk.android.player.Spotify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.AudioFeaturesTrack;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.Recommendations;
import kaaes.spotify.webapi.android.models.Track;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by dylanhobbs on 15/10/2017.
 */

public class RecommendationsGeneration extends AppCompatActivity {
    final int TEMPO_VARIATION = 5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recommendation_generation);

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
                max_tempo[0] = tempo + TEMPO_VARIATION;
                min_tempo[0] = tempo - TEMPO_VARIATION;

                // Generate reccomendations
                String formattedSeed = createCommaSeperated(seed);

                HashMap<String, Object> options = new HashMap<>();
//                options.put("max_tempo", max_tempo[0]);
//                options.put("min_tempo", min_tempo[0]);
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
                        RecommendationAdapter recommendationAdapter = new RecommendationAdapter(
                                current,
                                android.R.layout.simple_list_item_1,
                                useableTrackList);

                        // Set adapter
                        listView.setAdapter(recommendationAdapter);
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
        // Get optional name

        // Create playlist
    }

    public String createCommaSeperated(String[] seeds){
        String toReturn = "";
        for (int i = 0; i < seeds.length; i++) {
            toReturn = toReturn + seeds[i] + ",";
        }
        return toReturn;
    }
}
