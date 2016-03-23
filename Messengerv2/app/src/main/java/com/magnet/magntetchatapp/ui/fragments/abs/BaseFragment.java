package com.magnet.magntetchatapp.ui.fragments.abs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.magnet.magntetchatapp.callbacks.OnActivityEventCallback;
import com.magnet.magntetchatapp.ui.activities.abs.BaseActivity;

import butterknife.ButterKnife;

/**
 * Created by Artli_000 on 18.03.2016.
 */
public abstract class BaseFragment extends Fragment implements View.OnClickListener {

    protected static final int NONE_MENU = Integer.MIN_VALUE;

    protected View containerView;
    protected OnActivityEventCallback eventCallback;

    public BaseFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        containerView = inflater.inflate(getLayoutId(), container, false);
        if (getMenuId() != NONE_MENU) {
            setHasOptionsMenu(true);
        }
        ButterKnife.inject(this, containerView);
        onCreateFragment(containerView);
        return containerView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        int menuId = getMenuId();
        if (menuId == NONE_MENU) {
            return;
        }
        inflater.inflate(menuId, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * Method which provide the getting of the current layout ID
     *
     * @return current layout ID
     */
    protected abstract int getLayoutId();

    /**
     * Method which provide to get of the option menu ID
     *
     * @return menu ID
     */
    protected abstract int getMenuId();

    /**
     * Method which provide the action when fragment is created
     *
     * @param containerView current view
     */
    protected abstract void onCreateFragment(View containerView);


    /**
     * Method which provide the setting of the OnClickListener
     *
     * @param views current list of Views
     */
    protected void setOnClickListeners(View... views) {
        for (View view : views) {
            view.setOnClickListener(this);
        }
    }

    /**
     * Method which provide the action for the OnClickListener event
     *
     * @param v current view
     */
    @Override
    public void onClick(View v) {

    }

    /**
     * Method which provide starting the Activity
     *
     * @param activtyClass        activity class
     * @param isNeedFinishCurrent is need clear current
     */
    protected void startActivity(Class activtyClass, boolean isNeedFinishCurrent) {
        Activity baseActivity = getActivity();
        if (baseActivity.getClass().isInstance(BaseActivity.class)) {
            ((BaseActivity) baseActivity).startActivity(activtyClass, isNeedFinishCurrent);
        }
    }

    /**
     * Method which provide starting the Activity
     *
     * @param activtyClass        activity class
     * @param isNeedFinishCurrent is need clear current
     */
    protected void startActivity(Intent activtyClass, boolean isNeedFinishCurrent) {
        Activity baseActivity = getActivity();
        if (baseActivity.getClass().isInstance(BaseActivity.class)) {
            ((BaseActivity) baseActivity).startActivity(activtyClass, isNeedFinishCurrent);
        }
    }

    /**
     * Method which provide the start activity with top clearing
     *
     * @param activtyClass activity class
     */
    protected void startActivityWithClearTop(Class activtyClass) {
        Activity baseActivity = getActivity();
        if (baseActivity.getClass().isInstance(BaseActivity.class)) {
            ((BaseActivity) baseActivity).startActivityWithClearTop(activtyClass);
        }
    }

    /**
     * Method which provide to message showing as Snackbar
     *
     * @param message message to show
     */
    protected void showMessage(@NonNull String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
    }

    //SETTERS

    /**
     * Method which provide the setting of the event callback
     *
     * @param eventCallback event callback
     */
    public void setEventCallback(@NonNull OnActivityEventCallback eventCallback) {
        this.eventCallback = eventCallback;
    }
}
