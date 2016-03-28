package com.magnet.chatsdkcover.ui.views.abs;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

/**
 * Created by dlernatovich on 3/11/16.
 */
public abstract class BaseView extends FrameLayout implements View.OnClickListener {

    /**
     * Interface which provide the doing some action inside the Handler thread
     */
    protected interface OnActionPerformer {
        void onActionPerform();
    }

    protected static int K_DEFAULT_ID = Integer.MIN_VALUE;

    private final Handler MAIN_THREAD_HANDLER = new Handler();

    protected View baseView;

    public BaseView(Context context) {
        super(context);
        onInitializeView(context, null);
    }

    public BaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        onInitializeView(context, attrs);
    }

    public BaseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        onInitializeView(context, attrs);
    }

    /**
     * Method which provide the attribute initialize
     *
     * @param context current context
     * @param attrs   current attribute
     */
    private void onInitializeView(@NonNull Context context, @Nullable AttributeSet attrs) {

        if (isInEditMode() == true) {
            return;
        }

        inflateView(context, getLayoutId());
        if (baseView != null) {
            onLinkInterface();
            if (attrs != null) {
                onAttributeInitialize(attrs);
            }
        }
        onCreateView();
    }

    /**
     * Method which provide the attribute initializing
     *
     * @param attrs attributes
     */
    protected void onAttributeInitialize(@NonNull AttributeSet attrs) {

    }

    /**
     * Method which provide the UI customizing with accordance to the custom attributes
     */
    protected void onApplyAttributes() {
    }

    /**
     * Method which provide the inflating of the view
     *
     * @param context  current context
     * @param layoutID layout id
     */
    private void inflateView(@NonNull Context context, int layoutID) {
        LayoutInflater inflater = LayoutInflater.from(context);
        baseView = inflater.inflate(layoutID, this);
    }

    /**
     * Method which provide to getting of the layout ID
     *
     * @return layout ID
     */
    protected abstract int getLayoutId();

    /**
     * Method which provide the interface linking
     */
    protected abstract void onLinkInterface();

    /**
     * Method which provide the action when view will create
     */
    protected abstract void onCreateView();

    /**
     * Method which provide starting the Activity
     *
     * @param activtyClass activity which should be starting
     */
    public void startActivity(@NonNull Class activtyClass) {
        getContext().startActivity(new Intent(getContext(), activtyClass));
    }

    /**
     * Method which provide the start activity with top clearing
     *
     * @param activtyClass activity class
     */
    protected void startActivityWithClearTop(Class activtyClass) {
        Intent intent = new Intent(getContext(), activtyClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        getContext().startActivity(intent);
    }

    /**
     * Method which provide starting the Activity for results
     *
     * @param activtyClass activity which should be starting
     * @param resultCode   result code
     */
    protected void startActivityForResults(@NonNull Class activtyClass, final int resultCode) {
        if (getActivity() != null) {
            getActivity().startActivityForResult(new Intent(getContext(), activtyClass), resultCode);
        }
    }

    /**
     * Method which provide starting the Activity for results
     *
     * @param activtyClass activity which should be starting
     * @param resultCode   result code
     */
    protected void startActivityForResults(@NonNull Intent activtyClass, final int resultCode) {
        if (getActivity() != null) {
            getActivity().startActivityForResult(activtyClass, resultCode);
        }
    }

    /**
     * Method which provide the setting of the OnClickListener
     *
     * @param views current list of Views
     */
    protected void setOnClickListeners(@NonNull View... views) {
        for (View view : views) {
            view.setOnClickListener(this);
        }
    }

    /**
     * Method which provide the action for onClickListener
     *
     * @param v
     */
    @Override
    public void onClick(View v) {

    }

    /**
     * Method which provide the message showing in the snack bar
     *
     * @param message current message value
     */
    protected void showMessage(@NonNull String message) {
        Snackbar snackbar = Snackbar.make(this, message, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    /**
     * Method which provide the doing action on UI thread after the delaying time
     *
     * @param delay     delaying time (in seconds)
     * @param performer current action
     */
    protected void runOnMainThread(int delay, final OnActionPerformer performer) {
        MAIN_THREAD_HANDLER.postDelayed(new Runnable() {
            @Override
            public void run() {
                performer.onActionPerform();
            }
        }, delay);
    }

    /**
     * Method which provide the running action on the background thread
     *
     * @param onActionPerformer action performer
     */
    protected void runOnBackground(final OnActionPerformer onActionPerformer) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                onActionPerformer.onActionPerform();
            }
        };
        new Thread(runnable).start();
    }

    /**
     * Method which provide the keyboard hiding
     */
    protected void hideKeyboard() {
        Activity activity = getActivity();
        if (activity != null) {
            InputMethodManager inputMethod = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethod.hideSoftInputFromWindow(activity.getWindow().getDecorView().getRootView().getWindowToken(), 0);
        }
    }

    /**
     * Method which provide the getting of the current activity from view
     *
     * @return getting activity
     */
    protected Activity getActivity() {
        Context context = getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }
}
