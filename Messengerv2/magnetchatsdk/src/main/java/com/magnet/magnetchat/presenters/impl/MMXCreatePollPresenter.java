package com.magnet.magnetchat.presenters.impl;

import android.os.Bundle;

import com.magnet.magnetchat.R;
import com.magnet.magnetchat.model.converters.BaseConverter;
import com.magnet.magnetchat.presenters.MMXCreatePollContract;
import com.magnet.mmx.client.api.MMX;
import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.mmx.client.api.MMXMessage;
import com.magnet.mmx.client.ext.poll.MMXPoll;
import com.magnet.mmx.client.ext.poll.MMXPollOption;

import java.util.List;

/**
 * Created by aorehov on 17.05.16.
 */
public class MMXCreatePollPresenter implements MMXCreatePollContract.Presenter {

    private MMXCreatePollContract.View view;
    private MMXChannel mmxChannel;
    private BaseConverter<String, MMXPollOption> converter;

    public MMXCreatePollPresenter(MMXCreatePollContract.View view, BaseConverter<String, MMXPollOption> converter) {
        this.view = view;
        this.converter = converter;
    }

    @Override
    public void doCreate() {
        String question = view.getQuestion().trim();
        if (question.length() == 0) {
            view.showMessage(R.string.err_poll_question_empty);
            return;
        }

        List<String> answers = view.getAnswers();
        if (answers == null || answers.size() < 2) {
            view.showMessage(R.string.err_poll_answers_less);
            return;
        }

        for (String answer : answers) {
            if (answer.isEmpty()) {
                view.showMessage(R.string.err_poll_answer_empty);
                return;
            }
        }

        String name = view.getName();
        MMXPoll.Builder builder = new MMXPoll.Builder()
                .question(question)
                .name(name)
                .allowMultiChoice(view.isAllowMultipleChoice())
                .hideResultsFromOthers(view.isHiderResult())
                .options(converter.convert(answers));

        MMXPoll mmxPoll = builder.build();
        view.onLock();
        mmxPoll.publish(mmxChannel, new MMX.OnFinishedListener<MMXMessage>() {
            @Override
            public void onSuccess(MMXMessage mmxMessage) {
                view.onUnlock();
                view.onPollCreatedSuccess(mmxMessage);
            }

            @Override
            public void onFailure(MMX.FailureCode failureCode, Throwable throwable) {
                view.onUnlock();
                view.showMessage(R.string.mmx_poll_create_error);
            }
        });
    }

    @Override
    public void setMMXChannel(MMXChannel mmxChannel) {
        this.mmxChannel = mmxChannel;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onPaused() {

    }

    @Override
    public void onResumed() {

    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public Bundle onSaveInstance(Bundle savedInstances) {
        return null;
    }

    @Override
    public void onRestore(Bundle savedInstances) {

    }
}
