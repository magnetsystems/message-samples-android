package com.magnet.demo.mmx.soapbox;

import android.content.Context;

import com.magnet.mmx.client.api.MMX;
import com.magnet.mmx.client.api.MMXChannel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A utility/manager class for the app to manage channel information.
 */
public class ChannelsManager {
  private static final String TAG = ChannelsManager.class.getSimpleName();

  private static ChannelsManager sInstance = null;
  private Context mContext = null;

  public static final MMXChannel CHANNEL_COMPANY_ANNOUNCEMENTS = new MMXChannel.Builder()
          .name("company_announcements")
          .setPublic(true)
          .build();
  public static final MMXChannel CHANNEL_LUNCH_BUDDIES = new MMXChannel.Builder()
          .name("lunch_buddies")
          .setPublic(true)
          .build();

  private List<MMXChannel> mChannels = null;

  //Used to hold the processed channels/subscriptions
  private ArrayList<MMXChannel> mSubscribedChannels = null;
  private ArrayList<MMXChannel> mOtherChannels = null;

  private ChannelsManager(Context context) {
    mContext = context.getApplicationContext();
  }

  /**
   * Retrieve the singleton instance of this ChannelsManager
   *
   * @param context the android context
   * @return the singleton instance
   */
  public static synchronized ChannelsManager getInstance(Context context) {
    if (sInstance == null) {
      sInstance = new ChannelsManager(context);
    }
    return sInstance;
  }

  /**
   * Set the channels
   *
   * @param allChannels all channels
   */
  public void setChannels(List<MMXChannel> allChannels) {
    synchronized (this) {
      mChannels = allChannels;
      clearCachedChannels();
    }
  }

  private void clearCachedChannels() {
    synchronized (this) {
      mSubscribedChannels = null;
      mOtherChannels = null;
    }
  }

  /**
   * Retrieves the list of subscribed channels.  If a searchString paramter is
   * specified, the results will be filtered.
   *
   * @param searchString the partial match string to filter, or null for all subscribed channels
   * @return a list of subscribed channels matching the searchString filter
   */
  public List<MMXChannel> getSubscribedChannels(String searchString) {
    synchronized (this) {
      if (mSubscribedChannels == null) {
        mSubscribedChannels = new ArrayList<MMXChannel>();
        if (mChannels != null) {
          for (MMXChannel channel : mChannels) {
            if (channel.isSubscribed()) {
              mSubscribedChannels.add(channel);
            }
          }
        }
      }
      return filterList(mSubscribedChannels, searchString);
    }
  }

  /**
   * Retrieves the filter list of channels, with the subscribed channels removed.
   * If a searchString parameter is specified, the results will be filtered.
   *
   * @param searchString the partial match string to filter, or null for all non-subscribed channels
   * @return a list of non-subscribed channels matching the searchString filter
   */
  public List<MMXChannel> getOtherChannels(String searchString) {
    synchronized (this) {
      mOtherChannels = new ArrayList<MMXChannel>();
      if (mChannels != null) {
        for (MMXChannel channel : mChannels) {
          if (!channel.isSubscribed()) {
            mOtherChannels.add(channel);
          }
        }
      }
      return filterList(mOtherChannels, searchString);
    }
  }

  private List<MMXChannel> filterList(List<MMXChannel> channels, String filter) {
    if (filter == null || filter.isEmpty()) {
      return Collections.unmodifiableList(channels);
    } else {
      //filter this list down
      ArrayList<MMXChannel> filteredResults = new ArrayList<MMXChannel>();
      for (MMXChannel channel : channels) {
        if (channel.getName().toLowerCase().contains(filter.toLowerCase())) {
          filteredResults.add(channel);
        }
      }
      return Collections.unmodifiableList(filteredResults);
    }
  }

  /**
   * Provisions the pre-defined channels and subscriptions for this app.
   */
  public void provisionChannels() {
    CHANNEL_COMPANY_ANNOUNCEMENTS.create(new MMX.OnFinishedListener<MMXChannel>() {
      public void onSuccess(MMXChannel mmxChannel) {

      }

      public void onFailure(MMX.FailureCode failureCode, Throwable throwable) {

      }
    });

    CHANNEL_LUNCH_BUDDIES.create(new MMX.OnFinishedListener<MMXChannel>() {
      public void onSuccess(MMXChannel mmxChannel) {

      }

      public void onFailure(MMX.FailureCode failureCode, Throwable throwable) {

      }
    });
  }
}
