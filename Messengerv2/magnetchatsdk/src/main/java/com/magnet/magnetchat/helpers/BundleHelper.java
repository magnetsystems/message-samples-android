package com.magnet.magnetchat.helpers;

import android.os.Bundle;

import com.magnet.magnetchat.Constants;
import com.magnet.max.android.User;
import com.magnet.mmx.client.api.MMXChannel;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by aorehov on 04.05.16.
 */
public class BundleHelper {

    public static void packChannel(Bundle bundle, MMXChannel mmxChannel) {
        bundle.putParcelable(Constants.TAG_CHANNEL, mmxChannel);
    }

    public static Bundle packChannel(MMXChannel mmxChannel) {
        Bundle bundle = new Bundle();
        packChannel(bundle, mmxChannel);
        return bundle;
    }

    public static MMXChannel readMMXChannelFromBundle(Bundle bundle) {
        if (bundle == null || !bundle.containsKey(Constants.TAG_CHANNEL)) {
            return null;
        }

        return bundle.getParcelable(Constants.TAG_CHANNEL);
    }

    public static void packRecipients(Bundle bundle, Collection<User> recipients) {
        if (recipients == null) {
            return;
        }

        ArrayList<User> users;
        if (recipients instanceof ArrayList) {
            users = (ArrayList<User>) recipients;
        } else {
            users = new ArrayList<>(recipients);
        }

        bundle.putParcelableArrayList(Constants.TAG_CREATE_WITH_RECIPIENTS, users);
    }

    public static Bundle packRecipients(Collection<User> recipients) {
        if (recipients == null || recipients.isEmpty()) return null;
        Bundle bundle = new Bundle();
        packRecipients(bundle, recipients);
        return bundle;
    }

    public static ArrayList<User> readRecipients(Bundle bundle) {
        if (bundle == null || !bundle.containsKey(Constants.TAG_CREATE_WITH_RECIPIENTS))
            return null;

        return bundle.getParcelableArrayList(Constants.TAG_CREATE_WITH_RECIPIENTS);
    }
}
