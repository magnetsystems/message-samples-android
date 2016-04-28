package com.magnet.magnetchat.ui.factories;

import android.content.Context;

import com.magnet.magnetchat.ui.views.abs.BaseMMXTypedView;

/**
 * Created by aorehov on 28.04.16.
 */
public class DefaultMMXListItemFactory implements MMXListItemFactory {

    private MMXListItemFactory factory;

    public void setFactory(MMXListItemFactory factory) {
        this.factory = factory;
    }

    @Override
    final public BaseMMXTypedView createView(Context context, int type) {
        BaseMMXTypedView view = factory == null ? null : factory.createView(context, type);
        if (view == null)
            switch (type) {
            }
        return view;
    }
}
