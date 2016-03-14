package com.magnet.magnetchat.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import com.magnet.magnetchat.callbacks.BaseActivityCallback;

public abstract class BaseFragment extends Fragment implements View.OnClickListener {

    protected View containerView;
    private BaseActivityCallback baseActivityCallback;

    public BaseFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        containerView = inflater.inflate(getLayoutId(), container, false);
        onCreateFragment(containerView);
        return containerView;
    }

    /**
     * Method which provide the getting of the current layout ID
     *
     * @return current layout ID
     */
    protected abstract int getLayoutId();

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
            if(null != view) {
                view.setOnClickListener(this);
            } else {
                Log.e("BaseFragment", "setOnClickListeners : View is null", new Exception());
            }
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
     * @param activtyClass activity which should be starting
     */
    protected void startActivity(Class activtyClass) {
        getActivity().startActivity(new Intent(getActivity(), activtyClass));
    }

    /**
     * Method which provide the keyboard hiding
     */
    public void hideKeyboard() {
        InputMethodManager inputMethod = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethod.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getRootView().getWindowToken(), 0);
    }

    public void setBaseActivityCallback(BaseActivityCallback baseActivityCallback) {
        this.baseActivityCallback = baseActivityCallback;
    }

    @Override public void onAttach(Context context) {
        super.onAttach(context);

        Log.d("BaseFragment", "\n--------------------------------\nonAttach\n--------------------------------\n");
    }

    @Override public void onDetach() {
        super.onDetach();

        Log.d("BaseFragment", "\n--------------------------------\nonDetach\n--------------------------------\n");
    }
}
