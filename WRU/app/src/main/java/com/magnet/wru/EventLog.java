package com.magnet.wru;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventLog {
  private static final String TAG = EventLog.class.getSimpleName();
  private static final String PREF_ENABLED = "enabled";
  private SharedPreferences mSharedPrefs = null;
  private boolean mIsEnabled;
  public enum Type {ERROR, INFO}

  private static EventLog sInstance = null;
  private Context mContext = null;
  private DBHelper mDbHelper = null;

  private EventLog(Context context) {
    mContext = context.getApplicationContext();
    mDbHelper = new DBHelper(mContext);
    mSharedPrefs = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
    mIsEnabled = mSharedPrefs.getBoolean(PREF_ENABLED, false);
  }

  public synchronized static EventLog getInstance(Context context) {
    if (sInstance == null) {
      sInstance = new EventLog(context);
    }
    return sInstance;
  }

  public boolean isEnabled() {
    return mIsEnabled;
  }

  public void setEnabled(boolean isEnabled) {
    mSharedPrefs.edit().putBoolean(PREF_ENABLED, isEnabled).apply();
    mIsEnabled = isEnabled;
  }

  public void add(Type type, String text) {
    if (mIsEnabled) {
      SQLiteDatabase db = mDbHelper.getWritableDatabase();
      try {
        ContentValues values = new ContentValues();
        values.put(DBHelper.SQL.EventTable.COL_TEXT, text);
        values.put(DBHelper.SQL.EventTable.COL_TYPE, type.name());
        values.put(DBHelper.SQL.EventTable.COL_TIMESTAMP, System.currentTimeMillis());
        long rowId = db.insert(DBHelper.SQL.EventTable.TABLE_NAME, "", values);
        if (rowId >= 0) {
          Log.d(TAG, "add(): success");
        } else {
          Log.w(TAG, "add(): failure");
        }
      } catch (Exception ex) {
        //db may not have been created yet.
        Log.w(TAG, "add(): caught exception.  Most likely database is being initialized", ex);
      }
    }
  }

  public void clear() {
    SQLiteDatabase db = mDbHelper.getWritableDatabase();
    db.execSQL(DBHelper.SQL.DELETE_EVENTS);
  }

  public List<Event> listEvents(Type type, int maxItems) {
    SQLiteDatabase db = mDbHelper.getReadableDatabase();
    ArrayList<Event> result = new ArrayList<>();
    Cursor cursor = null;
    try {
      StringBuilder where = new StringBuilder();
      if (type != null) {
        where.append(DBHelper.SQL.EventTable.COL_TYPE).append("='").append(type.name()).append("'");

      }
      String orderBy = DBHelper.SQL.EventTable.COL_TIMESTAMP + " DESC";
      String limit = String.valueOf(maxItems);
      cursor = db.query(DBHelper.SQL.EventTable.TABLE_NAME, null, where.toString(), null, null, null, orderBy, limit);
      int idxText = cursor.getColumnIndex(DBHelper.SQL.EventTable.COL_TEXT);
      int idxTimestamp = cursor.getColumnIndex(DBHelper.SQL.EventTable.COL_TIMESTAMP);
      int idxType = cursor.getColumnIndex(DBHelper.SQL.EventTable.COL_TYPE);
      while (cursor.moveToNext()) {
        result.add(new Event(Type.valueOf(cursor.getString(idxType)),
                cursor.getString(idxText), new Date(cursor.getLong(idxTimestamp))));
      }
      cursor.close();
    } finally {
      if (cursor != null) {
        cursor.close();
      }
    }
    return result;
  }

  public static class Event {
    public final Type type;
    public final String text;
    public final Date timestamp;

    private Event(Type type, String text, Date timestamp) {
      this.type = type;
      this.text = text;
      this.timestamp = timestamp;
    }
  }

  private class DBHelper extends SQLiteOpenHelper {
    private static final String NAME = "EventLog";
    private static final int VERSION = 1;
    private class SQL {
      private class EventTable implements BaseColumns {
        private static final String TABLE_NAME = "events";
        private static final String COL_TEXT = "text";
        private static final String COL_TYPE = "type";
        private static final String COL_TIMESTAMP = "timestamp";
      }

      private static final String CREATE_EVENT_TABLE =
              "CREATE TABLE " + EventTable.TABLE_NAME + " ( " +
                      EventTable.COL_TEXT + " TEXT NOT NULL, " +
                      EventTable.COL_TYPE + " TEXT NOT NULL, " +
                      EventTable.COL_TIMESTAMP + " TEXT NOT NULL)";
      private static final String DELETE_EVENTS =
              "DELETE FROM " + EventTable.TABLE_NAME;
    }


    public DBHelper(Context context) {
      super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
      db.execSQL(SQL.CREATE_EVENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
  }
}
