package com.magnet.magnetchat.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.bumptech.glide.Glide;
import com.magnet.magnetchat.R;
import com.magnet.magnetchat.helpers.DateHelper;
import com.magnet.magnetchat.helpers.UserHelper;
import com.magnet.magnetchat.model.Conversation;
import com.magnet.magnetchat.model.Message;
import com.magnet.magnetchat.ui.views.CircleNameView;
import com.magnet.magnetchat.util.AppLogger;
import com.magnet.magnetchat.util.Utils;
import com.magnet.max.android.ApiCallback;
import com.magnet.max.android.ApiError;
import com.magnet.max.android.Attachment;
import com.magnet.max.android.User;
import com.magnet.max.android.util.StringUtil;
import com.magnet.mmx.client.api.MMX;
import com.magnet.mmx.client.api.MMXMessage;
import com.magnet.mmx.client.ext.poll.MMXPoll;
import com.magnet.mmx.client.ext.poll.MMXPollOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageContentViewHolder> {
    private static final int CONTENT_TYPE_TEXT = 1;
    private static final int CONTENT_TYPE_IMAGE = 2;
    private static final int CONTENT_TYPE_POLL = 3;
    private static final int CONTENT_TYPE_POLL_ANSWER = 4;

    private final static String TAG = MessagesAdapter.class.getSimpleName();

    private LayoutInflater inflater;
    private List<Message> messageList;
    private Context context;
    private Conversation currentConversation;

    public class MessageContentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected LinearLayout messageArea;
        protected AppCompatTextView tvDate;
        protected AppCompatTextView sender;
        protected AppCompatTextView text;
        protected AppCompatTextView delivered;
        protected FrameLayout flContent;
        protected ImageView imageMyAvatar;
        protected ImageView imageOtherAvatar;
        protected CircleNameView viewMyAvatar;
        protected CircleNameView viewOtherAvatar;

        protected Message message;

        public MessageContentViewHolder(View itemView, View contentView, int contentType) {
            super(itemView);
            this.messageArea = (LinearLayout) itemView.findViewById(R.id.itemMessageArea);
            this.tvDate = (AppCompatTextView) itemView.findViewById(R.id.itemMessageDate);
            this.sender = (AppCompatTextView) itemView.findViewById(R.id.itemMessageSender);
            this.text = (AppCompatTextView) itemView.findViewById(R.id.itemMessageText);
            this.delivered = (AppCompatTextView) itemView.findViewById(R.id.itemMessageDelivered);
            this.imageMyAvatar = (ImageView) itemView.findViewById(R.id.imageMyAvatar);
            this.imageMyAvatar.setBackgroundResource(android.R.color.transparent);
            this.imageMyAvatar.setImageResource(android.R.color.transparent);
            this.imageOtherAvatar = (ImageView) itemView.findViewById(R.id.imageOtherAvatar);
            this.imageOtherAvatar.setBackgroundResource(android.R.color.transparent);
            this.imageOtherAvatar.setImageResource(android.R.color.transparent);
            this.viewMyAvatar = (CircleNameView) itemView.findViewById(R.id.viewMyAvatar);
            this.viewOtherAvatar = (CircleNameView) itemView.findViewById(R.id.viewOtherAvatar);

            this.flContent = (FrameLayout) itemView.findViewById(R.id.flContent);
            if(null != contentView) {
                FrameLayout.LayoutParams layoutParams =
                    new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                flContent.addView(contentView, layoutParams);
            } else {
                flContent.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View v) {
            try {
                onOpenAttachment();
            } catch (Exception e) {
                AppLogger.error(this, e.toString());
            }
        }

        public void showMessage(Message message, Message previousMessage, Message nextMessage) {
            if(null != this.message && this.message.equals(message)) {
                return;
            }

            this.message = message;
            boolean sameDate = configureDate(previousMessage);
            if (message.getSender() == null || StringUtil.isStringValueEqual(User.getCurrentUserId(), message.getSender().getUserIdentifier())) {
                makeMessageFromMe(nextMessage, sameDate);
            } else {
                makeMessageToMe(previousMessage);
            }

            showMessageContent();
        }

        protected void showMessageContent() {
            if(Message.TYPE_TEXT.equals(message.getType())) {
                text.setText(message.getText());
                text.setVisibility(View.VISIBLE);
            }
        }

        protected void makeMessageToMe(Message previous) {
            imageMyAvatar.setVisibility(View.GONE);
            viewMyAvatar.setVisibility(View.GONE);
            imageOtherAvatar.setVisibility(View.VISIBLE);
            imageOtherAvatar.setBackgroundResource(android.R.color.transparent);
            imageOtherAvatar.setImageResource(android.R.color.transparent);
            viewOtherAvatar.setVisibility(View.VISIBLE);

            messageArea.setGravity(Gravity.LEFT | Gravity.START);
            text.setBackgroundResource(R.drawable.bubble_odd);
            text.setTextColor(Color.BLACK);
            delivered.setVisibility(View.GONE);
            if (message.getSender() != null) {
                String userName = UserHelper.getDisplayName(message.getSender());
                String previousUser = "";
                if (previous != null) {
                    previousUser = UserHelper.getDisplayName(previous.getSender());
                }
                viewOtherAvatar.setUserName(userName);
                sender.setText(userName);
                if (userName.equalsIgnoreCase(previousUser)) {
                    sender.setVisibility(View.GONE);
                } else {
                    sender.setVisibility(View.VISIBLE);
                }
                if (null != message.getSender().getAvatarUrl()) {
                    Glide.with(context)
                        .load(message.getSender().getAvatarUrl())
                        .fitCenter()
                        .into(imageOtherAvatar);
                }
            }
        }

        protected void makeMessageFromMe(Message next, boolean previousSameDate) {
            User user = User.getCurrentUser();

            imageMyAvatar.setVisibility(View.VISIBLE);
            imageMyAvatar.setBackgroundResource(android.R.color.transparent);
            imageMyAvatar.setImageResource(android.R.color.transparent);
            viewMyAvatar.setVisibility(View.VISIBLE);
            imageOtherAvatar.setVisibility(View.GONE);
            viewOtherAvatar.setVisibility(View.GONE);

            messageArea.setGravity(Gravity.RIGHT | Gravity.END);
            text.setBackgroundResource(R.drawable.bubble2);
            text.setTextColor(Color.WHITE);
            sender.setVisibility(View.GONE);

            String previousUserId = "";
            if (next != null) {
                if (next.getSender() != null) {
                    previousUserId = next.getSender().getUserIdentifier();
                } else {
                    previousUserId = User.getCurrentUserId();
                }
            }
            if (next == null || !User.getCurrentUserId().equals(previousUserId)) {
                delivered.setVisibility(View.VISIBLE);
                switch (message.getMessageStatus()) {
                    case DELIVERED:
                        delivered.setText("Delivered");
                        break;
                    case ERROR:
                        delivered.setText("Error");
                        break;
                    case PENDING:
                        delivered.setText("Pending");
                        break;
                }
            } else {
                delivered.setVisibility(View.GONE);
            }

            if ((user != null)) {
                String userName = UserHelper.getDisplayName(user);
                viewMyAvatar.setUserName(userName);
                if ((null != user.getAvatarUrl())) {
                    Glide.with(context)
                        .load(user.getAvatarUrl())
                        .fitCenter()
                        .into(imageMyAvatar);
                }
            }
        }

        /**
         * Returns true if current message and previous have the same date
         * @param previous
         * @return
         */
        public boolean configureDate(Message previous) {
            Date date;
            Date previousDate = null;
            if (message.getCreateTime() == null) {
                date = new Date();
            } else {
                date = DateHelper.utcToLocal(message.getCreateTime());
            }
            if (previous != null) {
                previousDate = DateHelper.utcToLocal(null != previous.getCreateTime() ? previous.getCreateTime() : new Date());
            }
            String msgDate = DateHelper.getMessageDateTime(date);
            String previousMsgDate = null;
            if (previousDate != null) {
                previousMsgDate = DateHelper.getMessageDateTime(previousDate);
            }
            if (!msgDate.equalsIgnoreCase(previousMsgDate)) {
                tvDate.setVisibility(View.VISIBLE);
                tvDate.setText(msgDate);
                return true;
            } else {
                tvDate.setVisibility(View.GONE);
            }
            return false;
        }

        /**
         * Method which provide the opening of the attachment
         *
         * @throws Exception
         */
        private void onOpenAttachment() throws Exception {
            Intent intent;
            if (message.getType() != null) {
                switch (message.getType()) {
                    case Message.TYPE_MAP:
                        if (!Utils.isGooglePlayServiceInstalled(context)) {
                            Utils.showMessage(context, "It seems Google play services is not available, can't use location API");
                        } else {
                            String uri = String.format(Locale.ENGLISH, "geo:%s?z=16&q=%s", message.getLatitudeLongitude(), message.getLatitudeLongitude());
                            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                            try {
                                context.startActivity(intent);
                            } catch (Throwable e) {
                                Log.e(TAG, "Can find any app to show map", e);
                                Utils.showMessage(context, "Can find any app to show map");
                            }
                        }
                        break;
                    case Message.TYPE_VIDEO:
                        String newVideoPath = message.getAttachment().getDownloadUrl();
                        Log.d(TAG, "paying video : " + newVideoPath + "\n" + message.getAttachment());
                        if (newVideoPath != null) {
                            String type = "video/*";
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                type = message.getAttachment().getMimeType();
                            }
                            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(newVideoPath));
                            intent.setDataAndType(Uri.parse(newVideoPath), type);
                            try {
                                context.startActivity(intent);
                            } catch (Throwable e) {
                                Log.e(TAG, "Can find any app to play video", e);
                                Utils.showMessage(context, "Can find any app to play video");
                            }
                        }
                        break;
                    case Message.TYPE_PHOTO:
                        if (message.getAttachment() != null) {
                            String newImagePath = message.getAttachment().getDownloadUrl();
                            Log.d(TAG, "Viewing photo : " + newImagePath + "\n" + message.getAttachment());
                            if (newImagePath != null) {
                                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(newImagePath));
                                intent.setDataAndType(Uri.parse(newImagePath), "image/*");
                                try {
                                    context.startActivity(intent);
                                } catch (Throwable e) {
                                    Log.e(TAG, "Can find any app to view image", e);
                                    Utils.showMessage(context, "Can find any app to view image");
                                }
                            }
                        }
                        break;
                    case Message.TYPE_POLL:

                        break;
                }
            }
        }

    }

    public class TextContentViewHolder extends MessageContentViewHolder {

        public TextContentViewHolder(View itemView, View contentView, int contentType) {
            super(itemView, contentView, contentType);
        }
    }

    public class ImageContentViewHolder extends MessageContentViewHolder {
        ImageView imageView;

        public ImageContentViewHolder(View itemView, View contentView, int contentType) {
            super(itemView, contentView, contentType);
            imageView = (ImageView) contentView.findViewById(R.id.itemMessageImage);
        }

        public void showMessageContent() {
            text.setVisibility(View.GONE);

            switch (message.getType()) {
                case Message.TYPE_MAP:
                    configureMapMsg();
                    break;
                case Message.TYPE_VIDEO:
                    configureVideoMsg();
                    break;
                case Message.TYPE_PHOTO:
                    configImageMsg();
                    break;
            }
        }

        private void configImageMsg() {
            final Attachment attachment = message.getAttachment();
            if (attachment != null) {
                String attachmentId = null;
                try {
                    attachmentId = attachment.getDownloadUrl();
                } catch (IllegalStateException e) {
                    Log.d(TAG, "Attachment is not ready2", e);
                }
                if (null != attachmentId) {
                    Glide.with(context)
                        .load(Uri.parse(attachmentId))
                        .centerCrop()
                        .placeholder(R.drawable.photo_msg)
                        .into(imageView);
                } else {
                    Glide.with(context)
                        .load(R.drawable.photo_msg)
                        .centerCrop()
                        .into(imageView);
                }
            }
        }

        private void configureMapMsg() {
            String loc = "http://maps.google.com/maps/api/staticmap?center=" + message.getLatitudeLongitude() + "&zoom=18&size=700x300&sensor=false&markers=color:blue%7Clabel:S%7C" + message.getLatitudeLongitude();
            Glide.with(context).load(loc).placeholder(R.drawable.map_msg).centerCrop().into(imageView);
        }

        private void configureVideoMsg() {
            imageView.setImageResource(R.drawable.video_msg);
            imageView.setContentDescription("Click to watch the video");
        }
    }

    public class PollContentViewHolder extends MessageContentViewHolder {

        @InjectView(R.id.rvOptions) ListView rvOptions;
        Button btnSubmit;
        @InjectView(R.id.tvName) TextView tvName;
        @InjectView(R.id.tvQuestion) TextView tvQuestion;

        View footer;

        PollOptionAdapter adapter;

        MMXPoll mmxPoll;

        public PollContentViewHolder(View itemView, View contentView, View footer, int contentType) {
            super(itemView, contentView, contentType);

            ButterKnife.inject(this, contentView);

            this.footer = footer;
        }

        @Override protected void showMessageContent() {
            text.setVisibility(View.GONE);
            message.getPoll(new ApiCallback<MMXPoll>() {
                @Override public void success(MMXPoll poll) {
                    showPoll(poll);
                    //if(null != poll && !poll.equals(mmxPoll)) {
                    //    showPoll(poll);
                    //    mmxPoll = poll;
                    //    Log.d(TAG, "--------previous poll : " + mmxPoll + "\n------------current poll : " + poll);
                    //} else {
                    //    Log.d(TAG, "--------Binding the same poll " + poll);
                    //}
                }

                @Override public void failure(ApiError error) {
                    Log.e(TAG, "Failed to get poll " + error.toString());
                    //FIXME testing
                    MMXPoll testPoll = new MMXPoll.Builder().name("Favorite Color").question("What's your favorite color ?")
                        .option("Red").option("Blue").option("Yellow").allowMultiChoice(false).build();
                    showPoll(testPoll);
                }
            });
        }

        private void showPoll(final MMXPoll poll) {
            if(null != poll) {
                if(poll.equals(mmxPoll)) {
                    Log.d(TAG, "Same poll binding, just refresh, --------previous poll : " + mmxPoll + "\n------------current poll : " + poll);
                    adapter.notifyDataSetChanged();
                    return;
                }

                mmxPoll = poll;

                tvName.setText(poll.getName());
                tvQuestion.setText(poll.getQuestion());

                Log.d(TAG, "-----------------Allowing multichoice " + poll.getName() + " : " + poll.isAllowMultiChoices());
                rvOptions.setChoiceMode(poll.isAllowMultiChoices() ? ListView.CHOICE_MODE_MULTIPLE : ListView.CHOICE_MODE_SINGLE);

                boolean showCount = !poll.shouldHideResultsFromOthers() || (poll.shouldHideResultsFromOthers()
                    && poll.getOwnerId().equals(User.getCurrentUserId()));
                if(null != adapter) {
                    adapter.setShowCount(showCount);
                    adapter.resetData(poll.getOptions());
                } else {
                    adapter = new PollOptionAdapter(context, poll.getOptions(),showCount);
                    rvOptions.setAdapter(adapter);
                }
                if(poll.isAllowMultiChoices()) {
                    rvOptions.addFooterView(footer, null, false);

                    btnSubmit = (Button) footer.findViewById(R.id.btnSubmit);
                    btnSubmit.setOnClickListener(new View.OnClickListener() {
                        @Override public void onClick(View v) {
                            List<MMXPollOption> chosenOptions = new ArrayList<MMXPollOption>();
                            SparseBooleanArray checkedItemPositions = rvOptions.getCheckedItemPositions();
                            for(int i = 0; i< checkedItemPositions.size(); i++) {
                                if(checkedItemPositions.valueAt(i)){
                                    chosenOptions.add(poll.getOptions().get(checkedItemPositions.keyAt(i)));
                                }
                            }

                            poll.choose(chosenOptions, new MMX.OnFinishedListener<MMXMessage>() {
                                @Override public void onSuccess(MMXMessage message) {
                                    addMessage(message);
                                }

                                @Override public void onFailure(MMX.FailureCode failureCode,
                                    Throwable throwable) {
                                    Log.d(TAG, "Failed to choose option", throwable);
                                }
                            });
                        }
                    });
                } else {
                    if(rvOptions.getFooterViewsCount() > 0) {
                        rvOptions.removeFooterView(footer);
                    }
                }

                rvOptions.clearChoices();
                if(null != poll.getMyVotes() && !poll.getMyVotes().isEmpty()) {
                    for(MMXPollOption option : poll.getMyVotes()) {
                        int index = poll.getOptions().indexOf(option);
                        if(index > -1) {
                            rvOptions.setItemChecked(index, true);
                        }
                    }
                }

                rvOptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position,long arg3) {
                        //view.setSelected(false);
                        //boolean isSelected = view.isSelected();
                        //boolean isItemChecked = rvOptions.isItemChecked(position);
                        //SparseBooleanArray checkedPositions = rvOptions.getCheckedItemPositions();
                        //if(checkedPositions.get(position)) {
                        //    //checkedPositions.delete(position);
                        //    //rvOptions.setItemChecked(position, false);
                        //
                        //} else {
                        //    rvOptions.setItemChecked(position, true);
                        //}

                        if(!poll.isAllowMultiChoices()) {
                            poll.choose(poll.getOptions().get(position), new MMX.OnFinishedListener<MMXMessage>() {
                                @Override public void onSuccess(MMXMessage message) {
                                    addMessage(message);
                                }

                                @Override public void onFailure(MMX.FailureCode failureCode,
                                    Throwable throwable) {
                                    Log.d(TAG, "Failed to choose option", throwable);
                                }
                            });
                        }
                    }
                });

                //if (null == adapter) {
                //    adapter = new PollOptionAdapter(context, poll.getOptions());
                //    rvOptions.setAdapter(adapter);
                //    if(poll.isAllowMultiChoices()) {
                //        rvOptions.addFooterView(footer, null, false);
                //    }
                //
                //    rvOptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                //        @Override
                //        public void onItemClick(AdapterView<?> parent, View view, int position,long arg3) {
                //            //view.setSelected(true);
                //            rvOptions.setItemChecked(position, true);
                //        }
                //    });
                //} else {
                //    adapter.clear();
                //    adapter.addAll(poll.getOptions());
                //    adapter.notifyDataSetChanged();
                //    //rvOptions.invalidate();
                //    //if(rvOptions.getFooterViewsCount() == 1) {
                //    //    if (!poll.isAllowMultiChoices()) {
                //    //        rvOptions.removeFooterView(footer);
                //    //    }
                //    //} else if(rvOptions.getFooterViewsCount() == 0) {
                //    //    if(poll.isAllowMultiChoices()) {
                //    //        rvOptions.addFooterView(footer, null, false);
                //    //    }
                //    //}
                //}

                //footer.setVisibility(poll.isAllowMultiChoices() ? View.VISIBLE : View.GONE);
            } else { // testing
                Log.e(TAG, "Poll is null");
                //tvName.setText("Favoriate Color");
                //tvQuestion.setText("What's your favorite color");
                //
                //rvOptions.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                //
                //if (null == adapter) {
                //    List<MMXPollOption> pollOptions = new ArrayList<MMXPollOption>();
                //    pollOptions.add(new MMXPollOption("Red"));
                //    pollOptions.add(new MMXPollOption("Blue"));
                //    pollOptions.add(new MMXPollOption("Yellow"));
                //    adapter = new PollOptionAdapter(context, /**poll.getOptions()*/pollOptions, true);
                //    rvOptions.setAdapter(adapter);
                //    rvOptions.addFooterView(footer);
                //} else {
                //    //adapter.clear();
                //    //adapter.addAll(poll.getOptions());
                //    //adapter.notifyDataSetChanged();
                //}

                //btnSubmit.setVisibility(View.VISIBLE);
            }
        }

        private void addMessage(MMXMessage mmxMessage) {
            if(null != mmxMessage) {
                currentConversation.addMessage(Message.createMessageFrom(mmxMessage));
            } else {
                Log.e(TAG, "MMXMessage is null");
            }
        }
    }

    public class PollAnswerContentViewHolder extends MessageContentViewHolder {

        public PollAnswerContentViewHolder(View itemView, int contentType) {
            super(itemView, null, contentType);
        }

        @Override protected void showMessageContent() {
            text.setVisibility(View.VISIBLE);

            MMXPoll.MMXPollAnswer pollAnswer =
                (MMXPoll.MMXPollAnswer) message.getMmxMessage().getPayload();
            if(null != pollAnswer) {
                text.setText(message.getSender().getDisplayName() + " chose " + pollAnswer.getSelectedOptionsAsString()  + " for poll " + pollAnswer.getName());
            } else {
                text.setText("");
            }
        }
    }

    public MessagesAdapter(Context context, List<Message> messages, Conversation currentConversation) {
        inflater = LayoutInflater.from(context);
        this.messageList = messages;
        this.context = context;
        this.currentConversation = currentConversation;
    }

    @Override public int getItemViewType(int position) {
        Message message = getItem(position);
        if (message.getType() != null) {
            switch (message.getType()) {
                case Message.TYPE_MAP:
                case Message.TYPE_VIDEO:
                case Message.TYPE_PHOTO:
                    return CONTENT_TYPE_IMAGE;
                case Message.TYPE_TEXT:
                    return CONTENT_TYPE_TEXT;
                case Message.TYPE_POLL:
                    return CONTENT_TYPE_POLL;
                case Message.TYPE_POLL_ANSWER:
                    return CONTENT_TYPE_POLL_ANSWER;
                default:
                    return CONTENT_TYPE_TEXT;
            }
        } else {
            return CONTENT_TYPE_TEXT;
        }
    }

    @Override
    public MessageContentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_message, parent, false);
        if(viewType == CONTENT_TYPE_TEXT) {
            return new TextContentViewHolder(view, null, viewType);
        } else if(viewType == CONTENT_TYPE_IMAGE) {
            View contentView = inflater.inflate(R.layout.view_image_content, parent, false);
            return new ImageContentViewHolder(view, contentView, viewType);
        } else if(viewType == CONTENT_TYPE_POLL) {
            View contentView = inflater.inflate(R.layout.view_poll, parent, false);
            View footer = inflater.inflate(R.layout.view_poll_footer, parent, false );
            return new PollContentViewHolder(view, contentView, footer, viewType);
        } else if(viewType == CONTENT_TYPE_POLL_ANSWER) {
            return new PollAnswerContentViewHolder(view, viewType);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(MessageContentViewHolder holder, int position) {
        if (position >= getItemCount()) {
            return;
        }
        Message message = getItem(position);
        Message previous = null;
        if (position > 0) {
            previous = getItem(position - 1);
        }
        Message next = null;
        if (position < getItemCount() - 1) {
            next = getItem(position + 1);
        }

        holder.showMessage(message, previous, next);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    private Message getItem(int position) {
        return messageList.get(position);
    }
}
