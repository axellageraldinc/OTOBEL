package com.example.axellageraldinc.smartalarm.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by almantera on 14/11/16.
 */

public class DBHelper extends SQLiteOpenHelper {

    AlarmModel alarmModel;

    public static final String DATABASE_NAME = "Alarm.db";
    public static final String TABLE_ALARM = "alarm";
    public static final String ID_ALARM = "id";
    public static final String HOUR_ALARM = "hour";
    public static final String MINUTE_ALARM = "minute";
    public static final String RINGTONE_ALARM = "ringtone";
    public static final String SETDAY_ALARM = "set_day";
    public static final String STATUS_ALARM = "status"; // 0 if Off, 1 if On

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_ALARM + " (id INTEGER PRIMARY KEY AUTOINCREMENT, hour TEXT, minute TEXT, " +
                "ringtone TEXT, set_day TEXT, status INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALARM);
        onCreate(db);
    }

    public boolean createAlarm(AlarmModel alarmModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(HOUR_ALARM, alarmModel.getHour());
        values.put(MINUTE_ALARM, alarmModel.getMinute());
        values.put(RINGTONE_ALARM, alarmModel.getRingtone());
        values.put(SETDAY_ALARM, alarmModel.getSet_day());
        values.put(STATUS_ALARM, alarmModel.getStatus());

        long result = db.insert(TABLE_ALARM, null, values);
        db.close();
        if (result == -1) return false;
        else return true;
    }

    public AlarmModel getAlarmModel(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_ALARM, new String[]{HOUR_ALARM,MINUTE_ALARM,RINGTONE_ALARM,SETDAY_ALARM,STATUS_ALARM},
                ID_ALARM + "=?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        alarmModel = new AlarmModel(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getInt(1));
        return alarmModel;
    }

    public List<AlarmModel> getAllAlarm() {
        List<AlarmModel> alarmModelList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_ALARM;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                alarmModel = new AlarmModel(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getInt(1));
                alarmModelList.add(alarmModel);
            } while (cursor.moveToNext());
        }
        return alarmModelList;
    }

    public boolean updateAlarmStatus(int id, String hour, String minute) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(STATUS_ALARM, alarmModel.getStatus());
        int rowAffected = db.update(TABLE_ALARM, values, "id=? AND hour=? AND minute=?"
                , new String[] {String.valueOf(id), hour, minute});
        if (rowAffected == 1) return true;
        else return false;
    }

    public void deleteAlarm(AlarmModel alarmModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ALARM, ID_ALARM + "=?", new String[]{String.valueOf(alarmModel.getId())});
        db.close();
    }

    public void deleteAllAlarm() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_ALARM);
    }
}
