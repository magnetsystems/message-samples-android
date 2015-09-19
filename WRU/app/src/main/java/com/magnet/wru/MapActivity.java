package com.magnet.wru;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.magnet.mmx.client.api.MMXUser;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
  private static final String TAG = MapActivity.class.getSimpleName();
  private WRU mWru = null;
  private GoogleMap mMap = null;
  private TextView mTextTop = null;
  private SupportMapFragment mMapFragment = null;
  private MessageListFragment mMessageListFragment = null;
  private ImageButton mToggleMessageList = null;
  private DateFormat mFormatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
  private MyMessageStore.OnChangeListener mMessageListListener = new MyMessageStore.OnChangeListener() {
    public void onChange() {
      if (!mMessageListOpen) {
        runOnUiThread(new Runnable() {
          public void run() {
            mToggleMessageList.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
          }
        });
      }
    }
  };

  private WRU.OnLocationReceivedListener mListener = new WRU.OnLocationReceivedListener() {
    public void onLocationReceived(MMXUser user, WRU.LocationTime locationTime) {
      updateLocationMarkers(locationTime);
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_map);
    mWru = WRU.getInstance(this);
    mWru.registerOnLocationReceivedListener(mListener);
    mTextTop = (TextView) findViewById(R.id.text_top);
    mTextTop.setText("group key: " + mWru.getJoinedTopicKey() + ", passphrase: " + mWru.getJoinedTopicPassphrase());
    mToggleMessageList = (ImageButton) findViewById(R.id.toggle_message_list);

    //setup map
    mMapFragment = (SupportMapFragment) getSupportFragmentManager()
            .findFragmentById(R.id.map);
    mMapFragment.getMapAsync(this);

    mMessageListFragment = (MessageListFragment) getSupportFragmentManager()
            .findFragmentById(R.id.fragment_message_list);

    MyMessageStore.registerListener(mMessageListListener);
  }

  protected void onResume() {
    updateLocationMarkers(null);
    MyMessageStore.sSuppressNotification = true;
    super.onResume();
  }

  protected void onPause() {
    MyMessageStore.sSuppressNotification = false;
    super.onPause();
  }

  protected void onDestroy() {
    mWru.unregisterOnLocationReceivedListener(mListener);
    MyMessageStore.unregisterListener(mMessageListListener);
    super.onDestroy();
  }

  @Override
  public void onMapReady(GoogleMap map) {
    mMap = map;
    mMap.setMyLocationEnabled(true);
    mMap.setTrafficEnabled(true);

    AsyncTask.execute(new Runnable() {
      public void run() {
        GoogleApiClient googleApiClient = mWru.waitForGoogleApi();
        Log.d(TAG, "onMapReady(): requesting last location");
        Location result = LocationServices.FusedLocationApi
                .getLastLocation(googleApiClient);
        if (result != null) {
          final LatLng myloc = new LatLng(result.getLatitude(),
                  result.getLongitude());
          runOnUiThread(new Runnable() {
            public void run() {
              mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myloc, 16));
            }
          });
        }
      }
    });
    updateLocationMarkers(null);
    Handler handler = new Handler();
    handler.postDelayed(new Runnable() {
      public void run() {
        mWru.requestLocationUpdates();
      }
    }, 2000);
  }

  private HashMap<String, Marker> mMarkerMap = new HashMap<>();
  private HashMap<String, Circle> mCircleMap = new HashMap<>();

  private void updateLocationMarkers(final WRU.LocationTime locationTime) {
    if (locationTime == null) {
      for (WRU.LocationTime entry : mWru.getLocationTimes().values()) {
        updateMarker(entry);
      }
    } else {
      updateMarker(locationTime);
    }
  }

  private void updateMarker(final WRU.LocationTime locationTime) {
    runOnUiThread(new Runnable() {
      public void run() {
        if (mMap == null) {
          Log.w(TAG, "updateMarker(): map is not ready yet, ignoring");
          return;
        }
        if (!locationTime.username.equalsIgnoreCase(mWru.getUsername())) {
          Marker marker = mMarkerMap.get(locationTime.username);
          Circle circle = mCircleMap.get(locationTime.username);
          LatLng loc = new LatLng(locationTime.location.getLat(), locationTime.location.getLng());
          StringBuilder snippet = new StringBuilder()
                  .append("+/-").append(locationTime.location.getAccuracy()).append("m, fix: ")
                  .append(mFormatter.format(new Date(locationTime.locationTimestamp)))
                  .append(", rcv: ")
                  .append(mFormatter.format(new Date(locationTime.timestamp)));
          if (marker == null) {
            String label = locationTime.username;
            marker = mMap.addMarker(new MarkerOptions().position(loc).title(label).snippet(snippet.toString()));
            mMarkerMap.put(locationTime.username, marker);
            //draw the circle
            mCircleMap.put(locationTime.username, mMap.addCircle(
                    new CircleOptions()
                            .center(marker.getPosition())
                            .radius(locationTime.location.getAccuracy())
                            .strokeColor(0xffff0000)
                            .strokeWidth(1.0f)
                            .fillColor(0x44ff0000)));
          } else {
            marker.setSnippet(snippet.toString());
            marker.setPosition(loc);
            if (marker.isInfoWindowShown()) {
              //refresh the marker info window
              marker.hideInfoWindow();
              marker.showInfoWindow();
            }
            circle.setCenter(marker.getPosition());
            circle.setRadius(locationTime.location.getAccuracy());
          }
          marker.setAlpha(getTimeBasedAlpha(locationTime.locationTimestamp));
        }
      }
    });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_map, menu);
    MenuItem item = menu.findItem(R.id.action_set_enabled);
    EventLog eventLog = EventLog.getInstance(this);
    if (eventLog.isEnabled()) {
      item.setTitle(R.string.action_disable);
    } else {
      item.setTitle(R.string.action_enable);
    }

    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      AlertDialog dialog = new AlertDialog.Builder(this)
              .setTitle(R.string.action_settings)
              .setMessage("YOU ARE: " + mWru.getUsername() + "\nGROUP KEY: " + mWru.getJoinedTopicKey() +
                      "\nPASSPHRASE: " + mWru.getJoinedTopicPassphrase())
              .show();
      return true;
    }

    if (id == R.id.action_request_updates) {
      mWru.requestLocationUpdates();
      return true;
    }

    if (id == R.id.action_leave) {
      mWru.leaveTopic();
      Intent intent = new Intent(this, MainActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
      startActivity(intent);
      this.finish();
      return true;
    }

    if (id == R.id.action_find) {
      final String[] users = new String[mMarkerMap.size()];
      mMarkerMap.keySet().toArray(users);
      ArrayList<String> tmpList = new ArrayList<>();
      Collections.addAll(tmpList, users);
      Collections.sort(tmpList, new SortIgnoreCase());
      tmpList.toArray(users);
      final AlertDialog dialog = new AlertDialog.Builder(this)
              .setTitle(R.string.action_find)
              .setItems(users, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                  String user = users[which];
                  Marker marker = mMarkerMap.get(user);
                  marker.showInfoWindow();

                  LatLngBounds.Builder builder = new LatLngBounds.Builder()
                          .include(marker.getPosition());
                  Location myLoc = mMap.getMyLocation();
                  if (myLoc != null) {
                    builder.include(new LatLng(mMap.getMyLocation().getLatitude(),
                            mMap.getMyLocation().getLongitude()));
                  }
                  mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 40));
                  dialog.dismiss();
                }
              })
              .show();
    }

    if (id == R.id.action_clear_log) {
      EventLog.getInstance(this).clear();
    }

    if (id == R.id.action_view_log) {
      List<EventLog.Event> events = EventLog.getInstance(this).listEvents(null, 50);
      final String[] eventStrs = new String[events.size()];

      for (int i=0; i<events.size(); i++) {
        EventLog.Event event = events.get(i);
        eventStrs[i] = mFormatter.format(event.timestamp) + " " + event.type + ": " + event.text;
      }
      final AlertDialog dialog = new AlertDialog.Builder(this)
              .setTitle(R.string.action_view_log)
              .setItems(eventStrs, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                  AlertDialog messageDialog = new AlertDialog.Builder(MapActivity.this)
                          .setMessage(eventStrs[which])
                          .show();
                }
              })
              .show();
    }

    if (id == R.id.action_set_enabled) {
      EventLog eventLog = EventLog.getInstance(this);
      eventLog.setEnabled(!eventLog.isEnabled());
      item.setTitle(eventLog.isEnabled() ? R.string.action_disable : R.string.action_enable);
      Toast.makeText(this, "Event log is " + (eventLog.isEnabled() ? "enabled" : "disabled"), Toast.LENGTH_SHORT).show();
    }
    return super.onOptionsItemSelected(item);
  }

  private static final float MARKER_MIN_ALPHA = 0.2f;
  private static final float MILLIS_IN_HOUR = 60 * 60 * 1000f;

  private float getTimeBasedAlpha(long locationTime) {
    // we will use a 1 hr window for opacity meaning at 1hr age,
    // the marker should be transparent (or at least min alpha)
    long age = System.currentTimeMillis() - locationTime;
    float calculatedFloat = (MILLIS_IN_HOUR - age) / MILLIS_IN_HOUR;
    Log.d(TAG, "getTimeBasedAlpha(): calculated float = " + String.valueOf(calculatedFloat));
    return calculatedFloat < MARKER_MIN_ALPHA ? MARKER_MIN_ALPHA : calculatedFloat;
  }

  public class SortIgnoreCase implements Comparator<Object> {
    public int compare(Object o1, Object o2) {
      String s1 = (String) o1;
      String s2 = (String) o2;
      return s1.toLowerCase().compareTo(s2.toLowerCase());
    }
  }

  public void doFragmentClick(View view) {
    mMessageListFragment.doFragmentClick(view);
  }

  private boolean mMessageListOpen = false;

  public synchronized void toggleMessageList(View view) {
    float newWeight;
    if (mMessageListOpen) {
      //hide soft keyboard
      InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
      imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
      newWeight = 0;
    } else {
      mToggleMessageList.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
      newWeight = 9;
    }
    mMessageListFragment.getView()
            .setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, newWeight));
    mMessageListOpen = !mMessageListOpen;
  }
}
