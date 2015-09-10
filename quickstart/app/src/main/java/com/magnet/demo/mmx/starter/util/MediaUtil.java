package com.magnet.demo.mmx.starter.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import com.magnet.mmx.client.common.Log;
import com.magnet.mmx.util.Base64;
import com.magnet.mmx.util.DisposableBinFile;
import com.magnet.mmx.util.DisposableFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public final class MediaUtil {
  public static final int LOAD_IMAGE_RESULTS_REQUEST = 1000;
  private static final float BASE_64_FACTOR = 1.37f;
  private static final String TAG = MediaUtil.class.getSimpleName();

  /**
   * Converts the specified file to a DisposableBinFile that fits into the maxSizeBytes.
   * This takes into account a factor for Base64 encoding.
   *
   * @param imageFile the file to convert
   * @param maxSizeBytes the maximum size in bytes
   * @return the DisposableBinFile for this resized image
   * @throws IOException
   */
  public static DisposableBinFile convertImageToDisposableFile(File imageFile, long maxSizeBytes)
          throws IOException {
    if (imageFile == null || imageFile.isDirectory() || !imageFile.exists()) {
      throw new IOException("Invalid file specified: " + imageFile);
    }
    long originalSize = imageFile.length();
    Log.d(TAG, "convertImageToDisposableFile(): original size=" + originalSize);
    //account for base64 encoding, 1.37x
    if (originalSize * BASE_64_FACTOR < maxSizeBytes) {
      //return a disposable file
      Log.d(TAG, "convertImageToDisposableFile(): original file is within bounds, just returning the disposable file");
      return new DisposableBinFile(imageFile.getAbsolutePath(), false);
    } else {
      BitmapFactory.Options options = new BitmapFactory.Options();
      Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(imageFile), null, options);
      Log.d(TAG, "convertImageToDisposableFile(): original dimensions: " + options.outWidth + "x" + options.outHeight);

      //determine the scale factor
      float compressionRatio = (float) originalSize / ((float) options.outWidth * options.outHeight * 32 / 8);
      float scaleFactor = (float) Math.sqrt(maxSizeBytes / BASE_64_FACTOR / compressionRatio / options.outWidth / options.outHeight);
      Log.d(TAG, "convertImageToDisposableFile(): scale factor = " + String.valueOf(scaleFactor));

      File scaledFile = File.createTempFile("FileUtil_processed_", null);
      FileOutputStream fos = new FileOutputStream(scaledFile);
      Bitmap processedBitmap;
      int quality;
      if (scaleFactor < 1) {
        int newWidth = Math.round(options.outWidth * scaleFactor);
        int newHeight = Math.round(options.outHeight * scaleFactor);
        Log.d(TAG, "convertImageToDisposableFile(): new dimensions: " + newWidth + "x" + newHeight);
        processedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, false);
        quality = 50;
      } else {
        //at this point, the file is bigger than we want it to be, but we don't want to scale
        //just adjust quality.
        processedBitmap = bitmap;
        quality = 75;
      }
      boolean success = processedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, fos);
      if (success) {
        Log.d(TAG, "convertImageToDisposableFile(): successfully compressed image new size: " + scaledFile.length() +
                ", estimated size (after base64 encoding): " + String.valueOf(scaledFile.length() * BASE_64_FACTOR));
        return new DisposableBinFile(scaledFile.getAbsolutePath(), true);
      } else {
        throw new IOException("unable to compress image");
      }
    }
  }

  /**
   * The calling activity should verify that the requestCode returned in the onActivityResult callback
   * is the one defined in LOAD_IMAGE_RESULTS_REQUEST and call this method to retrieve the
   * resized DisposableFile.
   *
   * @see MediaUtil#LOAD_IMAGE_RESULTS_REQUEST
   * @see Activity#startActivityForResult(Intent, int)
   * @see Activity#onActivityResult(int, int, Intent)
   *
   * @param context the context
   * @param resultCode the result code return in onActivityResult()
   * @param data the intent returned in onActivityResult()
   * @return the DisposableFile image that was picked
   * @throws IOException
   */
  public static DisposableFile getDisposableImageFromActivityResult(
          Context context, int resultCode, Intent data) throws IOException {
    // Here we need to check if the activity that was triggers was the Image Gallery.
    // If it is the requestCode will match the LOAD_IMAGE_RESULTS value.
    // If the resultCode is RESULT_OK and there is some data we know that an image was picked.
    if (resultCode == Activity.RESULT_OK && data != null) {
      // Let's read picked image data - its URI
      Uri pickedImage = data.getData();
      // Let's read picked image path using content resolver
      String[] filePath = { MediaStore.Images.Media.DATA };
      Cursor cursor = context.getContentResolver().query(pickedImage, filePath, null, null, null);
      try {
        cursor.moveToFirst();
        String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));
        return convertImageToDisposableFile(new File(imagePath), 200 * 1024);
      } finally {
        cursor.close();
      }
    }
    return null;
  }

  /**
   * Calls startActivityForResult for the image picker.  Will use the LOAD_IMAGE_RESULTS_REQUEST
   * request code.
   *
   * @see #LOAD_IMAGE_RESULTS_REQUEST
   * @param activity the activity
   */
  public static void startImagePickerActivityWithResult(Activity activity) {
    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    activity.startActivityForResult(intent, LOAD_IMAGE_RESULTS_REQUEST);
  }
}
