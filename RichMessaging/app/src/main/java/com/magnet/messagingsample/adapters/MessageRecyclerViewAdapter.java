package com.magnet.messagingsample.adapters;

/**
 * Created by edwardyang on 9/15/15.
 */
import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.magnet.messagingsample.R;
import com.magnet.messagingsample.activities.ImageViewActivity;
import com.magnet.messagingsample.activities.MapViewActivity;
import com.magnet.messagingsample.activities.VideoViewActivity;
import com.magnet.messagingsample.models.MessageImage;
import com.magnet.messagingsample.models.MessageMap;
import com.magnet.messagingsample.models.MessageText;
import com.magnet.messagingsample.models.MessageVideo;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

public class MessageRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TEXT_TYPE = 0;
    private static final int IMAGE_TYPE = 1;
    private static final int MAP_TYPE = 2;
    private static final int VIDEO_TYPE = 3;

    private List<Object> messageItems;
    public Activity mActivity;

    public MessageRecyclerViewAdapter(Activity activity, List<Object> items) {
        this.messageItems = items;
        this.mActivity = activity;
    }

    class ViewHolderText extends RecyclerView.ViewHolder {
        public TextView tvMessageText;
        private LinearLayout wrapper;

        public ViewHolderText(View itemView) {
            super(itemView);
            this.wrapper = (LinearLayout) itemView.findViewById(R.id.wrapper);
            this.tvMessageText = (TextView) itemView.findViewById(R.id.tvMessageText);
        }
    }

    class ViewHolderImage extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView ivMessageImage;
        private LinearLayout wrapper;

        public ViewHolderImage(View itemView) {
            super(itemView);
            this.wrapper = (LinearLayout) itemView.findViewById(R.id.wrapper);
            this.ivMessageImage = (ImageView) itemView.findViewById(R.id.ivMessageImage);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (messageItems.size() > 0) {
                MessageImage item = (MessageImage) messageItems.get(getAdapterPosition());
                goToImageViewActivity(item.imageUrl);
            }
        }
    }

    class ViewHolderMap extends RecyclerView.ViewHolder implements View.OnClickListener {
        private LinearLayout wrapper;
        public ImageView ivMessageLocation;

        public ViewHolderMap(View itemView) {
            super(itemView);
            this.wrapper = (LinearLayout) itemView.findViewById(R.id.wrapper);
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

    class ViewHolderVideo extends RecyclerView.ViewHolder implements View.OnClickListener {
        private LinearLayout wrapper;
        public ImageView ivVideoPlayButton;

        public ViewHolderVideo(View itemView) {
            super(itemView);
            this.wrapper = (LinearLayout) itemView.findViewById(R.id.wrapper);
            this.ivVideoPlayButton = (ImageView) itemView.findViewById(R.id.ivVideoPlayButton);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (messageItems.size() > 0) {
                MessageVideo item = (MessageVideo) messageItems.get(getAdapterPosition());
                goToVideoViewActivity(item.videoUrl);
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

    private void configureViewHolder1(ViewHolderText vh, int position) {
        MessageText item = (MessageText) messageItems.get(position);
        if (item != null) {
            vh.tvMessageText.setText(item.text);
            vh.tvMessageText.setBackgroundResource(item.left ? R.drawable.bubble_yellow : R.drawable.bubble_green);
            vh.wrapper.setGravity(item.left ? Gravity.LEFT : Gravity.RIGHT);
        }
    }

    private void configureViewHolder2(ViewHolderImage vh, int position) {
        MessageImage item = (MessageImage) messageItems.get(position);
        if (item != null) {
            vh.ivMessageImage.setBackgroundResource(item.left ? R.drawable.bubble_yellow : R.drawable.bubble_green);
            vh.wrapper.setGravity(item.left ? Gravity.LEFT : Gravity.RIGHT);
            Picasso.with(mActivity).load(item.imageUrl).into(vh.ivMessageImage);
        }
    }

    private void configureViewHolder3(ViewHolderMap vh, int position) {
        final MessageMap item = (MessageMap) messageItems.get(position);
        if (item != null) {
            vh.ivMessageLocation.setBackgroundResource(item.left ? R.drawable.bubble_yellow : R.drawable.bubble_green);
            vh.wrapper.setGravity(item.left ? Gravity.LEFT : Gravity.RIGHT);
            String loc = "http://maps.google.com/maps/api/staticmap?center="+item.latlng+"&zoom=18&size=700x300&sensor=false&markers=color:blue%7Clabel:S%7C"+item.latlng;
            Picasso.with(mActivity).load(loc).into(vh.ivMessageLocation);
        }
    }

    private void configureViewHolder4(final ViewHolderVideo vh, int position) {
        final MessageVideo item = (MessageVideo) messageItems.get(position);
        if (item != null) {
            vh.ivVideoPlayButton.setBackgroundResource(item.left ? R.drawable.bubble_yellow : R.drawable.bubble_green);
            vh.wrapper.setGravity(item.left ? Gravity.LEFT : Gravity.RIGHT);
        }
    }

    public void goToMapViewActivity(String latlng) {
        Intent intent;
        intent = new Intent(mActivity, MapViewActivity.class);
        intent.putExtra("latlng", latlng);
        mActivity.startActivity(intent);
    }

    public void goToImageViewActivity(String url) {
        Intent intent;
        intent = new Intent(mActivity, ImageViewActivity.class);
        intent.putExtra("imageUrl", url);
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