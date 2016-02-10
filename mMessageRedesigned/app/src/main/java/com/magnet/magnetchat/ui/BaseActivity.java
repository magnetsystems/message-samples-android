package com.magnet.magnetchat.ui;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.magnet.magnetchat.helpers.SnackNotificationHelper;
import com.magnet.magnetchat.util.AppLogger;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    protected static final int ON_BASE_ACTIVITY_RESULTS = 0x1;
    protected static final String ON_RESULT_EXTRA_KEY = "ON_RESULT_EXTRA_KEY";

    protected static final int NONE_MENU = Integer.MIN_VALUE;
    protected final Handler MAIN_THREAD_HANDLER = new Handler();

    private AlertDialog infoDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());
        ButterKnife.inject(this);
    }

    /**
     * Method which provide to create the Dialog
     *
     * @param title   dialog title
     * @param message dialog message
     */
    protected void showInfoDialog(String title, String message) {
        if (infoDialog == null) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            dialogBuilder.setCancelable(false);
            dialogBuilder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    infoDialog.dismiss();
                }
            });
            infoDialog = dialogBuilder.create();
        }
        infoDialog.setTitle(title);
        infoDialog.setMessage(message);
        infoDialog.show();
    }

    /**
     * Method which provide the keyboard hiding
     */
    public void hideKeyboard() {
        InputMethodManager inputMethod = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethod.hideSoftInputFromWindow(getWindow().getDecorView().getRootView().getWindowToken(), 0);
    }

    /**
     * Method which provide the show message in the snack bar
     *
     * @param message curren message
     */
    public void showMessage(String message) {
        if (getBaseViewID() != -1) {
            SnackNotificationHelper.show(getBaseView(), message);
        }
    }

    private View getBaseView() {
        return (View) findViewById(getBaseViewID());
    }

    /**
     * Method which privde the getting of base view ID
     *
     * @return
     */
    protected int getBaseViewID() {
        return -1;
    }

    ;

    /**
     * Method which provide the show message in the snack bar
     *
     * @param message curren message
     */
    public void showMessage(String... message) {
        if (getBaseViewID() == -1) {
            return;
        }
        StringBuilder messages = new StringBuilder();
        for (String str : message) {
            messages.append(String.format("%s\n", str));
        }
        SnackNotificationHelper.show(getBaseView(), messages.toString().trim());
    }

    /**
     * Method whihc provide the show message form the resource
     *
     * @param stringRes current resource ID
     */
    public void showMessage(int stringRes) {
        showMessage(getString(stringRes));
    }

    @Override
    protected void onPause() {
        //Dismiss dialog on pause
        if (infoDialog != null && infoDialog.isShowing()) {
            infoDialog.dismiss();
        }
        super.onPause();
    }

    /**
     * Method which provide the string checking
     *
     * @param str current string value
     * @return checking value
     */
    protected boolean checkString(String str) {
        return str != null && !str.isEmpty();
    }

    /**
     * Method which provide the checking of strings
     *
     * @param str current strings
     * @return checking value
     */
    protected boolean checkStrings(String... str) {
        for (String string : str) {
            if (!checkString(string)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Interface which provide the doing some action inside the Handler thread
     */
    protected interface OnActionPerformer {
        void onActionPerform();

    }


    /**
     * Method which provide the replace of the fragment inside the activity
     *
     * @param fragment     current fragment
     * @param container_id current container id
     */
    public void replace(Fragment fragment, int container_id) {
        getFragmentTransaction().replace(container_id, fragment,
                getFragmentTag()).commit();
    }


    /**
     * Method which provide the getting of the FragmentTransaction
     *
     * @return current FragmentTransaction
     */
    private FragmentTransaction getFragmentTransaction() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        return ft;
    }

    /**
     * Method which provide the getting of the fragment tag
     *
     * @return current fragment Tag
     */
    private String getFragmentTag() {
        return getClass().getName();
    }

    /**
     * Method which provide starting the Activity
     *
     * @param activtyClass activity which should be starting
     */
    protected void startActivity(Class activtyClass) {
//        Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(new Intent(this, activtyClass));
    }

    /**
     * Method which provide starting the Activity
     *
     * @param activtyClass activity which should be starting
     */
    protected void startActivity(Class activtyClass, boolean isNeedClear) {
//        Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP
        Intent intent = new Intent(this, activtyClass);
        if (isNeedClear) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }
        startActivity(intent);
    }

    //====================ACTIVITY FOR RESULT METHODS====================

    /**
     * Method which provide starting the Activity for results
     *
     * @param activtyClass activity which should be starting
     */
    protected void startActivityForResults(Class activtyClass) {
        startActivityForResult(new Intent(this, activtyClass), ON_BASE_ACTIVITY_RESULTS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (resultCode == RESULT_OK) {
            // The user picked a contact.
            // The Intent's data Uri identifies which contact was selected.
            onActivityResult(requestCode, data);
        }

    }

    /**
     * Method which provide the action when activity return result
     *
     * @param data current intent
     */
    protected void onActivityResult(int requestCode, Intent data) {
        AppLogger.info(this, String.format("%d", requestCode), data.toString());
    }

    /**
     * Method which provide the sending of the Activity results
     *
     * @param extraValue current extra value
     */
    protected void sendActivityResult(String extraValue) {
        if (extraValue == null) {
            extraValue = "";
        }
        Intent intent = new Intent();
        intent.putExtra(ON_RESULT_EXTRA_KEY, extraValue);
        setResult(RESULT_OK, intent);
        onBackPressed();
    }

    /**
     * Method which provide starting the Service
     *
     * @param serviceClass service which should be starting
     */
    protected void startService(Class serviceClass) {
        if (!isMyServiceRunning(serviceClass)) {
            startService(new Intent(this, serviceClass));
        }
    }

    /**
     * Method which provide the service running checking
     *
     * @param serviceClass current service
     * @return checking results
     */
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Method which provide the setting of the OnClickListener
     *
     * @param views current list of views
     */
    protected void setOnClickListeners(View... views) {
        for (View view : views) {
            view.setOnClickListener(this);
        }
    }

    /**
     * Method which provide the setting of the OnClickListener
     *
     * @param viewsId current list of views id
     */
    protected void setOnClickListeners(int... viewsId) {
        for (int view : viewsId) {
            findViewById(view).setOnClickListener(this);
        }
    }

    /**
     * Method which provide the doing action on UI thread after the delaying time
     *
     * @param delayTime       delaying time (in seconds)
     * @param actionPerformer current action
     */
    protected void runOnMainThread(double delayTime, final OnActionPerformer actionPerformer) {
        MAIN_THREAD_HANDLER.postDelayed(new Runnable() {
            @Override
            public void run() {
                actionPerformer.onActionPerform();
            }
        }, (int) (delayTime * 1000));
    }

    /**
     * Method which provide the get layout resource
     *
     * @return current layout id
     */
    protected abstract int getLayoutResource();
}
