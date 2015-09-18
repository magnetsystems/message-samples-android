package com.magnet.messagingsample.activities;

import android.support.v7.app.ActionBar;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.magnet.messagingsample.R;
import com.magnet.messagingsample.helpers.TouchImageView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

public class ImageViewActivity extends AppCompatActivity {

    public TouchImageView ivMessageImage;
    final String TAG = "ImageViewActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        String imageUrl = getIntent().getStringExtra("imageUrl");
        ivMessageImage = (TouchImageView) findViewById(R.id.ivMessageImage);

        if (imageUrl != null) {
            Picasso.with(this).load(imageUrl).into(ivMessageImage);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_image_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
