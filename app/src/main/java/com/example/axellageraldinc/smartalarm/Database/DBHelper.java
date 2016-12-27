package com.example.axellageraldinc.smartalarm.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.axellageraldinc.smartalarm.ModifyAlarm;
import com.example.axellageraldinc.smartalarm.ModifyBelManual;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by almantera on 14/11/16.
 */
public class DBHelper extends SQLiteOpenHelper {

    BelOtomatisModel belOtomatisModel;
    SQLiteDatabase db;
    BelManualModel bmm;

    public static final String DATABASE_NAME = "Alarm.db";
    public static final String TABLE_ALARM = "alarm";
    public static final String ID_ALARM = "_id";
    public static final String HOUR_ALARM = "hour";
    public static final String MINUTE_ALARM = "minute";
    public static final String RINGTONE_ALARM = "ringtone";
    public static final String SETDAY_ALARM = "set_day";
    public static final String STATUS_ALARM = "status"; // 0 if Off, 1 if On
    public static final String VIBRATE_ALARM = "vibrate";
    public static final String ALARM_DURATION = "alarm_duration";
    public static final String ALARM_RINGTONE = "alarm_ringtone";
    public static final String ALARM_OFF_METHOD = "alarm_off_method";
    public static final String ID2 = "ID2";
    public static final String JUDUL_BEL = "judul_bel";
    public static final String order_alarm = "order_alarm";
    public static final String TABLE_2 = "alarm2";
    public static final String ALARM_VOLUME = "volume_alarm";
    public static final String TABLE_3 = "alarm3";
    public static final String TABLE_4 = "ManualAlarm";
    public static final String ID_MANUAL = "id_manual";
    public static final String judul_manual = "judul_manual";
    public static final String ringtone_manual = "ringtone_manual";
    public static final String durasi_manual = "durasi_manual";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    // Creating database
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_ALARM + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, hour INTEGER, minute INTEGER, " +
                "ringtone TEXT, set_day TEXT, status INTEGER, alarm_duration INTEGER, " +
                "ID2 INTEGER, judul_bel TEXT, order_alarm INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_2 + " (volume_alarm INTEGER)");
        db.execSQL("INSERT INTO " + TABLE_2 + " values (8)");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_3 + " (alarm_duration INTEGER)");
        db.execSQL("INSERT INTO " + TABLE_3 + " values (10)");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_4 + " (id_manual INTEGER PRIMARY KEY AUTOINCREMENT, judul_manual TEXT, " +
                "ringtone_manual TEXT, durasi_manual INTEGER)");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALARM);
        onCreate(db);
    }

    public boolean createBelManual(BelManualModel bmm) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(judul_manual, bmm.getNama_bel_manual());
        values.put(ringtone_manual, bmm.getRingtone_manual());
        values.put(durasi_manual, bmm.getDurasi_manual());

        long result = db.insert(TABLE_4, null, values);
        db.close();
        return result != -1;
    }

    public List<BelManualModel> getAllBelManual() {
        List<BelManualModel> belManualModelList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_4;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                bmm = new BelManualModel();
                bmm.setId_manual(cursor.getInt(0));
                bmm.setNama_bel_manual(cursor.getString(1));
                bmm.setRingtone_manual(cursor.getString(2));
                bmm.setDurasi_manual(cursor.getInt(3));
                belManualModelList.add(bmm);
            } while (cursor.moveToNext());
        }
        return belManualModelList;
    }

    public BelManualModel getBelManualDetail(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_4, null,
                "id_manual=?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        bmm = new BelManualModel();
        bmm.setId_manual(cursor.getInt(0));
        bmm.setNama_bel_manual(cursor.getString(1));
        bmm.setRingtone_manual(cursor.getString(2));
        bmm.setDurasi_manual(cursor.getInt(3));
        return bmm;
    }

    public boolean updateBelManual(BelManualModel bmm) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(judul_manual, bmm.getNama_bel_manual());
        values.put(ringtone_manual, bmm.getRingtone_manual());
        values.put(durasi_manual, bmm.getDurasi_manual());

        long result = db.update(TABLE_4, values, "id_manual=" + ModifyBelManual.ID, null);
        db.close();
        return result != -1;
    }

    public void deleteBelManual(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_4, ID_MANUAL + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void InsertVolume(int volume){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ALARM_VOLUME, volume);
        db.update(TABLE_2, cv, null, null);
        db.close();
    }

    public int GetVolume(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + ALARM_VOLUME + " FROM " + TABLE_2;
        Cursor c = db.rawQuery(query, null);
        int volume = 0;
            if (c.moveToFirst()){
                do{
                    volume = c.getInt(c.getColumnIndex("volume_alarm"));
                }
                while(c.moveToNext());
            }
        db.close();
        return volume;
    }

    public String GetRingtone(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + RINGTONE_ALARM + " FROM " + TABLE_ALARM;
        Cursor c = db.rawQuery(query, null);
        String data = new String();
        if (c.moveToFirst()){
            do{
                data = c.getString(c.getColumnIndex("ringtone"));
            }
            while(c.moveToNext());
        }
        db.close();
        return data;
    }

    public void InsertDuration(int duration){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ALARM_DURATION, duration);
        db.update(TABLE_3, cv, null, null);
        db.close();
    }

    public int GetDuration(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + ALARM_DURATION + " FROM " + TABLE_3;
        Cursor c = db.rawQuery(query, null);
        int duration = 0;
        if (c.moveToFirst()){
            do{
                duration = c.getInt(c.getColumnIndex("alarm_duration"));
            }
            while(c.moveToNext());
        }
        db.close();
        return duration;
    }

    // Create alarm
    public boolean createAlarm(BelOtomatisModel belOtomatisModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(HOUR_ALARM, belOtomatisModel.getHour());
        values.put(MINUTE_ALARM, belOtomatisModel.getMinute());
        values.put(RINGTONE_ALARM, belOtomatisModel.getRingtone());
        values.put(SETDAY_ALARM, belOtomatisModel.getSet_day());
        values.put(STATUS_ALARM, belOtomatisModel.getStatus());
        values.put(ALARM_DURATION, belOtomatisModel.getAlarm_duration());
        values.put(ID2, belOtomatisModel.getID2());
        values.put(JUDUL_BEL, belOtomatisModel.getJudul_bel());
        values.put(order_alarm, belOtomatisModel.getOrder_alarm());

        long result = db.insert(TABLE_ALARM, null, values);
        db.close();
        return result != -1;
    }

    // Update Alarm
    public boolean updateAlarm(BelOtomatisModel belOtomatisModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(HOUR_ALARM, belOtomatisModel.getHour());
        values.put(MINUTE_ALARM, belOtomatisModel.getMinute());
        values.put(RINGTONE_ALARM, belOtomatisModel.getRingtone());
        values.put(SETDAY_ALARM, belOtomatisModel.getSet_day());
        values.put(STATUS_ALARM, belOtomatisModel.getStatus());
        values.put(ALARM_DURATION, belOtomatisModel.getAlarm_duration());
        values.put(ID2, belOtomatisModel.getID2());
        values.put(JUDUL_BEL, belOtomatisModel.getJudul_bel());
        values.put(order_alarm, belOtomatisModel.getOrder_alarm());

        long result = db.update(TABLE_ALARM, values, "_id= " + ModifyAlarm.ID, null);
        db.close();
        return result != -1;
    }

    // Get 1 Alarm
    public BelOtomatisModel getAlarmModel(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_ALARM, null,
                "_id=?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        belOtomatisModel = new BelOtomatisModel();
        belOtomatisModel.setId(Integer.parseInt(cursor.getString(0)));
        belOtomatisModel.setHour(cursor.getInt(1));
        belOtomatisModel.setMinute(cursor.getInt(2));
        belOtomatisModel.setRingtone(cursor.getString(3));
        belOtomatisModel.setSet_day(cursor.getString(4));
        belOtomatisModel.setStatus(cursor.getInt(5));
        belOtomatisModel.setAlarm_duration(cursor.getInt(6));
        belOtomatisModel.setID2(cursor.getInt(7));
        belOtomatisModel.setJudul_bel(cursor.getString(8));
        belOtomatisModel.setOrder_alarm(cursor.getInt(9));
        return belOtomatisModel;
    }

    // Get All alarm for alarm adapter
    public List<BelOtomatisModel> getAllAlarm() {
        List<BelOtomatisModel> belOtomatisModelList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_ALARM + " ORDER BY " + HOUR_ALARM + ", " + MINUTE_ALARM;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                belOtomatisModel = new BelOtomatisModel();
                belOtomatisModel.setId(Integer.parseInt(cursor.getString(0)));
                belOtomatisModel.setHour(cursor.getInt(1));
                belOtomatisModel.setMinute(cursor.getInt(2));
                belOtomatisModel.setRingtone(cursor.getString(3));
                belOtomatisModel.setSet_day(cursor.getString(4));
                belOtomatisModel.setStatus(cursor.getInt(5));
                belOtomatisModel.setAlarm_duration(cursor.getInt(6));
                belOtomatisModel.setID2(cursor.getInt(7));
                belOtomatisModel.setJudul_bel(cursor.getString(8));
                belOtomatisModel.setOrder_alarm(cursor.getInt(9));
                belOtomatisModelList.add(belOtomatisModel);
            } while (cursor.moveToNext());
        }
        return belOtomatisModelList;
    }

    // Update alarm status (on/off)
    public boolean updateAlarmStatus(int ID, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(STATUS_ALARM, status);
        long rowAffected = db.update(TABLE_ALARM, values, "_id=?"
                , new String[] {String.valueOf(ID)});
        return rowAffected != -1;
    }

    // Get alarm status (on/off)
    public boolean getAlarmStatus(int ID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ALARM, null, "_id=?", new String[]{String.valueOf(ID)}, null, null, null);
        int i=0;
        if (cursor.moveToFirst()) {
            i = cursor.getInt(cursor.getColumnIndex(STATUS_ALARM));
        }
        return i == 1;
    }

    public BelOtomatisModel getID2 (String ID){
        String query = "SELECT " + ID2 + " FROM " + TABLE_ALARM + " WHERE " + ID_ALARM + "=" + ID;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor!=null)
            cursor.moveToFirst();
        belOtomatisModel = new BelOtomatisModel();
        belOtomatisModel.setId(Integer.parseInt(cursor.getString(0)));
        belOtomatisModel.setHour(cursor.getInt(1));
        belOtomatisModel.setMinute(cursor.getInt(2));
        belOtomatisModel.setRingtone(cursor.getString(3));
        belOtomatisModel.setRingtone(cursor.getString(4));
        belOtomatisModel.setStatus(cursor.getInt(5));
        belOtomatisModel.setAlarm_duration(cursor.getInt(6));
        belOtomatisModel.setID2(cursor.getInt(7));
        belOtomatisModel.setJudul_bel(cursor.getString(8));
        return belOtomatisModel;
    }

    // Delete 1 alarm from database
    public void deleteAlarm(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ALARM, ID_ALARM + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    // Clear alarm value in database
    public void deleteAllAlarm() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_ALARM);
    }

    public BelOtomatisModel getID (String ID){
        String query = "SELECT " + ID_ALARM + " FROM " + TABLE_ALARM + " WHERE " + ID_ALARM + "=" + ID;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor!=null)
            cursor.moveToFirst();
        belOtomatisModel = new BelOtomatisModel();
        belOtomatisModel.setId(Integer.parseInt(cursor.getString(0)));
        belOtomatisModel.setHour(cursor.getInt(1));
        belOtomatisModel.setMinute(cursor.getInt(2));
        belOtomatisModel.setRingtone(cursor.getString(3));
        belOtomatisModel.setRingtone(cursor.getString(4));
        belOtomatisModel.setStatus(cursor.getInt(5));
        belOtomatisModel.setAlarm_duration(cursor.getInt(6));
        belOtomatisModel.setID2(cursor.getInt(7));
        belOtomatisModel.setJudul_bel(cursor.getString(8));
        return belOtomatisModel;
    }

    public String[] getAlarmID2(int id2) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_ALARM + " WHERE " + ID2 + "=" + id2;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            return new String[]{cursor.getString(cursor.getColumnIndex(SETDAY_ALARM))
                    , String.valueOf(cursor.getInt(cursor.getColumnIndex(ID_ALARM)))};
        } else {
            return null;
        }
    }

    public Cursor cursorAlarm() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ALARM, null, null, null, null, null, HOUR_ALARM + "," +MINUTE_ALARM);
        return cursor;
    }

    public Cursor cursorOneAlarm(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ALARM, null, ID_ALARM+"=?", new String[]{String.valueOf(id)}, null, null, null, null);
        return cursor;
    }
}
