package com.magnet.imessage.ui;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    private AlertDialog infoDialog;

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

    protected String getFieldText(int fieldId) {
        EditText editText = (EditText) findViewById(fieldId);
        if (editText != null) {
            return editText.getText().toString().trim();
        }
        return null;
    }

    protected void clearFieldText(int fieldId) {
        EditText editText = (EditText) findViewById(fieldId);
        if (editText != null) {
            editText.setText("");
        }
    }

    protected boolean getCheckBoxStatus(int checkBoxId) {
        CheckBox checkBox = (CheckBox) findViewById(checkBoxId);
        if (checkBox != null) {
            return checkBox.isChecked();
        }
        return false;
    }

    public void hideKeyboard() {
        InputMethodManager inputMethod = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethod.hideSoftInputFromWindow(getWindow().getDecorView().getRootView().getWindowToken(), 0);
    }

    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public void showMessage(int stringRes) {
        showMessage(getString(stringRes));
    }

    public void setText(int textViewId, String text) {
        TextView textView = (TextView) findViewById(textViewId);
        if (textView != null) {
            textView.setText(text);
            textView.setVisibility(View.VISIBLE);
        }
    }

    public void setText(int textViewId, int stringRes) {
        setText(textViewId, getString(stringRes));
    }

    public void setTextTemporary(final int textViewId, String text, final boolean visible) {
        setText(textViewId, text);
        MAIN_THREAD_HANDLER.postDelayed(new Runnable() {
            @Override
            public void run() {
                setText(textViewId, "");
                if (!visible) {
                    findViewById(textViewId).setVisibility(View.GONE);
                }
            }
        }, 10 * 1000);
    }

    @Override
    protected void onPause() {
        if (infoDialog != null && infoDialog.isShowing()) {
            infoDialog.dismiss();
        }
        super.onPause();
    }

    protected boolean checkString(String str) {
        return str != null && !str.isEmpty();
    }

    protected boolean checkStrings(String... str) {
        for (String string : str) {
            if (!checkString(string)) {
                return false;
            }
        }
        return true;
    }

    protected static final int ON_BASE_ACTIVITY_RESULTS = 0x1;
    protected static final String ON_RESULT_EXTRA_KEY = "ON_RESULT_EXTRA_KEY";

    /**
     * Interface which provide the doing some action inside the Handler thread
     */
    protected interface OnActionPerformer {
        void onActionPerform();

    }

    protected static final int NONE_MENU = Integer.MIN_VALUE;
    protected final Handler MAIN_THREAD_HANDLER = new Handler();


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
        startActivity(new Intent(this, activtyClass));
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
}
