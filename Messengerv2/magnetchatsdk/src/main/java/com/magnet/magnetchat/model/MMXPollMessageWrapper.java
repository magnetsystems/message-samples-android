package com.magnet.magnetchat.model;

import android.support.annotation.NonNull;

import com.magnet.magnetchat.helpers.MMXObjectsHelper;
import com.magnet.magnetchat.model.converters.MMXPollOptionWrapperConverter;
import com.magnet.max.android.ApiCallback;
import com.magnet.max.android.ApiError;
import com.magnet.mmx.client.api.MMXMessage;
import com.magnet.mmx.client.ext.poll.MMXPoll;

import java.util.Date;
import java.util.List;

/**
 * Created by aorehov on 06.05.16.
 */
public class MMXPollMessageWrapper extends MMXMessageWrapper {

    private MMXPoll mmxPoll;
    private List<MMXPollOptionWrapper> mmxPollOptions;

    public MMXPollMessageWrapper(MMXMessageWrapper mmxMessageWrapper) {
        this(mmxMessageWrapper.obj, mmxMessageWrapper.isMyMessage());
    }

    public MMXPollMessageWrapper(MMXMessage obj, boolean isMyMessage) {
        super(obj, isMyMessage ? TYPE_POLL_MY : TYPE_POLL_ANOTHER, isMyMessage);
    }

    public MMXPollMessageWrapper(MMXMessage obj, boolean isMyMessage, Date date) {
        super(obj, isMyMessage ? TYPE_POLL_MY : TYPE_POLL_ANOTHER, isMyMessage, date);
    }

    public MMXPoll getMmxPoll() {
        return mmxPoll;
    }

    public List<MMXPollOptionWrapper> getMmxPollOptions() {
        return mmxPollOptions;
    }

    public boolean isReadyForUsing() {
        return mmxPoll != null;
    }

    public boolean isMultipleChoice() throws IllegalStateException {
        if (mmxPoll == null)
            throw new IllegalStateException("MMXPoll object is not initialized. You should call MMXPollMessageWrapper.loadPoll previously");
        return mmxPoll.isAllowMultiChoices();
    }

    public boolean isHideResult() throws IllegalStateException {
        if (mmxPoll == null)
            throw new IllegalStateException("MMXPoll object is not initialized,. You should call MMXPollMessageWrapper.loadPoll previously");
        return mmxPoll.shouldHideResultsFromOthers();
    }

    public String getQuestion() {
        return mmxPoll == null ? null : mmxPoll.getQuestion();
    }

    public String getName() {
        return mmxPoll == null ? null : mmxPoll.getName();
    }

    public void loadPoll(MMXPollOptionWrapperConverter converter) {
        loadPoll(converter, null, null);
    }

    public void loadPoll(MMXPollOptionWrapperConverter converter, PollLoadListener callback) {
        loadPoll(converter, callback, null);
    }

    public void loadPoll(@NonNull final MMXPollOptionWrapperConverter converter, final PollLoadListener callback, final PollLoadErrorListener errorCallback) {
        MMXObjectsHelper.loadPollFromMessage(getObj(), new ApiCallback<MMXPoll>() {
            @Override
            public void success(MMXPoll poll) {
                List<MMXPollOptionWrapper> list = converter.convert(poll);
                mmxPoll = poll;
                mmxPollOptions = list;
                if (callback != null) callback.onSuccess(mmxPoll, mmxPollOptions);
            }

            @Override
            public void failure(ApiError apiError) {
                if (errorCallback != null) errorCallback.onError(apiError);
            }
        });
    }

    public interface PollLoadListener {
        void onSuccess(MMXPoll poll, List<MMXPollOptionWrapper> wrappers);
    }

    public interface PollLoadErrorListener {
        void onError(ApiError error);
    }
}
