package me.dylanhobbs.tempospotter;

import android.content.Context;
import android.graphics.Movie;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by dylanhobbs on 14/10/2017.
 */

public class TrackArrayAdapter extends ArrayAdapter<Track> {

    private Context mContext;
    private List<Track> trackList = new ArrayList<>();

    public TrackArrayAdapter(Context context, int textViewResourceId,  ArrayList<Track> list) {
        super(context, textViewResourceId, list);
        mContext = context;
        trackList = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.track_list_item, parent, false);

        Track currentTrack = trackList.get(position);

        // Album Art
        new DownloadImageTask((ImageView) listItem.findViewById(R.id.imageView_album_art))
                .execute(currentTrack.album.images.get(0).url);

//        ImageView image = (ImageView)listItem.findViewById(R.id.imageView_album_art);
//        image.setImageResource(currentTrack.album.images.get(0).url);

        // Track name
        TextView name = (TextView) listItem.findViewById(R.id.track_View_name);
        name.setText(currentTrack.name);

        // Artist name
        TextView release = (TextView) listItem.findViewById(R.id.artist_View_name);
        release.setText(currentTrack.artists.get(0).name);

        return listItem;
    }
}
