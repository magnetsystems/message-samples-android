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

        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case TEXT_TYPE:
                View v1 = inflater.inflate(R.layout.item_chat_text, parent, false);
                viewHolder = new ViewHolderText(v1);
                break;
            case IMAGE_TYPE:
                View v2 = inflater.inflate(R.layout.item_chat_image, parent, false);
                viewHolder = new ViewHolderImage(v2);
                break;
            case MAP_TYPE:
                View v3 = inflater.inflate(R.layout.item_chat_map, parent, false);
                viewHolder = new ViewHolderMap(v3);
                break;
            case VIDEO_TYPE:
                View v4 = inflater.inflate(R.layout.item_chat_video, parent, false);
                viewHolder = new ViewHolderVideo(v4);
                break;
            default:
                // TODO: handle this later
                View v5 = inflater.inflate(R.layout.item_chat_text, parent, false);
                viewHolder = new ViewHolderText(v5);
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
                ViewHolderText vh1 = (ViewHolderText) viewHolder;
                configureViewHolder1(vh1, position);
                break;
            case IMAGE_TYPE:
                ViewHolderImage vh2 = (ViewHolderImage) viewHolder;
                configureViewHolder2(vh2, position);
                break;
            case MAP_TYPE:
                ViewHolderMap vh3 = (ViewHolderMap) viewHolder;
                configureViewHolder3(vh3, position);
                break;
            case VIDEO_TYPE:
                ViewHolderVideo vh4 = (ViewHolderVideo) viewHolder;
                configureViewHolder4(vh4, position);
                break;
        }
    }

    private void configureViewHolder1(ViewHolderText vh1, int position) {
        MessageText item = (MessageText) messageItems.get(position);
        if (item != null) {
            vh1.tvMessageText.setText(item.text);
            vh1.tvMessageText.setBackgroundResource(item.left ? R.drawable.bubble_yellow : R.drawable.bubble_green);
            vh1.wrapper.setGravity(item.left ? Gravity.LEFT : Gravity.RIGHT);

        }
    }

    private void configureViewHolder2(ViewHolderImage vh2, int position) {
        MessageImage item = (MessageImage) messageItems.get(position);
        if (item != null) {
            vh2.ivMessageImage.setBackgroundResource(item.left ? R.drawable.bubble_yellow : R.drawable.bubble_green);
            vh2.wrapper.setGravity(item.left ? Gravity.LEFT : Gravity.RIGHT);
//            File f = new File(item.imageUrl);
            Picasso.with(mActivity).load(item.imageUrl).into(vh2.ivMessageImage);
        }
    }

    private void configureViewHolder3(ViewHolderMap vh3, int position) {
        final MessageMap item = (MessageMap) messageItems.get(position);
        if (item != null) {
            vh3.ivMessageLocation.setBackgroundResource(item.left ? R.drawable.bubble_yellow : R.drawable.bubble_green);
            vh3.wrapper.setGravity(item.left ? Gravity.LEFT : Gravity.RIGHT);
            String loc = "http://maps.google.com/maps/api/staticmap?center="+item.latlng+"&zoom=18&size=700x300&sensor=false&markers=color:blue%7Clabel:S%7C"+item.latlng;
            Picasso.with(mActivity).load(loc).into(vh3.ivMessageLocation);
        }
    }

    private void configureViewHolder4(final ViewHolderVideo vh4, int position) {
        final MessageVideo item = (MessageVideo) messageItems.get(position);
        if (item != null) {
            vh4.ivVideoPlayButton.setBackgroundResource(item.left ? R.drawable.bubble_yellow : R.drawable.bubble_green);
            vh4.wrapper.setGravity(item.left ? Gravity.LEFT : Gravity.RIGHT);
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