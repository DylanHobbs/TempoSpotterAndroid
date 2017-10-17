package me.dylanhobbs.tempospotter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by dylanhobbs on 16/10/2017.
 */

public class CreatePlaylistDialogFragment extends DialogFragment {
    boolean goAhead = false;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.playlist_create_information)
                .setPositiveButton(R.string.create_playlist_confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        goAhead = true;
                    }
                })
                .setNegativeButton(R.string.cancel_playlist_create, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Do nothing
                        goAhead = true;
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public boolean getGoAhead(){
        return this.goAhead;
    }
}
