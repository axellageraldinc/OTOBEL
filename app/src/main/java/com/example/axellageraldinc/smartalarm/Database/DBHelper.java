package com.example.axellageraldinc.smartalarm.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.axellageraldinc.smartalarm.ModifyAlarm;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by almantera on 14/11/16.
 */
//Todo : vibrate alarm
//Todo : alarm on/off
public class DBHelper extends SQLiteOpenHelper {

    AlarmModel alarmModel;
    SQLiteDatabase db;

    public static final String DATABASE_NAME = "Alarm.db";
    public static final String TABLE_ALARM = "alarm";
    public static final String ID_ALARM = "id";
    public static final String HOUR_ALARM = "hour";
    public static final String MINUTE_ALARM = "minute";
    public static final String RINGTONE_ALARM = "ringtone";
    public static final String SETDAY_ALARM = "set_day";
    public static final String STATUS_ALARM = "status"; // 0 if Off, 1 if On
    public static final String VIBRATE_ALARM = "vibrate";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    // Creating database
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_ALARM + " (id INTEGER PRIMARY KEY AUTOINCREMENT, hour TEXT, minute TEXT, " +
                "ringtone TEXT, set_day TEXT, status INTEGER, vibrate INTEGER)");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALARM);
        onCreate(db);
    }

    // Create alarm
    public boolean createAlarm(AlarmModel alarmModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(HOUR_ALARM, alarmModel.getHour());
        values.put(MINUTE_ALARM, alarmModel.getMinute());
        values.put(RINGTONE_ALARM, alarmModel.getRingtone());
        values.put(SETDAY_ALARM, alarmModel.getSet_day());
        values.put(STATUS_ALARM, alarmModel.getStatus());
        values.put(VIBRATE_ALARM, alarmModel.getVibrate());

        long result = db.insert(TABLE_ALARM, null, values);
        db.close();
        return result != -1;
    }

    // Update Alarm
    public boolean updateAlarm(String hour, String minute) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(HOUR_ALARM, hour);
        values.put(MINUTE_ALARM, minute);
        /*values.put(RINGTONE_ALARM, alarmModel.getRingtone());
        values.put(SETDAY_ALARM, alarmModel.getSet_day());
        values.put(STATUS_ALARM, alarmModel.getStatus());
        values.put(VIBRATE_ALARM, alarmModel.getVibrate());*/

        long result = db.update(TABLE_ALARM, values, "hour= " + ModifyAlarm.hourModify + " AND minute= " + ModifyAlarm.menitModify, null);
        db.close();
        return result != -1;
    }

    // Get 1 Alarm
    public AlarmModel getAlarmModel(String hour, String minute) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_ALARM, null,
                "hour=? AND minute=?", new String[]{hour, minute}, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        alarmModel = new AlarmModel(
                cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4)
                , cursor.getInt(5), cursor.getInt(6));
        return alarmModel;
    }

    // Get All alarm for alarm adapter
    public List<AlarmModel> getAllAlarm() {
        List<AlarmModel> alarmModelList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_ALARM;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                alarmModel = new AlarmModel(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4)
                        , cursor.getInt(5), cursor.getInt(6));
                alarmModelList.add(alarmModel);
            } while (cursor.moveToNext());
        }
        return alarmModelList;
    }

    // Update alarm status (on/off)
    public boolean updateAlarmStatus(int status, String hour, String minute) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(STATUS_ALARM, status);
        int rowAffected = db.update(TABLE_ALARM, values, "hour=? AND minute=?"
                , new String[] {hour, minute});
        return rowAffected == 1;
    }

    // Get alarm status (on/off)
    public boolean getAlarmStatus(String hour, String minute) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ALARM, null, "hour=? AND minute=?", new String[]{hour, minute}, null, null, null);
        int i=0;
        if (cursor.moveToFirst()) {
            i = cursor.getInt(cursor.getColumnIndex(STATUS_ALARM));
        }
        return i == 1;
    }

    // Update alarm vibrate or not
    // Todo : biar bisa pake id
    public boolean updateAlarmVibrate(int id, String hour, String minute) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(STATUS_ALARM, alarmModel.getVibrate());
        int rowAffected = db.update(TABLE_ALARM, values, "id=? AND hour=? AND minute=?"
                , new String[] {String.valueOf(id), hour, minute});
        return rowAffected == 1;
    }

    public boolean getAlarmVibrate(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ALARM, null, "id=?", new String[]{String.valueOf(id)}, null, null, null);
        int i=0;
        if (cursor.moveToFirst()) {
            i = cursor.getInt(cursor.getColumnIndex(VIBRATE_ALARM));
        }
        return i == 1;
    }

    // Delete 1 alarm from database
    public void deleteAlarm(String hour, String minute) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ALARM, HOUR_ALARM + "=? AND " + MINUTE_ALARM + "=?", new String[]{hour, minute});
        db.close();
    }

    // Clear alarm value in database
    public void deleteAllAlarm() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_ALARM);
    }
}
