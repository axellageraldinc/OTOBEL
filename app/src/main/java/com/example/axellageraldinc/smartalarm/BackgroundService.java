package com.example.axellageraldinc.smartalarm;

/**
 * Created by Axellageraldinc A on 25-Dec-16.
 */

import android.app.*;
import android.content.*;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.*;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.axellageraldinc.smartalarm.Database.DBHelper;
import com.example.axellageraldinc.smartalarm.Receiver.AlarmReceiver;
import com.example.axellageraldinc.smartalarm.TambahBelOtomatis.SettingAlarm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

// TODO : Set alarm setelah booting

public class BackgroundService extends Service {

    private boolean isRunning;
    private Context context;
    private Thread backgroundThread;
    private DBHelper dbHelper; // Manggil database

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        this.context = this;
        this.isRunning = false;
        this.dbHelper = new DBHelper(this); // Manggil database
        this.backgroundThread = new Thread(myTask);
        loadAlarmFromDB();
    }

    private Runnable myTask = new Runnable() {
        public void run() {
            // Do something here
            stopSelf();
        }
    };

    @Override
    public void onDestroy() {
        this.isRunning = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!this.isRunning) {
            this.isRunning = true;
            this.backgroundThread.start();
            /*android.support.v4.app.NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.ic_stat_social_notifications)
                            .setContentTitle(getResources().getString(R.string.app_name))
                            .setContentText("Service running");
            Intent resultIntent = new Intent(context, HomeScreen.class);
// Because clicking the notification opens a new ("special") activity, there's
// no need to create an artificial back stack.
            mBuilder.setOngoing(true);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
            // Sets an ID for the notification
            int mNotificationId = 1234;
// Gets an instance of the NotificationManager service
            NotificationManager mNotifyMgr =
                    (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
// Builds the notification and issues it.
            mNotifyMgr.notify(mNotificationId, mBuilder.build());*/
            //Toast.makeText(this, "Service running", Toast.LENGTH_LONG).show();
        }
        // Nambah toast dan method loadAlarmFromDB, biar bisa setting alarm setelah booting (service start)
        loadAlarmFromDB();
