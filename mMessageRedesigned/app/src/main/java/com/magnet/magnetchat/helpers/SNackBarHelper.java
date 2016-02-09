package com.magnet.magnetchat.helpers;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.magnet.magnetchat.R;

/**
 * Created by dlernatovich on 2/9/16.
 */
public class SnackBarHelper {
    public static void show(View currentView, String message) {
        final Snackbar snackbar = Snackbar.make(currentView, message, Snackbar.LENGTH_LONG)
                .setAction("Close", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                })
                .setActionTextColor(Color.YELLOW);
        View view = snackbar.getView();
        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        if (tv != null) {
            tv.setTextColor(Color.WHITE);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, currentView.getContext().getResources().getDimension(R.dimen.text_14));
        }
        snackbar.show();
    }

    public static void show(View currentView, String message, String actionButtonText) {
        Snackbar snackbar = Snackbar.make(currentView, message, Snackbar.LENGTH_LONG).setAction(actionButtonText, null);
        View view = snackbar.getView();
        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        if (tv != null) {
            tv.setTextColor(Color.WHITE);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, currentView.getContext().getResources().getDimension(R.dimen.text_14));
        }
        snackbar.show();
    }
}
