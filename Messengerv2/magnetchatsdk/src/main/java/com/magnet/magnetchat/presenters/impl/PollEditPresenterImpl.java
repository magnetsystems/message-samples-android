package com.magnet.magnetchat.presenters.impl;

import android.os.Bundle;

import com.magnet.magnetchat.R;
import com.magnet.magnetchat.model.Message;
import com.magnet.magnetchat.presenters.PollEditContract;
import com.magnet.mmx.client.api.MMX;
import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.mmx.client.api.MMXMessage;
import com.magnet.mmx.client.ext.poll.MMXPoll;

import java.util.List;

/**
 * Created by aorehov on 27.04.16.
 */
class PollEditPresenterImpl implements PollEditContract.Presenter {

    private PollEditContract.View view;
    private MMXChannel channel;

    private List<String> tempAnswers = null;

    public PollEditPresenterImpl(PollEditContract.View view, MMXChannel channel) {
        this.view = view;
        this.channel = channel;
    }

    public PollEditPresenterImpl() {
    }

    @Override
    public void doSaveAction() {
        String question = view.getQuestion().trim();

        if (question.length() < 1) {
            view.onMessage(R.string.mmx_poll_question_empty);
            return;
        }

        List<String> list = view.getAnswersList();
        if (list == null || list.size() < 2) {
            view.onMessage(R.string.mmx_poll_empty);
            return;
        }


        MMXPoll.Builder builder = new MMXPoll.Builder()
                .question(question);

        for (String answer : view.getAnswersList()) {
            String preparedAnswer = answer.trim();
            if (preparedAnswer.length() < 1) {
                view.onMessage(R.string.mmx_poll_answer_empty);
                return;
            }
            builder.option(preparedAnswer);
        }

        builder.allowMultiChoice(view.isMultipleChoice());
        builder.extras(Message.makePollContent());

        MMXPoll poll = builder.build();

        view.onLockScreen();
        poll.publish(channel, innerCallback);

    }

    @Override
    public void setView(PollEditContract.View view) {
        this.view = view;

    }

    @Override
    public void setMMXChannel(MMXChannel mmxChannel) {
        this.channel = mmxChannel;
    }

    @Override
    public void onCreate(Bundle bundle, Bundle savedInstances) {
        readAnswers(bundle, savedInstances);
        updateUI();
    }

    private void readAnswers(Bundle bundle, Bundle savedInstances) {
        List<String> answers = null;
        if (savedInstances != null) {
//            read from saved instances
        } else if (bundle != null) {
//            read sent data
        }

        if (answers != null && view != null) view.onAnswersList(answers);
    }

    private void updateUI() {
        if (view != null && tempAnswers != null) {
            view.onAnswersList(tempAnswers);
        }
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    private MMX.OnFinishedListener<MMXMessage> innerCallback = new MMX.OnFinishedListener<MMXMessage>() {
        @Override
        public void onSuccess(MMXMessage mmxMessage) {
            view.onUnlockScreen();
            view.onPollSaved(mmxMessage);
        }

        @Override
        public void onFailure(MMX.FailureCode failureCode, Throwable throwable) {
            view.onMessage(R.string.mmx_poll_create_error);
            view.onUnlockScreen();
        }
    };
}
