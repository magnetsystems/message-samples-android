package com.magnet.magnetchat.helpers;

import android.support.annotation.NonNull;
import android.util.Log;

import com.magnet.magnetchat.model.MMXPollOptionWrapper;
import com.magnet.max.android.ApiCallback;
import com.magnet.max.android.ApiError;
import com.magnet.max.android.User;
import com.magnet.max.android.UserProfile;
import com.magnet.mmx.client.api.MMX;
import com.magnet.mmx.client.api.MMXMessage;
import com.magnet.mmx.client.api.MMXTypedPayload;
import com.magnet.mmx.client.ext.poll.MMXPoll;
import com.magnet.mmx.client.ext.poll.MMXPollOption;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by aorehov on 27.04.16.
 */
public class MMXObjectsHelper {

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

    public static void loadPollFromMessage(@NonNull MMXMessage mmxMessage, @NonNull final ApiCallback<MMXPoll> callback) {
        MMXTypedPayload payload = mmxMessage.getPayload();
        if (payload == null || !(payload instanceof MMXPoll.MMXPollIdentifier)) {
            callback.failure(new ApiError("MMXMessage is not a MMXPoll"));
            return;
        }

        MMXPoll.MMXPollIdentifier pollIdentifier = (MMXPoll.MMXPollIdentifier) payload;
        if (null == pollIdentifier) {
            callback.failure(new ApiError("MMXPollIdentifier is null"));
            return;
        }

        MMXPoll.get(pollIdentifier.getPollId(), new MMX.OnFinishedListener<MMXPoll>() {
            @Override
            public void onSuccess(MMXPoll result) {
                callback.success(result);
            }

            @Override
            public void onFailure(MMX.FailureCode code, Throwable ex) {
                callback.failure(new ApiError(code.getDescription(), ex));
            }
        });
    }

}
