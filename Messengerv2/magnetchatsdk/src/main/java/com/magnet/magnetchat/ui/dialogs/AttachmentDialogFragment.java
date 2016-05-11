package com.magnet.magnetchat.ui.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import java.util.List;

/**
 * Created by aorehov on 11.05.16.
 */
public abstract class AttachmentDialogFragment extends DialogFragment {

    private List<String> attachment;
    private ChooserDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return onDoCreateDialog(savedInstanceState, attachment);
    }

    public void setAttachment(List<String> attachment) {
        this.attachment = attachment;
    }

    public void setListener(ChooserDialogListener listener) {
        this.listener = listener;
    }

    public List<String> getAttachment() {
        return attachment;
    }

    @NonNull
    protected abstract Dialog onDoCreateDialog(Bundle savedInstanceState, List<String> attachment);

    protected void chooserAttachemnt(String attachment) {
        if (attachment != null && listener != null) listener.onChooseAttachment(attachment);
        dismiss();
    }

    public interface ChooserDialogListener {
        void onChooseAttachment(@NonNull String attachment);
    }

}
