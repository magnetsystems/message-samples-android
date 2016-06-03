package com.magnet.magnetchat.beans;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.magnet.magnetchat.Constants;
import com.magnet.magnetchat.helpers.BundleHelper;
import com.magnet.magnetchat.ui.activities.MMXChatDetailsActivity;
import com.magnet.mmx.client.api.MMXChannel;

/**
 * Created by aorehov on 20.05.16.
 */
public class DefaultMMXBeanFactory implements MMXBeanFactory {
    @Override
    public String messageDateFormat() {
        return null;
    }

    @Override
    public Intent getMagnetChatIntent(Context context, MMXChannel mmxChannel) {
        Bundle bundle = BundleHelper.packChannel(mmxChannel);
        if (bundle == null) return null;
        Intent intent = new Intent();
        intent.setAction(Constants.ACTION_CHAT);
        intent.putExtras(bundle);
        return intent;
//        return MMXChatActivity.createIntent(context, mmxChannel);
    }

    @Nullable
    @Override
    public Intent createChatDetailsIntent(Context context, MMXChannel mmxChannel) {
        return MMXChatDetailsActivity.createIntent(context, mmxChannel);
    }
}
