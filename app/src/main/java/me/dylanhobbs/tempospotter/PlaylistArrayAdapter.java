package me.dylanhobbs.tempospotter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.PlaylistSimple;


class PlaylistArrayAdapter extends ArrayAdapter<PlaylistSimple> {
    private Context mContext;
    private List<PlaylistSimple> playListList = new ArrayList<>();

    PlaylistArrayAdapter(Context context, int textViewResourceId, ArrayList<PlaylistSimple> list) {
        super(context, textViewResourceId, list);
        mContext = context;
        playListList = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.playlist_list_item, parent, false);

        PlaylistSimple currentPlaylist = playListList.get(position);

        // Playlist Art
        if(currentPlaylist.images.size() > 0){
            new DownloadImageTask((ImageView) listItem.findViewById(R.id.imageView_playlist_art))
                    .execute(currentPlaylist.images.get(0).url);
        }

        // PlayList name
        TextView name = (TextView) listItem.findViewById(R.id.playlist_View_name);
        name.setText(currentPlaylist.name);

        // Owner name
        TextView release = (TextView) listItem.findViewById(R.id.owner_View_name);
        release.setText(currentPlaylist.owner.display_name);

        return listItem;
    }
}

