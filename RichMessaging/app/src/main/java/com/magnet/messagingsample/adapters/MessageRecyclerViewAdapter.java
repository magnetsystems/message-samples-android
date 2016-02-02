package com.magnet.messagingsample.adapters;

/**
 * Created by edwardyang on 9/15/15.
 */

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.Profile;
import com.magnet.max.android.Attachment;
import com.magnet.messagingsample.R;
import com.magnet.messagingsample.activities.ImageViewActivity;
import com.magnet.messagingsample.activities.MapViewActivity;
import com.magnet.messagingsample.activities.VideoViewActivity;
import com.magnet.messagingsample.helpers.CircleImageView;
import com.magnet.messagingsample.helpers.TextHelper;
import com.magnet.messagingsample.models.MessageImage;
import com.magnet.messagingsample.models.MessageMap;
import com.magnet.messagingsample.models.MessageText;
import com.magnet.messagingsample.models.MessageVideo;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

public class MessageRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    final String TAG = "MessageAdapter";

    private static final int TEXT_TYPE = 0;
    private static final int IMAGE_TYPE = 1;
    private static final int MAP_TYPE = 2;
    private static final int VIDEO_TYPE = 3;

    private List<Object> messageItems;
    public Activity mActivity;
    private File defaultFolder;

    public MessageRecyclerViewAdapter(Activity activity, List<Object> items) {
        this.messageItems = items;
        this.mActivity = activity;
        defaultFolder = activity.getCacheDir();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        protected RelativeLayout avatar;
        protected LinearLayout wrapper;
        protected TextView tvChatUsername;
        protected TextView tvUserInitials;
        protected CircleImageView ivAvatarImage;

        public ViewHolder(View itemView) {
            super(itemView);
            this.wrapper = (LinearLayout) itemView.findViewById(R.id.wrapper);
            this.avatar = (RelativeLayout) itemView.findViewById(R.id.avatar);
            this.tvChatUsername = (TextView) itemView.findViewById(R.id.tvChatUsername);
            this.tvUserInitials = (TextView) itemView.findViewById(R.id.tvUserInitials);
            this.ivAvatarImage = (CircleImageView) itemView.findViewById(R.id.ivAvatarImage);
        }
    }

    class ViewHolderText extends ViewHolder {
        public TextView tvMessageText;

        public ViewHolderText(View itemView) {
            super(itemView);
            this.tvMessageText = (TextView) itemView.findViewById(R.id.tvMessageText);
        }
    }

    class ViewHolderImage extends ViewHolder implements View.OnClickListener {
        public ImageView ivMessageImage;

        public ViewHolderImage(View itemView) {
            super(itemView);
            this.ivMessageImage = (ImageView) itemView.findViewById(R.id.ivMessageImage);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (messageItems.size() > 0) {
                MessageImage item = (MessageImage) messageItems.get(getAdapterPosition());
                File file = new File(defaultFolder, item.imageAttachment.getAttachmentId());
                if (!file.exists()) {
                    item.imageAttachment.download(file, new Attachment.DownloadAsFileListener() {
                        @Override
                        public void onComplete(File file) {
                            goToImageViewActivity(file.getAbsolutePath());
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            Log.e(TAG, "downloadImageError", throwable);
                        }
                    });
                } else {
                    goToImageViewActivity(file.getAbsolutePath());
                }
            }
        }
    }

    class ViewHolderMap extends ViewHolder implements View.OnClickListener {
        public ImageView ivMessageLocation;

        public ViewHolderMap(View itemView) {
            super(itemView);
            this.ivMessageLocation = (ImageView) itemView.findViewById(R.id.ivMessageLocation);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (messageItems.size() > 0) {
                MessageMap item = (MessageMap) messageItems.get(getAdapterPosition());
                goToMapViewActivity(item.latlng);
            }
        }
    }

    class ViewHolderVideo extends ViewHolder implements View.OnClickListener {
        public ImageView ivVideoPlayButton;

        public ViewHolderVideo(View itemView) {
            super(itemView);
            this.ivVideoPlayButton = (ImageView) itemView.findViewById(R.id.ivVideoPlayButton);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (messageItems.size() > 0) {
                MessageVideo item = (MessageVideo) messageItems.get(getAdapterPosition());
                goToVideoViewActivity(item.videoAttachment.getDownloadUrl());
            }
        }
    }

    @Override
    public int getItemCount() {
        return this.messageItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (messageItems.get(position) instanceof MessageText) {
            return TEXT_TYPE;
        } else if (messageItems.get(position) instanceof MessageImage) {
            return IMAGE_TYPE;
        } else if (messageItems.get(position) instanceof MessageMap) {
            return MAP_TYPE;
        } else if (messageItems.get(position) instanceof MessageVideo) {
            return VIDEO_TYPE;
        }
        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case IMAGE_TYPE:
                view = inflater.inflate(R.layout.item_chat_image, parent, false);
                viewHolder = new ViewHolderImage(view);
                break;
            case MAP_TYPE:
                view = inflater.inflate(R.layout.item_chat_map, parent, false);
                viewHolder = new ViewHolderMap(view);
                break;
            case VIDEO_TYPE:
                view = inflater.inflate(R.layout.item_chat_video, parent, false);
                viewHolder = new ViewHolderVideo(view);
                break;
            default:
                view = inflater.inflate(R.layout.item_chat_text, parent, false);
                viewHolder = new ViewHolderText(view);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (position >= messageItems.size()) {
            return;
        }
        switch (viewHolder.getItemViewType()) {
            case TEXT_TYPE:
                configureViewHolder1((ViewHolderText) viewHolder, position);
                break;
            case IMAGE_TYPE:
                configureViewHolder2((ViewHolderImage) viewHolder, position);
                break;
            case MAP_TYPE:
                configureViewHolder3((ViewHolderMap) viewHolder, position);
                break;
            case VIDEO_TYPE:
                configureViewHolder4((ViewHolderVideo) viewHolder, position);
                break;
        }
    }

    private void placeLayouts(RelativeLayout imageLayout, LinearLayout messageLayout, boolean left) {
        RelativeLayout.LayoutParams imageParams = (RelativeLayout.LayoutParams) imageLayout.getLayoutParams();
        RelativeLayout.LayoutParams messageParams = (RelativeLayout.LayoutParams) messageLayout.getLayoutParams();
        if (left) {
            imageParams.removeRule(RelativeLayout.ALIGN_BOTTOM);
            imageParams.removeRule(RelativeLayout.ALIGN_PARENT_END);
            imageParams.addRule(RelativeLayout.ALIGN_PARENT_START);
            messageParams.removeRule(RelativeLayout.START_OF);
            messageParams.addRule(RelativeLayout.END_OF, imageLayout.getId());
            messageParams.leftMargin = 0;
            messageParams.rightMargin = 10;
        } else {
            imageParams.removeRule(RelativeLayout.ALIGN_PARENT_START);
            imageParams.addRule(RelativeLayout.ALIGN_PARENT_END);
            imageParams.addRule(RelativeLayout.ALIGN_BOTTOM, messageLayout.getId());
            messageParams.removeRule(RelativeLayout.END_OF);
            messageParams.addRule(RelativeLayout.START_OF, imageLayout.getId());
            messageParams.leftMargin = 10;
            messageParams.rightMargin = 0;
        }
        imageLayout.setLayoutParams(imageParams);
        messageLayout.setLayoutParams(messageParams);
    }

    private void configureBasicViewHolder(ViewHolder vh, String username, boolean left) {
        vh.wrapper.setGravity(left ? Gravity.LEFT : Gravity.RIGHT);
        vh.tvChatUsername.setText(username);
        vh.tvChatUsername.setVisibility(left ? View.VISIBLE : View.INVISIBLE);
        if (!left) {
            Profile profile = Profile.getCurrentProfile();
            if (profile != null) {
                Uri photoUri = profile.getProfilePictureUri(100, 100);
                if (photoUri != null) {
                    vh.tvUserInitials.setText("");
                    Picasso.with(mActivity).load(photoUri).into(vh.ivAvatarImage);
                }
            } else {
                vh.tvUserInitials.setText(TextHelper.getInitials(username));
            }
        } else {
            Picasso.with(mActivity).load(R.drawable.sqr_img).into(vh.ivAvatarImage);
            vh.tvUserInitials.setText(TextHelper.getInitials(username));
        }
        placeLayouts(vh.avatar, vh.wrapper, left);
    }

    private void configureViewHolder1(ViewHolderText vh, int position) {
        MessageText item = (MessageText) messageItems.get(position);
        if (item != null) {
            vh.tvMessageText.setText(item.text);
            vh.tvMessageText.setBackgroundResource(item.left ? R.drawable.bubble_yellow : R.drawable.bubble_green);
            configureBasicViewHolder(vh, item.username, item.left);
        }
    }

    private void configureViewHolder2(final ViewHolderImage vh, int position) {
        MessageImage item = (MessageImage) messageItems.get(position);
        if (item != null) {
            vh.ivMessageImage.setBackgroundResource(item.left ? R.drawable.bubble_yellow : R.drawable.bubble_green);
            configureBasicViewHolder(vh, item.username, item.left);
            vh.ivMessageImage.setImageDrawable(null);
            File file = new File(defaultFolder, item.imageAttachment.getAttachmentId());
            if (!file.exists()) {
                item.imageAttachment.download(file, new Attachment.DownloadAsFileListener() {
                    @Override
                    public void onComplete(File file) {
                        Log.d(TAG, "downloadImage : success " + file.getName());
                        Picasso.with(mActivity).load(file).into(vh.ivMessageImage);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e(TAG, "downloadImage : Error", throwable);
                    }
                });
            } else {
                Log.d(TAG, "downloadImage : image exists " + file.getName());
                Picasso.with(mActivity).load(file).into(vh.ivMessageImage);
            }
        }
    }

    private void configureViewHolder3(ViewHolderMap vh, int position) {
        final MessageMap item = (MessageMap) messageItems.get(position);
        if (item != null) {
            vh.ivMessageLocation.setBackgroundResource(item.left ? R.drawable.bubble_yellow : R.drawable.bubble_green);
            configureBasicViewHolder(vh, item.username, item.left);
            String loc = "http://maps.google.com/maps/api/staticmap?center="+item.latlng+"&zoom=18&size=700x300&sensor=false&markers=color:blue%7Clabel:S%7C"+item.latlng;
            Picasso.with(mActivity).load(loc).into(vh.ivMessageLocation);
        }
    }

    private void configureViewHolder4(final ViewHolderVideo vh, int position) {
        final MessageVideo item = (MessageVideo) messageItems.get(position);
        if (item != null) {
            vh.ivVideoPlayButton.setBackgroundResource(item.left ? R.drawable.bubble_yellow : R.drawable.bubble_green);
            configureBasicViewHolder(vh, item.username, item.left);
        }
    }

    public void goToMapViewActivity(String latlng) {
        Intent intent;
        intent = new Intent(mActivity, MapViewActivity.class);
        intent.putExtra("latlng", latlng);
        mActivity.startActivity(intent);
    }

    public void goToImageViewActivity(String path) {
        Intent intent;
        intent = new Intent(mActivity, ImageViewActivity.class);
        intent.putExtra("imagePath", path);
        mActivity.startActivity(intent);
    }

    public void goToVideoViewActivity(String url) {
        Intent intent;
        intent = new Intent(mActivity, VideoViewActivity.class);
        intent.putExtra("videoUrl", url);
        mActivity.startActivity(intent);
    }

    public void add(Object obj) {
        messageItems.add(obj);
    }

}