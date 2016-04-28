package com.magnet.magnetchat.ui.factories;

import android.content.Context;
import android.view.ViewGroup;

import com.magnet.magnetchat.ui.views.poll.AbstractEditPollView;
import com.magnet.magnetchat.ui.views.poll.DefaultEditPollView;

/**
 * Created by aorehov on 27.04.16.
 */
public class DefaultMMXViewFactory implements MMXViewFactory {


    @Override
    public AbstractEditPollView createPolView(Context context, ViewGroup parent) {
        return createPolView(context);
    }

    @Override
    public AbstractEditPollView createPolView(Context context) {
        return new DefaultEditPollView(context);
    }
}
