package com.magnet.magnetchat.ui.views.abs;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import butterknife.ButterKnife;

/**
 * Created by dlernatovich on 2/12/16.
 */
public abstract class BaseView extends FrameLayout implements View.OnClickListener {
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

    private void onInitializeView(Context context) {

        if (this.isInEditMode()) {
            return;
        }

        inflateView(context, getLayoutId());
        if (baseView != null) {
            ButterKnife.inject(this, baseView);
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

    protected abstract int getLayoutId();

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

    @Override
    public void onClick(View v) {

    }
}
