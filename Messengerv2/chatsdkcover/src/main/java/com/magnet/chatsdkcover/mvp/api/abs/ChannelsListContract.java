package com.magnet.chatsdkcover.mvp.api.abs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.magnet.chatsdkcover.mvp.abs.BaseContract;
import com.magnet.chatsdkcover.mvp.api.layers.ChannelsListContractLayer;
import com.magnet.chatsdkcover.ui.custom.AdapteredRecyclerView;
import com.magnet.mmx.client.api.ChannelDetail;
import com.magnet.mmx.client.api.MMXChannel;

import java.util.List;

/**
 * Created by Artli_000 on 18.03.2016.
 */
public interface ChannelsListContract extends ChannelsListContractLayer {

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

        /**
         * Method which provide the sorting of the channels
         */
        void sortChannels();

        /**
         * Method which provide the setting of the lazy load callback
         *
         * @param lazyLoadCallback lazy load callback
         */
        void setLazyLoadCallback(@NonNull AdapteredRecyclerView.OnLazyLoadCallback lazyLoadCallback);

        /**
         * Method which provide the setting of the loading message
         *
         * @param message    message
         * @param isNeedShow is need show
         */
        void switchLoadingMessage(@Nullable String message, boolean isNeedShow);

        /**
         * Method which provide the adding of the high priority items
         *
         * @param baseObjects list of the high priority items
         */
        void addHighPriorityItem(@NonNull List<AdapteredRecyclerView.BaseObject> baseObjects);

        /**
         * Method which provide the filtering of the channel
         *
         * @param query current query
         */
        void filterChannels(@Nullable String query);

        /**
         * Method which provide the clearing filter
         */
        void clearFilter();

        /**
         * Method which provide the deleting of the channel object
         *
         * @param channelObject channel object
         */
        void deleteChannel(@NonNull final ChannelObject channelObject);

    }

    interface Presenter extends BaseContract.BasePresenter, AdapteredRecyclerView.OnLazyLoadCallback {
        /**
         * Method which provide to start of channel receiving
         */
        void startChannelReceiving(int offset);

        /**
         * Method which provide the getting of the channel details
         *
         * @param channels
         */
        void getChannelsDetails(@Nullable final List<MMXChannel> channels);

        /**
         * Method which provide the hannel post processing
         *
         * @param channelDetails channel details
         */
        void onChannelsPostProcessing(@Nullable final List<ChannelDetail> channelDetails);
    }

}
