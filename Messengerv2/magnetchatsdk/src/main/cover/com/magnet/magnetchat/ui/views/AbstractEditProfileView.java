package com.magnet.magnetchat.ui.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

import com.magnet.magnetchat.ChatSDK;
import com.magnet.magnetchat.R;
import com.magnet.magnetchat.ui.views.abs.BasePresenterView;
import com.magnet.magnetchat.presenters.EditProfileContract;
import com.magnet.magnetchat.presenters.core.MMXPresenterFactory;

/**
 * Created by dlernatovich on 3/16/16.
 */
public abstract class AbstractEditProfileView extends BasePresenterView<EditProfileContract.Presenter> implements EditProfileContract.View {

    public AbstractEditProfileView(Context context) {
        super(context);
    }

    public AbstractEditProfileView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AbstractEditProfileView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @NonNull
    @Override
    public EditProfileContract.Presenter getPresenter() {
        return presenter;
    }

    @Override
    protected void onCreateView() {
        presenter = createPresenterByName(getPresenterName());
        if (presenter == null)
            presenter = ChatSDK.getPresenterFactory().createEditProfilePresenter(this);
    }

    private EditProfileContract.Presenter createPresenterByName(String presenterName) {
        MMXPresenterFactory factory = ChatSDK.getMMXFactotyByName(presenterName);
        return factory != null ? factory.createEditProfilePresenter(this) : null;
    }

    protected abstract String getPresenterName();

    /**
     * Method which provide to getting of the context inside the View/Activity/Fragment
     *
     * @return current view
     */
    @NonNull
    @Override
    public Context getCurrentContext() {
        return getContext();
    }

    /**
     * Method which provide to getting of the layout ID
     *
     * @return layout ID
     */
    @Override
    protected int getLayoutId() {
        return R.layout.view_edit_profile_cover;
    }

}
