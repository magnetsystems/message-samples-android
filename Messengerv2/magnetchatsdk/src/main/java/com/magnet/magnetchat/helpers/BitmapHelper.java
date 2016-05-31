package com.magnet.magnetchat.helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;

import com.magnet.magnetchat.util.Logger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
            String filePath = generatePathForPicture();
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

    private static final int PREFER_SIZE = 800;


    /**
     * The method return image path from uri. if uri is media://
     *
     * @param context
     * @param uri     media uri
     * @return file path
     */
    public static String getBitmapPath(Context context, Uri uri) {
        String[] colums = new String[]{MediaStore.Images.Media.DATA};
        Cursor query = context.getContentResolver().query(uri, colums, null, null, null);
        if (query.moveToFirst()) {
            int index = query.getColumnIndex(colums[0]);
            String path = query.getString(index);
            return path;
        }
        return null;
    }

    /**
     * The method reads bitmap from uri path
     *
     * @param context
     * @param uri     image uri
     * @return instance of image
     */
    public static Bitmap readBitmap(Context context, Uri uri) {
        if (!MediaStore.AUTHORITY.equals(uri.getAuthority())) {
            return readBitmapWithPreferSize(uri.getPath(), PREFER_SIZE);
        }

        String path = getBitmapPath(context, uri);
        if (path != null) {
            return readBitmapWithPreferSize(path, PREFER_SIZE);
        }

        return null;
    }

    /**
     * The method reads image by file path
     *
     * @param path file path
     * @return instance of image
     */
    public static Bitmap readBitmap(String path) {
        return readBitmapWithPreferSize(path, PREFER_SIZE);
    }

    public static Bitmap readBitmapFromAssets(Context context, String string) {
        InputStream is = null;
        try {
            is = context.getAssets().open(string);
            return BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            Logger.error("readBitmapFromAssets", null, e);
        }
        return null;
    }

    /**
     * The method generates path for image
     * Needs for picture capture flow
     *
     * @return DCIM path
     */
    public static String generatePathForPicture() {
        String dcimPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
        return String.format("%s/IMG_%d.png", dcimPath, System.currentTimeMillis());
    }

    /**
     * The method reads scaled instance of bitmap
     *
     * @param path       to image
     * @param preferSize size what you want
     * @return scaled instance of bitmap
     */
    private static Bitmap readBitmapWithPreferSize(String path, int preferSize) {
        Logger.error("PATH_", "readBitmapWithPrefSize", path);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        int scale = calculateScale(options, preferSize);

        options = new BitmapFactory.Options();
        options.inSampleSize = scale;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        return bitmap;
    }

    /**
     * The method calculate scale rate for image from gallery
     *
     * @param options    info about image
     * @param preferSize needed size of image
     * @return scale rate
     */
    private static int calculateScale(BitmapFactory.Options options, int preferSize) {
        int sampleSize = 1;

        int halfH = options.outHeight / 2;
        int halfW = options.outWidth / 2;

        while ((halfH / sampleSize) > preferSize || (halfW / sampleSize) > preferSize) {
            sampleSize *= 2;
        }

        return sampleSize;
    }

}