//        return START_STICKY;

        // Ganti return value
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * Method buat load alarm dari database
     */
    private void loadAlarmFromDB() {
        Cursor cursor = dbHelper.cursorAlarm();
        if (cursor.moveToFirst()) {
            do {
                int id2 = cursor.getInt(cursor.getColumnIndex(DBHelper.ID2));
                int date = cursor.getInt(cursor.getColumnIndex(DBHelper.order_alarm));
                String repeat = cursor.getString(cursor.getColumnIndex(DBHelper.SETDAY_ALARM));
                int hour = cursor.getInt(cursor.getColumnIndex(DBHelper.HOUR_ALARM));
                int minute = cursor.getInt(cursor.getColumnIndex(DBHelper.MINUTE_ALARM));
                int status = cursor.getInt(cursor.getColumnIndex(DBHelper.STATUS_ALARM));
                String chosenRingtone = cursor.getString(cursor.getColumnIndex(DBHelper.RINGTONE_ALARM));
                int duration = cursor.getInt(cursor.getColumnIndex(DBHelper.ALARM_DURATION));
                if (status == 1) {
                    activateAlarm(BackgroundService.this, id2, date, repeat, hour, minute, chosenRingtone, duration);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    /**
     * Method buat aktivasi alarm, didalemnya ada method buat ngeset repeat alarm
     * @param id2 id2 alarm dari database
     * @param date date(harusnya time) dari database (di database namanya order)
     * @param repeat repeat alarm dari database
     * @param hour jam alarm dari database
     * @param minute minute alarm dari database
     * @param chosenRingtone chosenRingtone alarm dari database
     * @param duration duration alarm dari database
     */
    public static void activateAlarm(Context context, int id2, int date, String repeat, int hour, int minute
            , String chosenRingtone, int duration) {
        ArrayList<String> stRepeat = new ArrayList<String>();
        stRepeat.addAll(Arrays.asList(repeat.split("\\s*,\\s*")));
        Intent intent = new Intent(context, AlarmReceiver.class);
        Bundle b = new Bundle();
        if (chosenRingtone == null){
            b.putString("ringtone_alarm", null);
        } else if (chosenRingtone.equals("Default")) {
            b.putString("ringtone_alarm", null);
        } else {
            b.putString("ringtone_alarm", chosenRingtone);
        }
        b.putInt("durasi", duration);
        intent.putExtras(b);
        intent.putExtra("repeat", repeat);
        intent.putExtra("duration", duration);
        intent.putExtra("id2",id2);
        PendingIntent pi = PendingIntent.getBroadcast(context, id2, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        long time = (long) date;
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        if (repeat.equals("Don't repeat")) {
            setRepeatAlarm(0, hour, minute, am, pi);
        } else {
            if (repeat.equals("Everyday")) {
                setEverydayAlarm(hour, minute, am, pi);
            }  else {
                alarmOn(context, repeat, hour, minute, chosenRingtone, duration, id2);
            }
        }
    }

    /**
     * Method buat Setting repeat alarm
     * @param daysOfWeek hari yang mau diset (1 = Minggu, 2 = Senin, dst.)
     * @param hour jam alarm
     * @param minute menit alarm
     * @param am AlarmManager yang digunain
     * @param pi Pending intent yang digunain
     */
    public static void setRepeatAlarm(int daysOfWeek, int hour, int minute, AlarmManager am, PendingIntent pi) {
        Calendar calendar = Calendar.getInstance();
        if (daysOfWeek != 0) {
            calendar.set(Calendar.DAY_OF_WEEK, daysOfWeek);
        }
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if(calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 7);
        }
//        Log.v("Calendar in millis", String.valueOf(calendar.getTimeInMillis()));
//        long time=(calendar.getTimeInMillis()-(calendar.getTimeInMillis()%60000));
//        Log.v("Time", String.valueOf(time));
        long time=calendar.getTimeInMillis(); //(calendar.getTimeInMillis()-(calendar.getTimeInMillis()%60000));
        if(daysOfWeek==0 && System.currentTimeMillis()>time)
        {
            if (calendar.AM_PM == 0)
                time = time + (1000*60*60*12);
            else
                time = time + (1000*60*60*24);
        }else {
            if (calendar.AM_PM == 0) {
                am.setRepeating(AlarmManager.RTC_WAKEUP, time, 1000*60*60*12, pi);
            } else {
                am.setRepeating(AlarmManager.RTC_WAKEUP, time, 1000*60*60*24, pi);
            }
        }
    }

    /**
     * Method buat aktivasi alarm, didalemnya ada method buat ngeset repeat alarm
     * @param id2 id2 alarm dari database
     * @param date date(harusnya time) dari database (di database namanya order)
     * @param repeat repeat alarm dari database
     * @param hour jam alarm dari database
     * @param minute minute alarm dari database
     * @param chosenRingtone chosenRingtone alarm dari database
     * @param duration duration alarm dari database
     */
    public static void stopAlarm(Context context, int id2, int date, String repeat, int hour, int minute
            , String chosenRingtone, int duration) {
        ArrayList<String> stRepeat = new ArrayList<String>();
        stRepeat.addAll(Arrays.asList(repeat.split("\\s*,\\s*")));
        ArrayList<Integer> intRepeat = SettingAlarm.getIntDaysOfWeek(stRepeat);
        Intent intent = new Intent(context, AlarmReceiver.class);
        Bundle b = new Bundle();
        if (chosenRingtone == null){
            b.putString("ringtone_alarm", null);
        } else if (chosenRingtone.equals("Default")) {
            b.putString("ringtone_alarm", null);
        } else {
            b.putString("ringtone_alarm", chosenRingtone);
        }
        b.putInt("durasi", duration);
        intent.putExtras(b);
        intent.putExtra("repeat", repeat);
        intent.putExtra("duration", duration);
        intent.putExtra("id2",id2);
        PendingIntent pi = PendingIntent.getBroadcast(context, id2, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        am.cancel(pi);
    }

    private static void setEverydayAlarm(int hour, int minute, AlarmManager alarmManager, PendingIntent pi) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);
    }

    private static void alarmOn(Context context, String repeat, int hour, int minute, String chosenRingtone, int duration
            , int id2) {
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        Integer day = calendar.get(Calendar.DAY_OF_WEEK);
        ArrayList<String> stRepeat = new ArrayList<String>();
        stRepeat.addAll(Arrays.asList(repeat.split("\\s*,\\s*")));
        ArrayList<Integer> daysOfWeek = SettingAlarm.getIntDaysOfWeek(stRepeat);
        int dow = 0;
        int i = 0, j = 0, k = 0, index = 0;
        while (j < daysOfWeek.size()) {
            if (calendar.get(Calendar.DAY_OF_WEEK) > daysOfWeek.get(j)) {
                i = daysOfWeek.size();
                index = j;
            } else {
                i  = 0;
                k++;
            }
            j++;
        }

        if (k == 0) {
            dow = daysOfWeek.get(0);
            i = daysOfWeek.size();
        }

        while (i < daysOfWeek.size()) {
            if (calendar.get(Calendar.DAY_OF_WEEK) <= daysOfWeek.get(i)) {
                dow = daysOfWeek.get(i);
                index = i;
                break;
            }
            i++;
        }

        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            if (day == dow) {
                // Variable pembantu
                int dayOfYear = 0;
                // Fungsi jalan kalo size dari arraylist udah maks, bakal ngeset buat arraylist ke-0
                if (daysOfWeek.size() == index+1) {
                    dayOfYear = day - daysOfWeek.get(0);
                    // Fungsi jalan kalo size dari arraylist belom maks
                } else {
                    dayOfYear = day - daysOfWeek.get(index+1);
                    dow = daysOfWeek.get(index+1);
                }
                // Fungsi jalan buat nambah hari dari hari ini / ngeset day of week kalo belom ganti minggu
                if (dayOfYear > 0) {
                    calendar.add(Calendar.DATE, Math.abs(7-dayOfYear));
                } else {
                    calendar.set(Calendar.DAY_OF_WEEK, dow);
                }
                // Fungsi jalan kalo hari ini bukan hari yang di set
            } else {
                int dayOfYear = day - dow;
                // Debug aja sih
                Log.v("DayofYear", String.valueOf(dayOfYear));
                // Fungsi jalan buat nambah hari dari hari ini / ngeset day of week kalo belom ganti minggu
                if (dayOfYear > 0) {
                    calendar.add(Calendar.DATE, Math.abs(7-dayOfYear));
                } else {
                    calendar.set(Calendar.DAY_OF_WEEK, dow);
                }
            }
        } else {
            // Ngecek hari yang di set >= hari ini
            if (dow >= day) {
                calendar.set(Calendar.DAY_OF_WEEK, dow);
            } else {
                int dayOfYear = day - dow;
                calendar.add(Calendar.DATE, Math.abs(7 - dayOfYear));
            }
        }
//        }
//        while (i < daysOfWeek.size()) {
//            if (daysOfWeek.contains(day)) {
//                if (daysOfWeek.get(i).equals(day)) {
//                    if (i+1 == daysOfWeek.size()) {
//                        i=0;
//                        dayOfYear = day - daysOfWeek.get(i);
//                        break;
//                    } else {
//                        i++;
//                        dayOfYear = day - daysOfWeek.get(i);
//                        if (dayOfYear < 0) {
//                            calendar.set(Calendar.DAY_OF_WEEK, daysOfWeek.get(i));
//                        }
//                        break;
//                    }
//                }
//            } else {
//                dayOfYear = day - daysOfWeek.get(i);
//                if (dayOfYear < 0) {
//                    calendar.set(Calendar.DAY_OF_WEEK, daysOfWeek.get(i));
//                }
//                break;
//            }
//            i++;
//        }
        Log.v("Day of Week", String.valueOf(calendar.get(Calendar.DATE)));
        Log.v("Hour of day", String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)));
        Log.v("Minute", String.valueOf(calendar.get(Calendar.MINUTE)));
        Log.v("Calendar millis", String.valueOf(calendar.getTimeInMillis()));
        Log.v("System millis", String.valueOf(System.currentTimeMillis()));
        Log.v("Day", String.valueOf(day));
        // Check we aren't setting it in the past which would trigger it to fire instantly
        Log.v("Tanggal ini", String.valueOf(now));
        Log.v("Alarm set on", String.valueOf(calendar.getTime()));
        Date setDate = calendar.getTime();//new Date(time);
        long time = calendar.getTimeInMillis();
        Log.v("Time set", String.valueOf(time));
        Intent intent2 = new Intent(context, AlarmReceiver.class);
        Bundle b = new Bundle();
        if (chosenRingtone.equals("Default")){
            b.putString("ringtone_alarm", null);
        } else {
            b.putString("ringtone_alarm", chosenRingtone);
        }
        b.putInt("durasi", duration);
        intent2.putExtras(b);

        intent2.putExtra("repeat", repeat);
        intent2.putExtra("duration", duration);
        intent2.putExtra("id2",id2);
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(context, id2, intent2,
                PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent1);
        long diff = setDate.getTime() - now.getTime();
        long sminute = diff / (60 * 1000) % 60;
        long shour = diff / (60 * 60 * 1000) % 24;
        long sday = diff / (60 * 60 * 24 * 1000) % 365;
        Log.v("Alarm", "Your next alarm will be set in " + sday + " day(s), " +
                shour + " hour(s), " + sminute + " minute(s)");
    }
}