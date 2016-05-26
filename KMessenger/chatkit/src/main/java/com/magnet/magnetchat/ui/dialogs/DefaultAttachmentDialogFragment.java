package com.magnet.magnetchat.ui.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;

import com.magnet.magnetchat.R;

import java.util.List;

/**
 * Created by aorehov on 11.05.16.
 */
public class DefaultAttachmentDialogFragment extends AttachmentDialogFragment {

    @NonNull
    @Override
    protected Dialog onDoCreateDialog(Bundle savedInstanceState, List<String> attachment) {

        CharSequence[] sequences = new CharSequence[attachment.size()];
        sequences = attachment.toArray(sequences);


        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.mmx_attachments))
                .setItems(sequences, listener);

        AlertDialog dialog = alertDialog.create();

        return dialog;
    }

    private final DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            String attachment = getAttachment().get(which);
            chooserAttachemnt(attachment);
        }
    };
}
