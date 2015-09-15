package com.magnet.messagingsample.adapters;

/**
 * Created by edwardyang on 9/15/15.
 */
import android.app.Activity;
import android.app.Fragment;
import android.location.Location;
import android.net.Uri;
import android.os.SystemClock;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.magnet.messagingsample.R;
import com.magnet.messagingsample.activities.ChatActivity;
import com.magnet.messagingsample.models.MessageImage;
import com.magnet.messagingsample.models.MessageMap;
import com.magnet.messagingsample.models.MessageText;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

public class MessageRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TEXT_TYPE = 0;
    private static final int IMAGE_TYPE = 1;
    private static final int MAP_TYPE = 2;

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

    class ViewHolderImage extends RecyclerView.ViewHolder {
        public ImageView ivMessageImage;
        private LinearLayout wrapper;

        public ViewHolderImage(View itemView) {
            super(itemView);
            this.wrapper = (LinearLayout) itemView.findViewById(R.id.wrapper);
            this.ivMessageImage = (ImageView) itemView.findViewById(R.id.ivMessageImage);
        }
    }

    class ViewHolderMap extends RecyclerView.ViewHolder {
        private LinearLayout wrapper;
        public MapFragment mapMessageLocation;
        public RelativeLayout rlMapContainer;

        public ViewHolderMap(View itemView) {
            super(itemView);
            this.wrapper = (LinearLayout) itemView.findViewById(R.id.wrapper);
            this.rlMapContainer = (RelativeLayout) itemView.findViewById(R.id.rlMapContainer);
            if (mapMessageLocation == null) {
                mapMessageLocation = ((MapFragment) mActivity.getFragmentManager().findFragmentById(R.id.mapMessageLocation));
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
            default:
                // TODO: handle this later
                View v4 = inflater.inflate(R.layout.item_chat_text, parent, false);
                viewHolder = new ViewHolderText(v4);
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
            File f = new File(item.imageUri);
            Picasso.with(mActivity).load(f).into(vh2.ivMessageImage);
        }
    }

    private void configureViewHolder3(ViewHolderMap vh3, int position) {
        final MessageMap item = (MessageMap) messageItems.get(position);
        if (item != null) {
            vh3.wrapper.setGravity(item.left ? Gravity.LEFT : Gravity.RIGHT);
            if (vh3.mapMessageLocation != null) {
                vh3.rlMapContainer.setBackgroundResource(item.left ? R.drawable.bubble_yellow : R.drawable.bubble_green);
                vh3.mapMessageLocation.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap map) {
                        loadMap(map, item.latlng);
                    }
                });
            }
        }
    }

    protected void loadMap(GoogleMap googleMap, String latlng) {
        if (googleMap != null) {
            googleMap.setMyLocationEnabled(true);

            String[] latlngAry = latlng.split(",");
            double lat = Double.parseDouble(latlngAry[0]);
            double lng = Double.parseDouble(latlngAry[1]);
            LatLng latlong = new LatLng(lat, lng);

            BitmapDescriptor defaultMarker =
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);

            Marker marker = googleMap.addMarker(new MarkerOptions()
                    .position(latlong)
                    .title("My Location")
                    .icon(defaultMarker));

            marker.showInfoWindow();
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlong, 13));
        }
    }

    public void add(Object obj) {
        messageItems.add(obj);
    }

}