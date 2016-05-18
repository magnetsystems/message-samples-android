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
import android.widget.Toast;

import com.magnet.magnetchat.callbacks.BaseActivityCallback;

public abstract class MMXBaseFragment extends Fragment implements View.OnClickListener {

    protected View containerView;
    private BaseActivityCallback baseActivityCallback;

    public MMXBaseFragment() {
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
            if (null != view) {
                view.setOnClickListener(this);
            } else {
                Log.e("ABaseFragment", "setOnClickListeners : View is null", new Exception());
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
        startActivity(activtyClass, null);
    }

    protected void startActivity(Class activtyClass, Bundle bundle) {
        Intent intent = new Intent(getActivity(), activtyClass);
        if (bundle != null) intent.putExtras(bundle);
        getActivity().startActivity(intent);

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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Log.d("ABaseFragment", "\n--------------------------------\nonAttach\n--------------------------------\n");
    }

    @Override
    public void onDetach() {
        super.onDetach();

        Log.d("ABaseFragment", "\n--------------------------------\nonDetach\n--------------------------------\n");
    }

    protected <T extends View> T findView(View uiBaseView, int resId) {
        return (T) uiBaseView.findViewById(resId);
    }

    /**
     * the methods are wrappers for toast notification
     *
     * @see Toast
     */
    public void toast(CharSequence message, boolean isLongShow) {
        Toast.makeText(getContext(), message, isLongShow ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
    }

    public void toast(CharSequence message) {
        toast(message, false);
    }

    public void toast(int resId, boolean isLongShow) {
        Toast.makeText(getContext(), resId, isLongShow ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
    }

    public void toast(int resId) {
        toast(resId, false);
    }
}
