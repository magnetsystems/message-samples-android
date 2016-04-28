package com.magnet.magnetchat.ui.factories;

import android.content.Context;
import android.view.ViewGroup;

import com.magnet.magnetchat.ui.views.poll.AbstractEditPollView;

/**
 * Created by aorehov on 27.04.16.
 */
public interface MMXViewFactory {
    AbstractEditPollView createPolView(Context context, ViewGroup parent);

    AbstractEditPollView createPolView(Context context);
}
