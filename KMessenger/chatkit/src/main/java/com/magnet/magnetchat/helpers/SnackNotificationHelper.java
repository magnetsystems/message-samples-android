package com.magnet.magnetchat.helpers;

import android.support.design.widget.Snackbar;
import android.view.View;
import com.magnet.magnetchat.R;

/**
 * Created by dlernatovich on 2/10/16.
 */
public class SnackNotificationHelper {
    /**
     * Method which provide to show the SnackBar
     *
     * @param currentView base view
     * @param message     snack bar message
     */
    public static void show(View currentView, String message) {
        final Snackbar snackbar = Snackbar.make(currentView, message, Snackbar.LENGTH_LONG)
                .setAction("Close", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                })
                .setActionTextColor(currentView.getResources().getColor(R.color.accent));
        View view = snackbar.getView();
        //TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        //if (tv != null) {
        //    tv.setTextColor(Color.WHITE);
        //    tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, currentView.getContext().getResources().getDimension(R.dimen.text_14));
        //}
        snackbar.show();
    }
}
