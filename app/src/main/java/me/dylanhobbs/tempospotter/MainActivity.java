package me.dylanhobbs.tempospotter;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.TracksPager;
import kaaes.spotify.webapi.android.models.UserPrivate;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity {

    public static final String TRACK_MESSAGE = "com.dylanhobbs.tempospotter.TRACK_MESSAGE";
    public static final String USER_ID_MESSAGE = "com.dylanhobbs.tempospotter.USER_ID_MESSAGE";

    private static final String CLIENT_ID = "f8e62d489d8d48d29ea438319de216d7";
    private static final String REDIRECT_URI = "tempo-spotter-android-login://callback";

    public static final int AUTH_TOKEN_REQUEST_CODE = 0x10;

    private String userID = "";

    public static SpotifyService spotify = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming", "user-read-email",
                "user-library-read", "user-top-read", "playlist-read-private",
                "playlist-modify-public", "playlist-modify-private", "playlist-read-collaborative"});
        AuthenticationRequest request = builder.build();
        AuthenticationClient.openLoginActivity(this, AUTH_TOKEN_REQUEST_CODE, request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        final AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
        if (AUTH_TOKEN_REQUEST_CODE == requestCode) {
            SpotifyApi api = new SpotifyApi();
            api.setAccessToken(response.getAccessToken());
            spotify = api.getService();
            onLoggedIn();
        }
    }

    // Search Spotify for given query
    public void searchForSong(View view){
        final Context current = this;
        // Get search query
        EditText editText = (EditText) findViewById(R.id.song_search_query);
        String query = editText.getText().toString();

        // Search with query
        spotify.searchTracks(query, new Callback<TracksPager>() {
            @Override
            public void success(TracksPager returnedPager, Response response) {
                // Transform pager to list of tracks
                Pager<Track> trackPager  = returnedPager.tracks;
                List<Track> trackList = trackPager.items;
                ArrayList<Track> useableTrackList = (ArrayList) trackList;

                // Get the list
                ListView listView = (ListView) findViewById(R.id.song_result_list);

                // Add to adapter
                RecommendationAdapter tracksAdapter = new RecommendationAdapter(
                        current,
                        android.R.layout.simple_list_item_1,
                        useableTrackList);

                // Set adapter
                listView.setAdapter(tracksAdapter);
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });

        final ListView listView = (ListView) findViewById(R.id.song_result_list);
        // Set Clickable event
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = listView.getItemAtPosition(position);
                Track track = (Track) o;
                goToSeedGenertion(track);
            }
        });
    }

    public void goToSeedGenertion(Track track){
        TextView textView = (TextView) findViewById(R.id.display_name_box);
        userID = (String) textView.getText();

        Intent intent = new Intent(this, SeedGeneration.class);
        intent.putExtra(TRACK_MESSAGE, track.id);
        intent.putExtra(USER_ID_MESSAGE, userID);
        startActivity(intent);
    }

    public void onLoggedIn() {
        final Context current = this;
        spotify.getMe(new Callback<UserPrivate>() {
            @Override
            public void success(UserPrivate user, Response response) {
                TextView textView = (TextView) findViewById(R.id.display_name_box);
                textView.setText(user.id);
            }

            @Override
            public void failure(RetrofitError error) {
                new SweetAlertDialog(current, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Derp")
                        .setContentText("Something went wrong while logging you in. Try again")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                restartActivity();
                            }
                        })
                        .show();
            }
        });
    }

    private void restartActivity(){
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }
}