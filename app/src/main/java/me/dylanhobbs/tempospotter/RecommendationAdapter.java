package me.dylanhobbs.tempospotter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.AudioFeaturesTrack;
import kaaes.spotify.webapi.android.models.AudioFeaturesTracks;
import kaaes.spotify.webapi.android.models.Track;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by dylanhobbs on 15/10/2017.
 */

public class RecommendationAdapter extends ArrayAdapter {
    private Context mContext;
    private List<Track> trackList = new ArrayList<>();

    RecommendationAdapter(Context context, int textViewResourceId, ArrayList<Track> list) {
        super(context, textViewResourceId, list);
        mContext = context;
        trackList = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.recommendation_list_item, parent, false);

        Track currentTrack = trackList.get(position);

        // Album Art
        new DownloadImageTask((ImageView) listItem.findViewById(R.id.imageView_album_art))
                .execute(currentTrack.album.images.get(0).url);

        // Track name
        TextView name = (TextView) listItem.findViewById(R.id.track_View_name);
        name.setText(currentTrack.name);

        // Artist name
        TextView artist = (TextView) listItem.findViewById(R.id.artist_View_name);
        artist.setText(currentTrack.artists.get(0).name);

        // Track Tempo
        SpotifyService spotify = MainActivity.spotify;
        String currentID = currentTrack.id;

        final View finalListItem = listItem;

        spotify.getTrackAudioFeatures(currentID, new Callback<AudioFeaturesTrack>() {
            @Override
            public void success(AudioFeaturesTrack audioFeaturesTrack, Response response) {
                TextView tempo = (TextView) finalListItem.findViewById(R.id.track_tempo);
                if(audioFeaturesTrack.tempo > 0){
                    tempo.setText("" + audioFeaturesTrack.tempo + " bpm");
                } else{
                    tempo.setText("?");
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("Me failure", error.toString());
            }
        });

        return listItem;
    }
}
