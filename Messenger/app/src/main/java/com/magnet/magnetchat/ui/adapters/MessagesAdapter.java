package com.magnet.magnetchat.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.magnet.magnetchat.R;
import com.magnet.magnetchat.helpers.DateHelper;
import com.magnet.magnetchat.helpers.UserHelper;
import com.magnet.magnetchat.model.Message;
import com.magnet.magnetchat.ui.views.CircleNameView;
import com.magnet.magnetchat.util.AppLogger;
import com.magnet.magnetchat.util.Utils;
import com.magnet.max.android.Attachment;
import com.magnet.max.android.User;
import com.magnet.max.android.util.StringUtil;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {
    private final static String TAG = MessagesAdapter.class.getSimpleName();

    private LayoutInflater inflater;
    private List<Message> messageList;
    private Context context;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        LinearLayout messageArea;
        AppCompatTextView date;
        AppCompatTextView sender;
        AppCompatTextView text;
        AppCompatTextView delivered;
        ImageView image;
        Message message;
        ImageView imageMyAvatar;
        ImageView imageOtherAvatar;
        CircleNameView viewMyAvatar;
        CircleNameView viewOtherAvatar;

        public ViewHolder(View itemView) {
            super(itemView);
            this.messageArea = (LinearLayout) itemView.findViewById(R.id.itemMessageArea);
            this.date = (AppCompatTextView) itemView.findViewById(R.id.itemMessageDate);
            this.sender = (AppCompatTextView) itemView.findViewById(R.id.itemMessageSender);
            this.image = (ImageView) itemView.findViewById(R.id.itemMessageImage);
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
            this.image.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            try {
                onOpenAttachment();
            } catch (Exception e) {
                AppLogger.error(this, e.toString());
            }
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
                }
            }
        }

    }

    public MessagesAdapter(Context context, List<Message> messages) {
        inflater = LayoutInflater.from(context);
        this.messageList = messages;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position >= getItemCount()) {
            return;
        }
        Message message = getItem(position);
        Message previous = null;
        if (position > 0) {
            previous = getItem(position - 1);
        }
        holder.message = message;
        configureDate(holder, message, previous, position);
        if (message.getSender() == null || StringUtil.isStringValueEqual(User.getCurrentUserId(), message.getSender().getUserIdentifier())) {
            makeMessageFromMe(holder, message);
        } else {
            makeMessageToMe(holder, message, previous);
        }
        if (message.getType() != null) {
            switch (message.getType()) {
                case Message.TYPE_MAP:
                    configureMapMsg(holder, message);
                    break;
                case Message.TYPE_VIDEO:
                    configureVideoMsg(holder, message);
                    break;
                case Message.TYPE_PHOTO:
                    configureImageMsg(holder, message);
                    break;
                case Message.TYPE_TEXT:
                    configureTextMsg(holder, message);
                    break;
            }
        } else {
            configureTextMsg(holder, message);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    private Message getItem(int position) {
        return messageList.get(position);
    }

    private void configureDate(ViewHolder viewHolder, Message message, Message previous, int position) {
        Date date;
        Date previousDate = null;
        if (message.getCreateTime() == null) {
            date = new Date();
        } else {
            date = DateHelper.utcToLocal(message.getCreateTime());
        }
        if (previous != null) {
            previousDate = DateHelper.utcToLocal(previous.getCreateTime());
        }
        String msgDate = DateHelper.getMessageDateTime(date);
        String previousMsgDate = null;
        if (previousDate != null) {
            previousMsgDate = DateHelper.getMessageDateTime(previousDate);
        }
        if (!msgDate.equalsIgnoreCase(previousMsgDate)) {
            viewHolder.date.setVisibility(View.VISIBLE);
            viewHolder.date.setText(msgDate);
        } else {
            viewHolder.date.setVisibility(View.GONE);
        }
    }

    private void makeMessageToMe(ViewHolder viewHolder, Message message, Message previous) {
        viewHolder.imageMyAvatar.setVisibility(View.GONE);
        viewHolder.viewMyAvatar.setVisibility(View.GONE);
        viewHolder.imageOtherAvatar.setVisibility(View.VISIBLE);
        viewHolder.imageOtherAvatar.setBackgroundResource(android.R.color.transparent);
        viewHolder.imageOtherAvatar.setImageResource(android.R.color.transparent);
        viewHolder.viewOtherAvatar.setVisibility(View.VISIBLE);

        viewHolder.messageArea.setGravity(Gravity.LEFT | Gravity.START);
        viewHolder.text.setBackgroundResource(R.drawable.bubble_odd);
        viewHolder.text.setTextColor(Color.BLACK);
        viewHolder.delivered.setVisibility(View.GONE);
        if (message.getSender() != null) {
            String userName = UserHelper.getDisplayName(message.getSender());
            String previousUser = "";
            if (previous != null) {
                previousUser = UserHelper.getDisplayName(previous.getSender());
            }
            viewHolder.viewOtherAvatar.setUserName(userName);
            viewHolder.sender.setText(userName);
            if (userName.equalsIgnoreCase(previousUser)) {
                viewHolder.sender.setVisibility(View.GONE);
            } else {
                viewHolder.sender.setVisibility(View.VISIBLE);
            }
            if (null != message.getSender().getAvatarUrl()) {
                Glide.with(context)
                        .load(message.getSender().getAvatarUrl())
                        .fitCenter()
                        .into(viewHolder.imageOtherAvatar);
            }
        }
    }

    private void makeMessageFromMe(ViewHolder viewHolder, Message message) {

        User user = User.getCurrentUser();

        viewHolder.imageMyAvatar.setVisibility(View.VISIBLE);
        viewHolder.imageMyAvatar.setBackgroundResource(android.R.color.transparent);
        viewHolder.imageMyAvatar.setImageResource(android.R.color.transparent);
        viewHolder.viewMyAvatar.setVisibility(View.VISIBLE);
        viewHolder.imageOtherAvatar.setVisibility(View.GONE);
        viewHolder.viewOtherAvatar.setVisibility(View.GONE);

        viewHolder.messageArea.setGravity(Gravity.RIGHT | Gravity.END);
        viewHolder.text.setBackgroundResource(R.drawable.bubble);
        viewHolder.text.setTextColor(Color.WHITE);
        viewHolder.sender.setVisibility(View.GONE);
        if (message.isDelivered()) {
            viewHolder.delivered.setVisibility(View.VISIBLE);
        } else {
            viewHolder.delivered.setVisibility(View.GONE);
        }

        if ((user != null)) {
            String userName = UserHelper.getDisplayName(user);
            viewHolder.viewMyAvatar.setUserName(userName);
            if ((null != user.getAvatarUrl())) {
                Glide.with(context)
                        .load(user.getAvatarUrl())
                        .fitCenter()
                        .into(viewHolder.imageMyAvatar);
            }
        }
    }

    private void configureMediaMsg(ViewHolder holder) {
        holder.text.setVisibility(View.GONE);
        holder.image.setVisibility(View.VISIBLE);
    }

    private void configureMapMsg(ViewHolder holder, Message message) {
        configureMediaMsg(holder);
        String loc = "http://maps.google.com/maps/api/staticmap?center=" + message.getLatitudeLongitude() + "&zoom=18&size=700x300&sensor=false&markers=color:blue%7Clabel:S%7C" + message.getLatitudeLongitude();
        Glide.with(context).load(loc).placeholder(R.drawable.map_msg).centerCrop().into(holder.image);
    }

    private void configureVideoMsg(ViewHolder holder, Message message) {
        configureMediaMsg(holder);
        holder.image.setImageResource(R.drawable.video_msg);
        holder.image.setContentDescription("Click to watch the video");
    }

    private void configureImageMsg(final ViewHolder holder, Message message) {
        configureMediaMsg(holder);
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
                        .into(holder.image);
            } else {
                Glide.with(context)
                        .load(R.drawable.photo_msg)
                        .centerCrop()
                        .into(holder.image);
            }
        }
    }

    private void configureTextMsg(ViewHolder holder, Message message) {
        holder.text.setText(message.getText());
        holder.text.setVisibility(View.VISIBLE);
        holder.image.setVisibility(View.GONE);
    }

}
