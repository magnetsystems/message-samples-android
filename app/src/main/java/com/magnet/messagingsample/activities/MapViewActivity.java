package com.magnet.messagingsample.activities;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.magnet.messagingsample.R;

public class MapViewActivity extends AppCompatActivity {

    private MapFragment mapMessageLocation;
    private String mLatlng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);

        mLatlng = getIntent().getStringExtra("latlng");

        if (mapMessageLocation == null) {
            mapMessageLocation = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapMessageLocation));
        }

        mapMessageLocation.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                loadMap(map, mLatlng);
            }
        });
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
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlong, 18));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map_view, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
