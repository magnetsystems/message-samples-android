package com.magnet.magnetchat.mvp.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

import com.magnet.magnetchat.ChatSDK;
import com.magnet.magnetchat.mvp.abs.BasePresenterView;
import com.magnet.magnetchat.mvp.api.abs.RegisterContract;
import com.magnet.magnetchat.presenters.core.MMXPresenterFactory;


/**
 * Created by dlernatovich on 3/15/16.
 */
public abstract class AbstractRegisterView extends BasePresenterView<RegisterContract.Presenter> implements RegisterContract.View {

    public AbstractRegisterView(Context context) {
        super(context);
    }

    public AbstractRegisterView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AbstractRegisterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onCreateView() {
        presenter = createPresenterByName(getFactoryPresenterName());
        if (presenter == null)
            presenter = ChatSDK.getPresenterFactory().createRegisterPresenter(this);
    }

    private RegisterContract.Presenter createPresenterByName(String factoryName) {
        MMXPresenterFactory factory = ChatSDK.getMMXFactotyByName(factoryName);
        return factory != null ? factory.createRegisterPresenter(this) : null;
    }

    protected abstract String getFactoryPresenterName();

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

    public abstract void setRegisterActionCallback(RegisterContract.OnRegisterActionCallback registerActionCallback);
}
