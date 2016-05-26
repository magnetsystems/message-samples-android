package com.magnet.magnetchat.ui.factories;

import android.content.Context;

import com.magnet.magnetchat.ui.views.abs.BaseMMXTypedView;

/**
 * Created by aorehov on 28.04.16.
 */
public interface MMXListItemFactory {

    BaseMMXTypedView createView(Context context, int type);

}
