package com.magnet.magnetchat.presenters.chatlist.impl;

import android.support.annotation.NonNull;

import com.magnet.magnetchat.R;
import com.magnet.magnetchat.model.MMXMessageWrapper;
import com.magnet.magnetchat.model.MMXPollMessageWrapper;
import com.magnet.magnetchat.model.MMXPollOptionWrapper;
import com.magnet.magnetchat.model.converters.BaseConverter;
import com.magnet.magnetchat.presenters.chatlist.MMXPollContract;
import com.magnet.mmx.client.api.MMX;
import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.mmx.client.api.MMXMessage;
import com.magnet.mmx.client.ext.poll.MMXPoll;
import com.magnet.mmx.client.ext.poll.MMXPollOption;

import java.util.Collection;
import java.util.List;

/**
 * Created by aorehov on 05.05.16.
 */
class DefaultMMXPollPresenter extends BaseMMXMessagePresenterImpl<MMXPollContract.View> implements MMXPollContract.Presenter {

    private final BaseConverter<MMXPoll, List<MMXPollOptionWrapper>> converter;
    private MMXPollMessageWrapper pollMessageWrapper;


    public DefaultMMXPollPresenter(BaseConverter<MMXPoll, List<MMXPollOptionWrapper>> converter, MMXPollContract.View view) {
        this.converter = converter;
        this.view = view;
    }

    @Override
    public void setMMXMessage(MMXMessageWrapper wrapper) {
        if (wrapper instanceof MMXPollMessageWrapper) {
            pollMessageWrapper = (MMXPollMessageWrapper) wrapper;
        } else {
            pollMessageWrapper = new MMXPollMessageWrapper(wrapper);
        }

        setWrapper(pollMessageWrapper);

    }

    @Override
    void updateUI(@NonNull MMXPollContract.View view, @NonNull MMXMessageWrapper wrapper) {
        if (pollMessageWrapper.isReadyForUsing()) {
            view.onPollType(pollMessageWrapper.isMultipleChoice() ? R.string.mmxchat_poll_type_multiple : R.string.mmxchat_poll_type_single);
            view.onPollQuestion(pollMessageWrapper.getQuestion());
            List<MMXPollOptionWrapper> options = pollMessageWrapper.getMmxPollOptions();
            view.onPollAnswersReceived(options);
            checkButton(pollMessageWrapper.getMmxPollOptions());
        } else {
            pollMessageWrapper.loadPoll(converter, callback);
            view.onEnableSubmitButton(false);
        }

    }

    @Override
    public void onNeedChangedState(MMXPollOptionWrapper wrapper) {
        List<MMXPollOptionWrapper> options = pollMessageWrapper.getMmxPollOptions();
        if (!options.contains(wrapper)) return;

        wrapper.setSelectedLocal(!wrapper.isSelectedLocal());
        if (!pollMessageWrapper.isMultipleChoice()) {
            deselectAllExcept(wrapper);
        }

        view.onPollAnswersReceived(options);
        checkButton(options);

    }

    @Override
    public void submitAnswers() {
        List<MMXPollOption> options = pollMessageWrapper.getSelectedOptions();
        if (options == null) return;
        view.onRefreshing();
        pollMessageWrapper.getMmxPoll().choose(options, pollCallback);
    }

    @Override
    public void doRefresh() {
        view.onRefreshing();
        pollMessageWrapper.getMmxPoll().refreshResults(new MMXChannel.OnFinishedListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                view.onRefreshingFinished();
                updateUI(view, pollMessageWrapper);
            }

            @Override
            public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                view.onRefreshingFinished();
                view.showMessage("Can't update poll");
            }
        });
    }

    private void checkButton(Collection<MMXPollOptionWrapper> options) {
        view.onEnableSubmitButton(MMXPollOptionWrapper.isHasChangedState(options));
    }

    private void deselectAllExcept(MMXPollOptionWrapper wrapper) {
        for (MMXPollOptionWrapper option : pollMessageWrapper.getMmxPollOptions()) {
            if (!option.equals(wrapper) && option.isSelectedLocal()) {
                option.setSelectedLocal(false);
            }
        }
    }

    private com.magnet.magnetchat.model.MMXPollMessageWrapper.PollLoadListener callback = new MMXPollMessageWrapper.PollLoadListener() {
        @Override
        public void onSuccess(MMXPoll poll, List<MMXPollOptionWrapper> wrappers) {
            setMMXMessage(wrapper);
        }
    };

    private MMX.OnFinishedListener<MMXMessage> pollCallback = new MMX.OnFinishedListener<MMXMessage>() {
        @Override
        public void onSuccess(MMXMessage mmxMessage) {
            view.showMessage("Updated");
            setMMXMessage(wrapper);
            view.onRefreshingFinished();
        }

        @Override
        public void onFailure(MMX.FailureCode failureCode, Throwable throwable) {
            view.showMessage("Can't update poll");
            view.onRefreshingFinished();
        }
    };
}
