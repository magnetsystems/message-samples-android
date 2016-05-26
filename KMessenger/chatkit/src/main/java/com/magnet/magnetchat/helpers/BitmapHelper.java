package com.magnet.magnetchat.helpers;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Artli_000 on 10.03.2016.
 */
public class BitmapHelper {
    @SuppressLint("LongLogTag")
    public static Uri storeImage(Bitmap image, int quality) {
        Uri uri = null;
        try {
            String dcimPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
            String timeStamp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date());
            String filePath = String.format("%s/DCIM%s.png", dcimPath, timeStamp);
            File pictureFile = new File(filePath);
            if (pictureFile == null) {
                Log.d("private void storeImage(Bitmap image)",
                        "Error creating media file, check storage permissions: ");// e.getMessage());
                return null;
            }
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, quality, fos);
            fos.close();
            uri = new Uri.Builder().appendEncodedPath(filePath).build();
        } catch (Exception e) {
            Log.d("private void storeImage(Bitmap image)", e.toString());
        } finally {
            return uri;
        }
    }
}
