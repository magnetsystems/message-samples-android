package com.magnet.chatsdkcover.core.factories;

import android.content.Context;
import android.view.LayoutInflater;

import com.magnet.chatsdkcover.R;
import com.magnet.magnetchat.model.MMXMessageWrapper;
import com.magnet.magnetchat.model.MMXUserWrapper;
import com.magnet.magnetchat.ui.factories.DefaultMMXListItemFactory;
import com.magnet.magnetchat.ui.views.abs.BaseMMXTypedView;


/**
 * Created by aorehov on 20.05.16.
 */
public class MyMMXListItemFactory extends DefaultMMXListItemFactory {
    @Override
    protected BaseMMXTypedView createMyCustomView(Context context, int type) {
        switch (type) {
//            case MMXMessageWrapper.TYPE_TEXT_ANOTHER:
//                return (BaseMMXTypedView) LayoutInflater.from(context).inflate(R.layout.test_view_cutom_msg_another, null, false);
//            case MMXMessageWrapper.TYPE_TEXT_MY:
//                return (BaseMMXTypedView) LayoutInflater.from(context).inflate(R.layout.test_view_cutom_msg_my, null, false);
//            case MMXMessageWrapper.TYPE_PHOTO_MY:
//            case MMXMessageWrapper.TYPE_PHOTO_ANOTHER:
//                return (BaseMMXTypedView) LayoutInflater.from(context).inflate(R.layout.test_view_custom_msg_pic_another, null, false);
//            case MMXMessageWrapper.TYPE_POLL_MY:
//                return (BaseMMXTypedView) LayoutInflater.from(context).inflate(R.layout.test_view_custom_msg_poll_my, null, false);
//            case MMXMessageWrapper.TYPE_VOTE_ANSWER:
//                return (BaseMMXTypedView) LayoutInflater.from(context).inflate(R.layout.test_view_custom_answers, null, false);
            case MMXUserWrapper.TYPE_USER:
                return (BaseMMXTypedView) LayoutInflater.from(context).inflate(R.layout.test_view_user_item, null, false);
        }
        return super.createMyCustomView(context, type);
    }
}
