package com.magnet.wru;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.mmx.client.api.MMXMessage;
import com.magnet.mmx.util.Base64;
import com.magnet.mmx.util.DisposableFile;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MessageListFragment extends Fragment {
  private static final String TAG = MessageListFragment.class.getSimpleName();
  static final String KEY_MESSAGE_TEXT = "textContent";
  static final String KEY_MESSAGE_SENDER = "messageSender";
  static final String KEY_ATTACHMENT = "attachment";
  static final String KEY_ATTACHMENT_MIME_TYPE = "attachmentMimeType";
  private DisposableFile mPickedFile = null;
  private ImageButton mGalleryButton = null;
  private ImageButton mSendButton = null;
  private EditText mSendText = null;
  private ListView mMessageListView = null;
  private MessageListAdapter mMessageListAdapter = null;
  private MyMessageStore.OnChangeListener mMessageListListener = new MyMessageStore.OnChangeListener() {
    public void onChange() {
      getActivity().runOnUiThread(new Runnable() {
        public void run() {
          mMessageListAdapter.notifyDataSetChanged();
          mMessageListView.smoothScrollToPosition(mMessageListAdapter.getCount());
        }
      });
    }
  };

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_message_list, container);
    mSendText = (EditText) view.findViewById(R.id.message_text);
    mSendButton = (ImageButton) view.findViewById(R.id.btn_send);
    mGalleryButton = (ImageButton) view.findViewById(R.id.btn_attach);
    mMessageListView = (ListView) view.findViewById(R.id.message_list_view);
    mMessageListAdapter = new MessageListAdapter(getActivity(), MyMessageStore.getMessageList());
    mMessageListView.setAdapter(mMessageListAdapter);
    mSendText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (event != null
                && event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                || actionId == EditorInfo.IME_ACTION_DONE) {
          doSendMessage();
        }
        return false;
      }
    });

    MyMessageStore.registerListener(mMessageListListener);
    return view;
  }

  @Override
  public void onDestroyView() {
    MyMessageStore.unregisterListener(mMessageListListener);
    super.onDestroyView();
  }

  public void doAttach() {
    MediaUtil.startImagePickerActivityWithResult(this);
  }

  public void doSendMessage() {
    final String messageText = mSendText.getText().toString();
    if (messageText.isEmpty()) {
      //don't send an empty message
      return;
    }
    WRU wru = WRU.getInstance(getActivity());
    final HashMap<String, String> content = new HashMap<>();
    content.put(KEY_MESSAGE_TEXT, messageText);
    content.put(KEY_MESSAGE_SENDER, wru.getUsername());

    MMXChannel chatChannel = WRU.getInstance(getActivity()).getJoinedTopicChat();

    if (mPickedFile != null) {
      try {
        content.put(KEY_ATTACHMENT, Base64.encodeFromFile(mPickedFile.getAbsolutePath()));
        content.put(KEY_ATTACHMENT_MIME_TYPE, "image/jpeg");
        //TODO:  Figure out the MIME type
      } catch (IOException e) {
        Log.e(TAG, "doSendMessage(): exception caught while attaching file" + e);
        Toast.makeText(getActivity(), "Unable to attach file: " + e.getMessage(), Toast.LENGTH_LONG).show();
      }
    }

    String messageID = chatChannel.publish(content, new MMXMessage.OnFinishedListener<String>() {
      public void onSuccess(String s) {
        Toast.makeText(getActivity(), "Message sent.", Toast.LENGTH_LONG).show();

        getActivity().runOnUiThread(new Runnable() {
          @Override
          public void run() {
            mSendText.setText(null);
          }
        });
        if (mPickedFile != null) {
          mPickedFile.finish();
          mPickedFile = null;
        }
        updateViewState();
      }

      public void onFailure(MMXMessage.FailureCode failureCode, Throwable throwable) {
        Log.e(TAG, "doSendMessage() failure: " + failureCode, throwable);
        Toast.makeText(getActivity(), "Exception: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
      }
    });
    MyMessageStore.addMessage(messageID, content, new Date(), false);
  }

  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    try {
      mPickedFile = MediaUtil.getDisposableImageFromActivityResult(getActivity(), resultCode, data);
      updateViewState();
    } catch (IOException e) {
      Log.e(TAG, "onActivityResult(): caught exception", e);
      Toast.makeText(getActivity(), "Unable to attach file: " + e.getMessage(), Toast.LENGTH_LONG).show();
    }
  }

  public void doFragmentClick(View view) {
    if (view == mGalleryButton) {
      doAttach();
    } else if (view == mSendButton) {
      doSendMessage();
    }
  }

  private class MessageListAdapter extends BaseAdapter {
    private final int[] COLOR_IDS = {R.color.chat_1, R.color.chat_2, R.color.chat_3, R.color.chat_4, R.color.chat_5, R.color.chat_6};
    private static final int TYPE_ME = 0;
    private static final int TYPE_THEM = 1;
    private List<MyMessageStore.Message> mMessageList = null;
    private Context mContext;
    private LayoutInflater mInflater;
    private DateFormat mFormatter = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
    private ExecutorService mExecutors = Executors.newFixedThreadPool(3);

    public MessageListAdapter(Context context, List<MyMessageStore.Message> messageList) {
      mContext = context;
      mMessageList = messageList;
      mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
      int type = getItemViewType(position);
      int colorResId = 0;
      String datePostedStr = null;
      String messageStr = null;
      final MyMessageStore.Message message = getItem(position);
      final Map<String, String> messageContent = message.getContent();
      switch (type) {
        case TYPE_ME:
          if (convertView == null) {
            convertView = mInflater.inflate(R.layout.message_list_item_me, null);
          }
          colorResId = R.color.chat_me;
          datePostedStr = mFormatter.format(message.getTimestamp());
          messageStr = messageContent.get(KEY_MESSAGE_TEXT);
          break;
        case TYPE_THEM:
          if (convertView == null) {
            convertView = mInflater.inflate(R.layout.message_list_item_them, null);
          }
          //set author and color
          String authorStr = messageContent.get(KEY_MESSAGE_SENDER);
          colorResId = COLOR_IDS[Math.abs(authorStr.hashCode() % COLOR_IDS.length)];
          TextView author = (TextView) convertView.findViewById(R.id.author);
          author.setText(authorStr + " - ");
          datePostedStr = mFormatter.format(message.getTimestamp());
          Object textObj = messageContent.get(KEY_MESSAGE_TEXT);
          messageStr = textObj != null ? textObj.toString() : "<no text>";
          break;
      }
      boolean hasAttachment = messageContent.containsKey(KEY_ATTACHMENT);
      TextView datePosted = (TextView) convertView.findViewById(R.id.datePosted);
      datePosted.setText(datePostedStr);
      TextView messageText = (TextView) convertView.findViewById(R.id.messageText);
      LinearLayout messageBubble = (LinearLayout) convertView.findViewById(R.id.messageBubble);
      final ImageView imageView = (ImageView) convertView.findViewById(R.id.image);
      imageView.setVisibility(View.GONE);
      messageBubble.setBackgroundResource(colorResId);
      messageText.setText(messageStr);
      convertView.setOnClickListener(null);
      if (hasAttachment) {
        if (MyMessageStore.isBitmapCached(message.getId())) {
          Bitmap bitmap = MyMessageStore.getBitmap(message.getId(), null);
          setupDrawableView(imageView, bitmap);
        } else {
          mExecutors.execute(new Runnable() {
            public void run() {
              String attachment = messageContent.get(KEY_ATTACHMENT);
              //String mimeType = messageContent.get(KEY_ATTACHMENT_MIME_TYPE);
              //TODO: Do something with the MIME type
              Bitmap bitmap = MyMessageStore.getBitmap(message.getId(), attachment);
              setupDrawableView(imageView, bitmap);
            }
          });
        }
      }
      return convertView;
    }

    private void setupDrawableView(final ImageView imageView, final Bitmap bitmap) {
      Activity activity = getActivity();
      if (activity != null) {
        activity.runOnUiThread(new Runnable() {
          public void run() {
            imageView.setImageBitmap(bitmap);
            imageView.setVisibility(View.VISIBLE);
            imageView.setOnClickListener(new View.OnClickListener() {
              public void onClick(View v) {
                showImageDialog(mContext, bitmap);
              }
            });
          }
        });
      }
    }

    @Override
    public int getViewTypeCount() {
      return 2;
    }

    @Override
    public int getItemViewType(int position) {
      MyMessageStore.Message message = getItem(position);
      if (!message.isIncoming()) {
        //me
        return TYPE_ME;
      } else {
        //them
        return TYPE_THEM;
      }
    }

    @Override
    public int getCount() {
      return mMessageList.size();
    }

    @Override
    public MyMessageStore.Message getItem(int position) {
      return mMessageList.get(position);
    }

    @Override
    public long getItemId(int position) {
      return 0l;
    }

    private void showImageDialog(Context context, Bitmap bitmap) {
      LinearLayout imageLayout = (LinearLayout) mInflater.inflate(R.layout.dialog_image, null);
      ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);
      BitmapDrawable bitmapDrawable = new BitmapDrawable(getActivity().getResources(), bitmap);
      imageView.setImageDrawable(bitmapDrawable);
      AlertDialog dialog = new AlertDialog.Builder(context)
              .setView(imageLayout)
              .create();
      WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
      lp.copyFrom(dialog.getWindow().getAttributes());
      lp.width = WindowManager.LayoutParams.MATCH_PARENT;
      lp.height = WindowManager.LayoutParams.MATCH_PARENT;
      dialog.show();
      dialog.getWindow().setAttributes(lp);
    }
  }

  /**
   * This can be called from anywhere to make sure that the view is updated.
   */
  private void updateViewState() {
    getActivity().runOnUiThread(new Runnable() {
      public void run() {
        if (mPickedFile != null) {
          mGalleryButton.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
        } else {
          mGalleryButton.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        }
      }
    });
  }
}
