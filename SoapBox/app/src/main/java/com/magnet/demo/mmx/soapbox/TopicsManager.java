package com.magnet.demo.mmx.soapbox;

import android.content.Context;
import android.widget.Toast;

import com.magnet.mmx.client.MMXClient;
import com.magnet.mmx.client.MMXPubSubManager;
import com.magnet.mmx.client.MMXTask;
import com.magnet.mmx.client.common.MMXException;
import com.magnet.mmx.client.common.MMXGlobalTopic;
import com.magnet.mmx.client.common.MMXSubscription;
import com.magnet.mmx.client.common.MMXTopicInfo;
import com.magnet.mmx.client.common.TopicExistsException;
import com.magnet.mmx.protocol.MMXTopic;
import com.magnet.mmx.protocol.MMXTopicOptions;
import com.magnet.mmx.protocol.TopicSummary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * A utility/manager class for the app to manage topic information.
 */
public class TopicsManager {
  private static final String TAG = TopicsManager.class.getSimpleName();

  private static TopicsManager sInstance = null;
  private Context mContext = null;
  private MMXClient mClient = null;

  public static final MMXTopic TOPIC_COMPANY_ANNOUNCEMENTS = new MMXGlobalTopic("company_announcements");
  public static final MMXTopic TOPIC_LUNCH_BUDDIES = new MMXGlobalTopic("lunch_buddies");

  private List<MMXSubscription> mSubscriptions = null;
  private List<MMXTopicInfo> mTopics = null;

  //Used to hold the processed topics/subscriptions
  private ArrayList<MMXTopic> mSubscribedTopics = null;
  private ArrayList<MMXTopic> mOtherTopics = null;
  private HashMap<String, TopicSummary> mTopicSummaryMap = null;

  private TopicsManager(Context context) {
    mContext = context.getApplicationContext();
    mClient = MMXClient.getInstance(context, R.raw.soapbox);
    provisionTopics();
  }

  /**
   * Retrieve the singleton instance of this TopicsManager
   *
   * @param context the android context
   * @return the singleton instance
   */
  public static synchronized TopicsManager getInstance(Context context) {
    if (sInstance == null) {
      sInstance = new TopicsManager(context);
    }
    return sInstance;
  }

  /**
   * Set the subscriptions data into this manager.
   * @param subscriptions the list of subscriptions retrieved from MMX
   */
  public void setSubscriptions(List<MMXSubscription> subscriptions) {
    synchronized (this) {
      mSubscriptions = subscriptions;
      clearCachedTopics();
    }
  }

  /**
   * Set the topics data into this manager.
   * @param topics the list of all topics retrieved from MMX
   */
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

  /**
   * Retrieves the summary for the specified topic.
   *
   * @param topic the topic to retrieve the summary
   * @return the topic summary or null of not found
   */
  public TopicSummary getTopicSummary(MMXTopic topic) {
    synchronized (this) {
      if (mTopicSummaryMap != null) {
        return mTopicSummaryMap.get(topic.getName());
      }
      return null;
    }
  }

  /**
   * Retrieves the list of subscribed topics.  If a searchString paramter is
   * specified, the results will be filtered.
   *
   * @param searchString the partial match string to filter, or null for all subscribed topics
   * @return a list of subscribed topics matching the searchString filter
   */
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

  /**
   * Retrieves the filter list of topics, with the subscribed topics removed.
   * If a searchString parameter is specified, the results will be filtered.
   *
   * @param searchString the partial match string to filter, or null for all non-subscribed topics
   * @return a list of non-subscribed topics matching the searchString filter
   */
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

  /**
   * Determines if the specified topic is currently subscribed.
   *
   * @param topic the topic to check
   * @return true if the topic is currently subscribed
   */
  public boolean isTopicSubscribed(MMXTopic topic) {
    for (MMXSubscription sub : mSubscriptions) {
      if (sub.getTopic().getName().equalsIgnoreCase(topic.getName())) {
        return true;
      }
    }
    return false;
  }

  /**
   * Verifies that the current user can delete the specified topic.
   *
   * @param topic the topic to verify
   * @param profile the current user's profile
   * @return true if the current user can delete the specified topic
   */
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

  /**
   * Provisions the pre-defined topics and subscriptions for this app.
   */
  private void provisionTopics() {
    MMXTask<Void> provisionTask = new MMXTask<Void>(mClient) {
      @Override
      public Void doRun(MMXClient mmxClient) throws Throwable {
        MMXPubSubManager psm = mmxClient.getPubSubManager();
        try {
          psm.createTopic(TOPIC_COMPANY_ANNOUNCEMENTS, new MMXTopicOptions());
        } catch (TopicExistsException tex) {
          //For our purposes, this is ok.
        } catch (MMXException e) {
          Toast.makeText(mContext, "Unable to create topic: " + TOPIC_COMPANY_ANNOUNCEMENTS.getName() +
                  ".  "  + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        try {
          String subscriptionId = psm.subscribe(TOPIC_COMPANY_ANNOUNCEMENTS, false);
        } catch (MMXException e) {
          Toast.makeText(mContext, "Unable to subscribe to: " + TOPIC_COMPANY_ANNOUNCEMENTS.getName() +
                  ".  "  + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        try {
          psm.createTopic(TOPIC_LUNCH_BUDDIES, new MMXTopicOptions());
        } catch (TopicExistsException tex) {
          //For our purposes, this is ok.
        } catch (MMXException e) {
          Toast.makeText(mContext, "Unable to create topic: " + TOPIC_LUNCH_BUDDIES.getName() +
                  ".  "  + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return null;
      }
    };
    provisionTask.execute();
  }
}
