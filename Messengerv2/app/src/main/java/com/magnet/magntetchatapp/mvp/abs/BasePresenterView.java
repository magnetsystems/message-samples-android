package com.magnet.magntetchatapp.mvp.abs;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

import com.magnet.magntetchatapp.mvp.abs.BaseContract;
import com.magnet.magntetchatapp.ui.views.abs.BaseView;

/**
 * Created by dlernatovich on 3/11/16.
 */
public abstract class BasePresenterView<T extends BaseContract.BasePresenter> extends BaseView {

    protected final T presenter;

    public BasePresenterView(Context context) {
        super(context);
        presenter = getPresenter();
    }

    public BasePresenterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        presenter = getPresenter();
    }

    public BasePresenterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        presenter = getPresenter();
    }

    /**
     * Method which provide the action when Activity/fragment call onCreate method
     */
    public void onCreateActivity() {
    }

    /**
     * Method which provide the action when Activity/fragment call onResume method
     */
    public void onResumeActivity() {
    }

    /**
     * Method which provide the action when Activity/fragment call onPause method
     */
    public void onPauseActivity() {
    }

    /**
     * Method which provide the action when Activity/fragment call onDestroy method
     */
    public void onDestroyActivity() {
    }

    /**
     * Method which provide the getting of the current presenter
     *
     * @return current view presenter
     */
    @NonNull
    public abstract T getPresenter();
}
