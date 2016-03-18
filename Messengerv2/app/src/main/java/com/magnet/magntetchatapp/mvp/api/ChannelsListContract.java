package com.magnet.magntetchatapp.mvp.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;

import com.magnet.magntetchatapp.R;
import com.magnet.magntetchatapp.mvp.abs.BaseContract;
import com.magnet.magntetchatapp.ui.custom.AdapteredRecyclerView;
import com.magnet.max.android.UserProfile;
import com.magnet.mmx.client.api.ChannelDetail;

import java.util.List;

import butterknife.InjectView;

/**
 * Created by Artli_000 on 18.03.2016.
 */
public interface ChannelsListContract {

    interface View extends BaseContract.BaseView {
        /**
         * Method which provide to add the channels to current list object
         *
         * @param objects current objects
         */
        void addChannels(@NonNull List<ChannelObject> objects);

        /**
         * Method which provide to set the channels to current list object
         *
         * @param objects current objects
         */
        void setChannels(@NonNull List<ChannelObject> objects);

        /**
         * Method which provide to channels clearing
         */
        void clearChannels();

    }

    interface Presenter extends BaseContract.BasePresenter {
        /**
         * Method which provide to start of channel receiving
         */
        void startChannelReceiving();
    }

    //RECYCLER ITEM

    /**
     * Channel view
     */
    class ChannelRecyclerItem extends AdapteredRecyclerView.BaseRecyclerItem<ChannelObject> {

        @InjectView(R.id.labelChannelName)
        AppCompatTextView labelChannelName;

        public ChannelRecyclerItem(Context context) {
            super(context);
        }

        @Override
        public void setUp(ChannelObject baseObject) {
            ChannelDetail channelDetail = baseObject.channelDetail;
            if (channelDetail != null) {
                StringBuilder name = new StringBuilder();
                for (UserProfile profile : channelDetail.getSubscribers()) {
                    String separator = ",";
                    if (name.length() == 0) {
                        separator = "";
                    }
                    name.append(separator);
                    name.append(String.format("%s %s", profile.getFirstName(), profile.getLastName()));
                }
                labelChannelName.setText(name);
            }

        }

        @Override
        protected int getLayoutId() {
            return R.layout.item_view_channel;
        }

        @Override
        protected void onCreateView() {

        }
    }

    /**
     * Channel object
     */
    class ChannelObject extends AdapteredRecyclerView.BaseObject {

        private final ChannelDetail channelDetail;

        public ChannelObject(ChannelDetail channelDetail) {
            this.channelDetail = channelDetail;
        }

        @Override
        public AdapteredRecyclerView.BaseRecyclerItem getRecyclerItem(Context context) {
            return new ChannelRecyclerItem(context);
        }
    }
}
