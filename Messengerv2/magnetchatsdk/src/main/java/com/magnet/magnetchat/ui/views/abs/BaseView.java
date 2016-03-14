package com.magnet.magnetchat.ui.views.abs;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import com.magnet.max.android.util.StringUtil;

/**
 * Created by dlernatovich on 2/12/16.
 */
public abstract class BaseView<T extends ViewProperty> extends FrameLayout implements View.OnClickListener {

    protected View baseView;

    public BaseView(Context context) {
        super(context);
        onInitializeView(context);
    }

    public BaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        onInitializeView(context);
    }

    public BaseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        onInitializeView(context);
    }

    /**
     * Method which provide the the action when view created
     *
     * @param context current context
     */
    private void onInitializeView(Context context) {

        if (isInEditMode()) {
            return;
        }

        inflateView(context, getLayoutId());
        if (baseView != null) {
            onLinkingViews(baseView);
        }
        onCreateView();
    }


    /**
     * Method which provide the inflating of the view
     *
     * @param context  current context
     * @param layoutID layout id
     */
    private void inflateView(Context context, int layoutID) {
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
     * Method which provide the view linking to inner objects (use as baseView.findViewByID(...))
     *
     * @param baseView created view
     */
    protected abstract void onLinkingViews(View baseView);

    /**
     * Method which provide the action when view was created (same as onCreate in activity)
     */
    protected abstract void onCreateView();

    /**
     * Method which provide starting the Activity
     *
     * @param activtyClass activity which should be starting
     */
    protected void startActivity(Class activtyClass) {
        getContext().startActivity(new Intent(getContext(), activtyClass));
    }

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
     * Method which provide the onClick functional
     *
     * @param v current view
     */
    @Override
    public void onClick(View v) {

    }

    /**
     * Method which provide to checking if current ID from the property is not default
     *
     * @param ID required ID
     * @return
     */
    protected boolean isNotDefaultID(int ID) {
        if (ID == ViewProperty.K_DEFAULT_ID_VALUE) {
            return false;
        }
        return true;
    }

    /**
     * Method which provide the keyboard hiding
     */
    public void hideKeyboard() {
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
    private Activity getActivity() {
        Context context = getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    /**
     * Method which provide the checking of strings
     *
     * @param str current strings
     * @return checking value
     */
    protected boolean checkStrings(String... str) {
        for (String string : str) {
            if (StringUtil.isEmpty(string)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Method which provide the view customization
     *
     * @param property current property
     */
    public abstract void setProperties(T property);

    /**
     * Method which do some action when activity/fragment resume their functional
     * (example: use it in activity onResume)
     */
    public void onResume() {

    }

    /**
     * Method which do some action when activity/fragment pause their functional
     * (example: use it in activity onPause)
     */
    public void onPause() {

    }

}
