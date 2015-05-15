package com.magnet.demo.mmx.soapbox;

import android.content.Context;

import com.magnet.mmx.client.MMXClient;
import com.magnet.mmx.client.common.MMXSubscription;
import com.magnet.mmx.client.common.MMXTopicInfo;
import com.magnet.mmx.protocol.MMXTopic;
import com.magnet.mmx.protocol.TopicSummary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class TopicsManager {
  private static final String TAG = TopicsManager.class.getSimpleName();

  private static TopicsManager sInstance = null;
  private Context mContext = null;
  private MMXClient mClient = null;

  private List<MMXSubscription> mSubscriptions = null;
  private List<MMXTopicInfo> mTopics = null;

  //Used to hold the processed topics/subscriptions
  private ArrayList<MMXTopic> mSubscribedTopics = null;
  private ArrayList<MMXTopic> mOtherTopics = null;
  private HashMap<String, TopicSummary> mTopicSummaryMap = null;


  private TopicsManager(Context context) {
    mContext = context.getApplicationContext();
    mClient = MMXClient.getInstance(context, R.raw.soapbox);
  }

  public static synchronized TopicsManager getInstance(Context context) {
    if (sInstance == null) {
      sInstance = new TopicsManager(context);
    }
    return sInstance;
  }

  public void setSubscriptions(List<MMXSubscription> subscriptions) {
    synchronized (this) {
      mSubscriptions = subscriptions;
      clearCachedTopics();
    }
  }

  public void setTopics(List<MMXTopicInfo> topics) {
    synchronized (this) {
      mTopics = topics;
      clearCachedTopics();
    }
  }

  public void setTopicSummaries(List<TopicSummary> topicSummaries) {
    synchronized (this) {
      mTopicSummaryMap = new HashMap<String, TopicSummary>();
      for (TopicSummary summary : topicSummaries) {
        mTopicSummaryMap.put(summary.getTopicNode().getName(), summary);
      }
    }
  }

  private void clearCachedTopics() {
    synchronized (this) {
      mSubscribedTopics = null;
      mOtherTopics = null;
    }
  }

  public TopicSummary getTopicSummary(MMXTopic topic) {
    synchronized (this) {
      if (mTopicSummaryMap != null) {
        return mTopicSummaryMap.get(topic.getName());
      }
      return null;
    }
  }

  public List<MMXTopic> getSubscribedTopics(String searchString) {
    synchronized (this) {
      if (mSubscribedTopics == null) {
        mSubscribedTopics = new ArrayList<MMXTopic>();
        if (mSubscriptions != null) {
          for (MMXSubscription sub : mSubscriptions) {
            mSubscribedTopics.add(sub.getTopic());
          }
        }
      }
      return filterList(mSubscribedTopics, searchString);
    }
  }

  public List<MMXTopic> getOtherTopics(String searchString) {
    synchronized (this) {
      mOtherTopics = new ArrayList<MMXTopic>();
      if (mTopics != null) {
        for (MMXTopicInfo topicInfo : mTopics) {
          if (topicInfo.getTopic().getName() == null || topicInfo.getTopic().getName().isEmpty()) {
            //shouldn't need this, but just being defensive
            continue;
          }
          boolean subscribed = false;
          List<MMXTopic> subscribedTopics = getSubscribedTopics(searchString);
          for (MMXTopic subscribedTopic : subscribedTopics) {
            String name = subscribedTopic.getName();
            if (name != null && name.equalsIgnoreCase(topicInfo.getTopic().getName())) {
              subscribed = true;
              break;
            }
          }
          if (!subscribed) {
            mOtherTopics.add(topicInfo.getTopic());
          }
        }
      }
      return filterList(mOtherTopics, searchString);
    }
  }

  private List<MMXTopic> filterList(List<MMXTopic> topics, String filter) {
    if (filter == null || filter.isEmpty()) {
      return Collections.unmodifiableList(topics);
    } else {
      //filter this list down
      ArrayList<MMXTopic> filteredResults = new ArrayList<MMXTopic>();
      for (MMXTopic topic : topics) {
        if (topic.getName().toLowerCase().contains(filter.toLowerCase())) {
          filteredResults.add(topic);
        }
      }
      return Collections.unmodifiableList(filteredResults);
    }
  }

  public boolean isTopicSubscribed(MMXTopic topic) {
    for (MMXSubscription sub : mSubscriptions) {
      if (sub.getTopic().getName().equalsIgnoreCase(topic.getName())) {
        return true;
      }
    }
    return false;
  }

  public boolean canDelete(MMXTopic topic, MyProfile profile) {
    for (MMXTopicInfo topicInfo : mTopics) {
      if (topic.getName()
              .equalsIgnoreCase(topicInfo.getTopic().getName())) {
        return topicInfo.getCreator().getUserId()
                .equalsIgnoreCase(profile.getUsername());
      }
    }
    return false;
  }
}
