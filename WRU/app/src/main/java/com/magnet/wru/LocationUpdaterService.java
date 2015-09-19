package com.magnet.wru;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Intent;
import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.magnet.mmx.client.api.MMX;
import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.mmx.client.api.MMXMessage;
import com.magnet.mmx.protocol.GeoLoc;

import java.util.Date;
import java.util.Map;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * helper methods.
 */
public class LocationUpdaterService extends IntentService {
  private static final String TAG = LocationUpdaterService.class.getSimpleName();
  public static final String ACTION_UPDATE_LOCATION = "com.magnet.wru.action.UPDATE_LOCATION";
  public static final String ACTION_LOCATION_UPDATED = "com.magnet.wru.action.LOCATION_UPDATED";

  public static final String EXTRA_USERNAME = "com.magnet.wru.extra.USERNAME";
  public static final String EXTRA_TOPIC_NAME = "com.magnet.wru.extra.TOPIC_NAME";

  /**
   * Starts the location updates for the specified topic/username on the given interval. If
   * the service is already performing a task this action will be queued.
   *
   * @param context the context
   * @param username the username
   * @param topic the topic
   * @param updateInterval if >= 0 will perform a location update and schedule future updates on the specified interval
   */
  public static void startLocationUpdates(Context context, String username, MMXChannel topic, long updateInterval) {
    if (updateInterval >= 0) {
      AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
      am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), updateInterval,
              getInvokeUpdatePendingIntent(context, username, topic));
    } else {
      updateLocation(context, username, topic);
    }
    registerForLocationUpdates(context);
  }

  public static void updateLocation(Context context, String username, MMXChannel topic) {
    Intent intent = getInvokeUpdateIntent(context, username, topic);
    context.sendBroadcast(intent);
  }

  private static Intent getInvokeUpdateIntent(Context context, String username, MMXChannel topic) {
    Intent intent = new Intent(context, LocationUpdaterService.LocationBroadcastReceiver.class);
    intent.setAction(ACTION_UPDATE_LOCATION);
    intent.putExtra(EXTRA_USERNAME, username);
    intent.putExtra(EXTRA_TOPIC_NAME, topic.getName());
    return intent;
  }

  private static PendingIntent getInvokeUpdatePendingIntent(Context context, String username, MMXChannel topic) {
    return PendingIntent.getBroadcast(context, 0, getInvokeUpdateIntent(context, username, topic), PendingIntent.FLAG_UPDATE_CURRENT);
  }

  private static Intent getLocationUpdatedIntent(Context context) {
    Intent intent = new Intent(context, LocationUpdaterService.LocationBroadcastReceiver.class);
    intent.setAction(ACTION_LOCATION_UPDATED);
    return intent;
  }

  private static PendingIntent getLocationUpdatedPendingIntent(Context context) {
    return PendingIntent.getBroadcast(context, 0, getLocationUpdatedIntent(context), PendingIntent.FLAG_UPDATE_CURRENT);
  }

  /**
   * Stops the location updates. If
   * the service is already performing a task this action will be queued.
   *
   * @see IntentService
   */
  public static void stopLocationUpdates(Context context, String username, MMXChannel topic) {
    Log.d(TAG, "stopLocationUpdates(): Stopping location updates");
    AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    am.cancel(getInvokeUpdatePendingIntent(context, username, topic));
    unregisterForLocationUpdates(context);
  }

  public LocationUpdaterService() {
    super("LocationUpdaterService");
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    Log.d(TAG, "onHandleIntent(): start.  Intent=" + intent);
    EventLog.getInstance(this).add(EventLog.Type.INFO, "LocationUpdaterService handling intent: " + intent);
    if (intent != null) {
      final String action = intent.getAction();
      if (ACTION_UPDATE_LOCATION.equals(action)) {
        final String username = intent.getStringExtra(EXTRA_USERNAME);
        final String topicName = intent.getStringExtra(EXTRA_TOPIC_NAME);
        MMXChannel joinedTopic = WRU.getInstance(this).getJoinedTopic();
        if (joinedTopic != null && joinedTopic.getName().equalsIgnoreCase(topicName)) {
          handleLocationUpdate(username, joinedTopic);
        }
      } else if (ACTION_LOCATION_UPDATED.equals(action)) {
        //this is the intent that is fired when the location has been updated from GooglePlayServices
        //need to do a publish
        LocationResult result = LocationResult.extractResult(intent);
        if (result != null) {
          Location lastLocation = result.getLastLocation();
          WRU wru = WRU.getInstance(this);
          publishLocation(lastLocation, wru.getUsername(), wru.getJoinedTopic());
        }
      } else if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
        WRU wru = WRU.getInstance(this);
        if (wru.getJoinedTopic() != null) {
          registerForLocationUpdates(this);
          handleLocationUpdate(wru.getUsername(), wru.getJoinedTopic());
        }
      }
    }
  }

  private void publishLocation(final Location location, final String username, final MMXChannel topic) {
    WRU wru = WRU.getInstance(this);
    final Date locationDate = new Date(location.getTime());
    EventLog.getInstance(this).add(EventLog.Type.INFO, "Location: " + location);
    Log.d(TAG, "publishLocation(): publishing...");
    final GeoLoc geo = new GeoLoc();
    geo.setAccuracy((int) location.getAccuracy());
    geo.setLat((float) location.getLatitude());
    geo.setLng((float) location.getLongitude());
    final Map<String, String> payload = WRU.buildPayload(username, geo, locationDate);
    if (MMX.getCurrentUser() == null) {
      Log.e(TAG, "publishLocation(): Not publishing because not connected.");
      EventLog.getInstance(LocationUpdaterService.this).add(EventLog.Type.ERROR, "Publish failed.  Not connected.");
    } else {
      publish(topic, payload);
    }
  }

  private void publish(MMXChannel topic, Map<String, String> payload) {
    topic.publish(payload, new MMXMessage.OnFinishedListener<String>() {
      public void onSuccess(String s) {
        Log.d(TAG, "publish(): successfully published location");
      }

      public void onFailure(MMXMessage.FailureCode failureCode, Throwable throwable) {
        Log.e(TAG, "publish(): Unable to publish location: " + failureCode, throwable);
        EventLog.getInstance(LocationUpdaterService.this).add(EventLog.Type.ERROR, "Publish failed: " + failureCode);
      }
    });
  }

  /**
   * Handle start location updates in the provided background thread with the provided
   * parameters.
   */
  private void handleLocationUpdate(final String username, final MMXChannel topic) {
    Log.d(TAG, "handleLocationUpdate(): start");
    GoogleApiClient googleApiClient = WRU.getInstance(this).waitForGoogleApi();
    Location currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
    if (currentLocation == null) {
      Log.e(TAG, "handleLocationUpdate(): Unable to retrieve location from locationClient.  " +
              "Ensure that the proper permissions(android.permission.ACCESS_COARSE_LOCATION, " +
              "android.permission.ACCESS_FINE_LOCATION) have been declared in the " +
              "AndroidManifest.xml file.  Skipping...");
    } else {
      publishLocation(currentLocation, username, topic);
      Log.d(TAG, "handleLocationUpdate(): completed.");
    }
  }

  private static void registerForLocationUpdates(final Context context) {
    GoogleApiClient googleApiClient = WRU.getInstance(context).waitForGoogleApi();
    Log.d(TAG, "registerForLocationUpdates(): requesting location updates from FusedLocationApi");
    LocationRequest request = new LocationRequest()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setSmallestDisplacement(10) // to prevent unnecessary updates if we haven't moved 10m
            .setInterval(5 * 60 * 60 * 1000) // 5 minutes
            .setFastestInterval(5000) // 5 seconds
            ;
    PendingResult<Status> result = LocationServices.FusedLocationApi
            .requestLocationUpdates(googleApiClient, request, getLocationUpdatedPendingIntent(context));
    Log.d(TAG, "registerForLocationUpdates(): result=" + result);
  }

  private static void unregisterForLocationUpdates(final Context context) {
    GoogleApiClient googleApiClient = WRU.getInstance(context).waitForGoogleApi();
    Log.d(TAG, "unregisterForLocationUpdates(): removing location updates from FusedLocationApi");
    PendingResult<Status> result= LocationServices.FusedLocationApi
            .removeLocationUpdates(googleApiClient, getLocationUpdatedPendingIntent(context));
  }

  public static final class LocationBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = LocationBroadcastReceiver.class.getSimpleName();
    public void onReceive(Context context, Intent intent) {
      //TODO: Handle boot completed intent as well
      Log.d(TAG, "onReceive(): intent=" + intent);
      intent.setComponent(new ComponentName(context, LocationUpdaterService.class));
      context.startService(intent);
    }
  }
}
