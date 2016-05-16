package com.magnet.magnetchat.ui.views.users;


import android.content.Context;
import android.util.AttributeSet;

import com.magnet.magnetchat.model.MMXUserWrapper;
import com.magnet.magnetchat.ui.views.abs.BaseMMXTypedView;
import com.magnet.magnetchat.ui.views.abs.ViewProperty;

/**
 * Created by aorehov on 12.05.16.
 */
public abstract class MMXUserItemView<T extends ViewProperty> extends BaseMMXTypedView<MMXUserWrapper, T> {
    public MMXUserItemView(Context context) {
        super(context);
    }

    public MMXUserItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MMXUserItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setObject(MMXUserWrapper object) {
        super.setObject(object);
        updateUI();
    }

    private void updateUI() {
        if (getObject() != null) {
            updateUI(getObject());
        }
    }

    private void updateUI(MMXUserWrapper object) {

        onSelected(object.isSelected());

        String displayName = object.getName();
        onDisplayName(displayName);

        String url = object.getPicUrl();
        onUserPic(url, displayName);

        boolean isShowLetter = object.isShowLetter();
        String firstLetter = object.getFirstLetter();
        onShowLetter(isShowLetter, firstLetter);
    }

    protected abstract void onSelected(boolean selected);

    protected abstract void onShowLetter(boolean selected, String firstLetter);

    protected abstract void onUserPic(String url, String displayName);

    protected abstract void onDisplayName(String displayName);
}
