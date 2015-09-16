package com.magnet.messagingsample.activities;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.MediaController;
import android.widget.VideoView;

import com.magnet.messagingsample.R;
import com.magnet.messagingsample.helpers.TouchImageView;
import com.squareup.picasso.Picasso;

import java.io.File;

public class VideoViewActivity extends AppCompatActivity {

    VideoView vidMessageVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);

        String imageUrl = getIntent().getStringExtra("videoUrl");

        Uri uri = Uri.parse(imageUrl);
        vidMessageVideo = (VideoView)findViewById(R.id.vidMessageVideo);
        vidMessageVideo.setMediaController(new MediaController(this));
        vidMessageVideo.setVideoURI(uri);
        vidMessageVideo.requestFocus();
        vidMessageVideo.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_video_view, menu);
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
