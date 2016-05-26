package com.magnet.magnetchat.helpers;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class FileHelper {

    private final static String TAG = FileHelper.class.getSimpleName();

    public static String getPath(final Context context, final Uri uri) {
        String path = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && DocumentsContract.isDocumentUri(context, uri)) {
            InputStream is = null;
            if (uri.getAuthority() != null) {
                try {
                    is = context.getContentResolver().openInputStream(uri);
                    Bitmap bmp = BitmapFactory.decodeStream(is);
                    path = writeToTempImageAndGetPathUri(context, bmp).toString();
                } catch (FileNotFoundException e) {
                    Log.e(TAG, "Failed to read content", e);
                }finally {
                    try {
                        is.close();
                    } catch (IOException e) {
                        //e.printStackTrace();
                    }
                }
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    path = Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                path = getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };
                path = getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            path = getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            path = uri.getPath();
        }

        Log.d(TAG, "Get path " + path + " from Uri " + uri);

        return path;
    }

    public static Uri writeToTempImageAndGetPathUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        Log.d(TAG, "writeToTempImageAndGetPathUri : " + path);
        return Uri.parse(path);
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = android.provider.MediaStore.MediaColumns.DATA;
        final String[] projection = {
                column
        };
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static Bitmap getImageBitmap(Context context, Uri uri) {
        try {
            final InputStream ist = context.getContentResolver().openInputStream(uri);
            return BitmapFactory.decodeStream(ist);
        } catch (Exception e) {
            Log.e(TAG, "Failed to read image from uri " + uri, e);
        }

        return null;
    }

    public static String getMimeType(Context context, Uri uri, String fileName, String type) {
        String result = null;
        if(null != uri) {
            String typeFromUri = getMimeType(context, uri);
            if(null != typeFromUri) {
                result = typeFromUri;
            }
        }

        if(null == result && null != fileName) {
            result = getMimeType(fileName, type);
        }

        Log.d(TAG, "Mime type for " + uri + " file name " + fileName + " is " + result);

        return result;
    }

    public static String getMimeType(Context context, Uri uri) {
        String result = context.getContentResolver().getType(uri);
        Log.d(TAG, "Mime type for uri " + uri + " is " + result);
        return result;
    }

    public static String getMimeType(String fileName, String type) {
        int idx = fileName.lastIndexOf(".");
        if (idx >= 0 && idx < fileName.length() - 1) {
            String format = fileName.substring(idx + 1);
            return MimeTypeMap.getSingleton().getMimeTypeFromExtension(format);
        }
        return type + "/*";
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isNewGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.contentprovider".equals(uri.getAuthority());
    }

}
