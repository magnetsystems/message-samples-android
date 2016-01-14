/*
 *  Copyright (c) 2016 Magnet Systems, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package com.magnet.samples.android.quickstart.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import com.magnet.max.android.Attachment;
import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.mmx.client.api.MMXMessage;
import com.magnet.samples.android.quickstart.R;
import com.magnet.samples.android.quickstart.helpers.AttachmentHelper;
import com.magnet.samples.android.quickstart.util.Logger;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Send a message to User(s) or Channel
 * @see <a href="https://developer.magnet.com/docs/message/v2.1/android/rich-message/index.html">Use Rich Message</a>
 */
public class NewMessageDialogFragment extends DialogFragment implements View.OnClickListener {

  public interface NewMessageListener {
    /**
     * Decorate the message
     * @param messageBuilder
     */
    void decorateMessage(MMXMessage.Builder messageBuilder);
    void messageSent(MMXMessage message);
    void messageFailure(Throwable error);
  }

  private NewMessageListener mNewMessageListener;
  private EditText mMessageEditText;
  private Switch attachmentSwitch;
  private MMXChannel mChannel;
  private String mTitle;

  public static NewMessageDialogFragment newInstance(String title) {
    NewMessageDialogFragment fragment = new NewMessageDialogFragment();
    Bundle args = new Bundle();
    args.putString("title", title);
    fragment.setArguments(args);
    return fragment;
  }

  public void setChannel(MMXChannel channel) {
    mChannel = channel;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mTitle = getArguments().getString("title");
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_send_message, container, false);

    return v;
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    attachmentSwitch = (Switch) view.findViewById(R.id.chatAttachmentOn);

    mMessageEditText = (EditText) view.findViewById(R.id.chatMessage);
    mMessageEditText.requestFocus();

    getDialog().setTitle(mTitle);
    getDialog().setCancelable(true);

    view.findViewById(R.id.btnSend).setOnClickListener(this);
    view.findViewById(R.id.btnCancel).setOnClickListener(this);

    getDialog().getWindow().setSoftInputMode(
        WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
  }

  @Override public void onAttach(Activity activity) {
    super.onAttach(activity);
    if(mNewMessageListener instanceof  NewMessageListener) {
      mNewMessageListener = (NewMessageListener) activity;
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    mNewMessageListener = null;
  }

  @Override public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btnSend:
        MMXMessage.Builder messageBuilder = createMessage();
        if(null != mNewMessageListener) {
          mNewMessageListener.decorateMessage(messageBuilder);
        }
        final MMXMessage message = messageBuilder.build();
        if(null != mChannel) {
          mChannel.publish(message, new MMXChannel.OnFinishedListener<String>() {
            @Override public void onSuccess(String s) {
              Logger.debug("send message to channel " + mChannel.getName() , "success");
              handleSendSuccess(message);
            }

            @Override
            public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
              String message = "Can't send message to channel " + mChannel.getName() + " due to " + failureCode + " : " + throwable.getMessage();
              handleSendFailure(message);
            }
          });
        } else {
        message.send(new MMXMessage.OnFinishedListener<String>() {
          @Override public void onSuccess(String s) {
            Logger.debug("send message", "success");
            handleSendSuccess(message);
          }

          @Override public void onFailure(MMXMessage.FailureCode failureCode, Throwable throwable) {
            String message = "Can't send message due to : " + failureCode + " : " + throwable.getMessage();
            handleSendFailure(message);
          }
        });
        }
        break;
      case R.id.btnCancel:
        getDialog().dismiss();
      default:
    }
  }

  private MMXMessage.Builder createMessage() {
    Map<String, String> content = new HashMap<>();
    content.put("content", mMessageEditText.getText().toString());
    MMXMessage.Builder builder = new MMXMessage.Builder();
    builder.content(content);
    if (attachmentSwitch.isChecked()) {
      try {
        Attachment attachment = AttachmentHelper.getRandomAttachment(getActivity());
        if (attachment != null) {
          builder.attachments(attachment);
        }
      } catch (IOException e) {
        Logger.error("attach file", e, "error : ");
      }
    }

    return builder;
  }

  private void handleSendSuccess(MMXMessage message) {
    if(null != mNewMessageListener) {
      mNewMessageListener.messageSent(message);
    }
    finish();
  }

  private void handleSendFailure(String message) {
    Logger.error(message);
    Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    if(null != mNewMessageListener) {
      mNewMessageListener.messageFailure(new Exception(message));
    }
    finish();
  }

  private void finish() {
    getDialog().dismiss();
  }
}
