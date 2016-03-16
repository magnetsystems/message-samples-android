package com.magnet.magntetchatapp.ui.views.section.edit;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

import com.magnet.magntetchatapp.mvp.api.EditProfileContract;
import com.magnet.magntetchatapp.mvp.presenters.DefaultEditProfilePresenter;
import com.magnet.magntetchatapp.mvp.views.AbstractEditProfileView;

/**
 * Created by Artli_000 on 16.03.2016.
 */
public class DefaultEditProfileView extends AbstractEditProfileView {
    public DefaultEditProfileView(Context context) {
        super(context);
    }

    public DefaultEditProfileView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DefaultEditProfileView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Method which provide the getting of the current presenter
     *
     * @return current view presenter
     */
    @NonNull
    @Override
    public EditProfileContract.Presenter getPresenter() {
        return new DefaultEditProfilePresenter(this);
    }
}
