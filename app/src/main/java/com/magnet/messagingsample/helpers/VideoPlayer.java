package com.magnet.messagingsample.helpers;

import android.util.Log;
import android.webkit.URLUtil;
import android.widget.VideoView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by edwardyang on 9/16/15.
 */
public class VideoPlayer {

    final String TAG = "VideoPlayer";
    String current;

    public void play(final String path, VideoView videoView) {
        try {
            Log.v(TAG, "path: " + path);
            if (path == null || path.length() == 0) {

            } else {
                // If the path has not changed, just start the media player
                if (path.equals(current) && videoView != null) {
                    videoView.start();
                    videoView.requestFocus();
                    return;
                }
                current = path;
                videoView.setVideoPath(getDataSource(path));
                videoView.start();
                videoView.requestFocus();

            }
        } catch (Exception e) {
            Log.e(TAG, "error: " + e.getMessage(), e);
            if (videoView != null) {
                videoView.stopPlayback();
            }
        }
    }

    private String getDataSource(String path) throws IOException {
        if (!URLUtil.isNetworkUrl(path)) {
            return path;
        } else {
            URL url = new URL(path);
            URLConnection cn = url.openConnection();
            cn.connect();
            InputStream stream = cn.getInputStream();
            if (stream == null)
                throw new RuntimeException("stream is null");
            File temp = File.createTempFile("mediaplayertmp", "dat");
            temp.deleteOnExit();
            String tempPath = temp.getAbsolutePath();
            FileOutputStream out = new FileOutputStream(temp);
            byte buf[] = new byte[128];
            do {
                int numread = stream.read(buf);
                if (numread <= 0)
                    break;
                out.write(buf, 0, numread);
            } while (true);
            try {
                stream.close();
            } catch (IOException ex) {
                Log.e(TAG, "error: " + ex.getMessage(), ex);
            }
            return tempPath;
        }
    }

}
