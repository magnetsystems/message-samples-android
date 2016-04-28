package com.magnet.magnetchat.helpers;

import com.magnet.magnetchat.model.MMXPollOptionWrapper;
import com.magnet.max.android.User;
import com.magnet.max.android.UserProfile;
import com.magnet.mmx.client.api.MMXMessage;
import com.magnet.mmx.client.ext.poll.MMXPollOption;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by aorehov on 27.04.16.
 */
public class MMXObjectsHelper {

    public static List<MMXPollOptionWrapper> convert(List<MMXPollOption> options, List<MMXPollOption> selectedByUser) {
        if (options == null) {
            return Collections.EMPTY_LIST;
        }

        List<MMXPollOptionWrapper> wrappers = new ArrayList<>(options.size());
        for (MMXPollOption opt : options) {
            wrappers.add(new MMXPollOptionWrapper(opt, isContain(selectedByUser, opt)));
        }
        return wrappers;
    }

    private static boolean isContain(List<MMXPollOption> list, MMXPollOption opt) {
        return list == null ? false : list.contains(opt);
    }

    public static List<String> convertToIdList(List<UserProfile> users) {
        ArrayList<String> list = new ArrayList();
        if (users != null) {
            for (UserProfile p : users) {
                list.add(p.getUserIdentifier());
            }
        }
        return list;
    }

    public static boolean isMyMessage(String currentUserId, MMXMessage mmxMessage) {
        User sender = mmxMessage.getSender();
        boolean isMine = false;
        if (sender != null && sender.getUserIdentifier() != null) {
            isMine = sender.getUserIdentifier().equals(currentUserId);
        }
        return isMine;
    }

}
