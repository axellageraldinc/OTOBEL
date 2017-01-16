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
        int dayOfYear = 1;
        int i = 0;
//        while (i < daysOfWeek.size()) {
//            if (daysOfWeek.contains(day)) {
//                if (daysOfWeek.get(i) == day) {
//                    if (i+1 == daysOfWeek.size()) {
//                        calendar.set(Calendar.DAY_OF_WEEK, daysOfWeek.get(0));
//                        dayOfYear = day - daysOfWeek.get(0);
//                        break;
//                    } else {
//                        calendar.set(Calendar.DAY_OF_WEEK, daysOfWeek.get(i+1));
//                        dayOfYear = day - daysOfWeek.get(i+1);
//                        break;
//                    }
//                }
//            } else {
//                calendar.set(Calendar.DAY_OF_WEEK, daysOfWeek.get(i));
//                dayOfYear = day - daysOfWeek.get(i);
//                break;
//            }
//            i++;
//        }
//        calendar.set(Calendar.HOUR_OF_DAY, hour);
//        calendar.set(Calendar.MINUTE, minute);
//        calendar.set(Calendar.SECOND, 0);
//        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
//            if (Objects.equals(daysOfWeek.get(0), day)) {
//                calendar.add(Calendar.DAY_OF_YEAR, 7);
//            } else {
//                calendar.add(Calendar.DAY_OF_YEAR, 8-dayOfYear);
//            }
//        }
        while (i < daysOfWeek.size()) {
            if (daysOfWeek.contains(day)) {
                if (daysOfWeek.get(i).equals(day)) {
                    if (i+1 == daysOfWeek.size()) {
                        dayOfYear = day - daysOfWeek.get(0);
                        i=0;
                        break;
                    } else {
                        dayOfYear = day - daysOfWeek.get(i+1);
                        i++;
                        if (dayOfYear < 0) {
                            calendar.set(Calendar.DAY_OF_WEEK, daysOfWeek.get(i));
                        }
                        break;
                    }
                }
            } else {
                //calendar.set(Calendar.DATE, daysOfWeek.get(i));
                dayOfYear = day - daysOfWeek.get(i);
                if (dayOfYear < 0) {
                    calendar.set(Calendar.DAY_OF_WEEK, daysOfWeek.get(i));
                }
                break;
            }
            i++;
        }
        String dow = "";
        int x =0;
        while (x<daysOfWeek.size()) {
            dow += daysOfWeek.get(x);
            Log.v("per Day of Week database", String.valueOf(daysOfWeek.get(x)));
            x++;
        }
        Log.v("Day of Week database", dow);
        Log.v("Day of Week", String.valueOf(calendar.get(Calendar.DATE)));
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        Log.v("Hour of day", String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)));
        calendar.set(Calendar.MINUTE, minute);
        Log.v("Minute", String.valueOf(calendar.get(Calendar.MINUTE)));
        calendar.set(Calendar.SECOND, 0);
        Log.v("Calendar millis", String.valueOf(calendar.getTimeInMillis()));
        Log.v("System millis", String.valueOf(System.currentTimeMillis()));
        Log.v("Day", String.valueOf(day));
        Log.v("DOY", String.valueOf(dayOfYear));
        // Check we aren't setting it in the past which would trigger it to fire instantly
        Log.v("calendar now", String.valueOf(calendar.getTime()));
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            if (day.equals(daysOfWeek.get(i))) {
                calendar.add(Calendar.DATE, 7);
            }
            else {
                if (dayOfYear > 0) {
                    calendar.add(Calendar.DATE, Math.abs(7-dayOfYear));
                }
            }
        }
        Log.v("calendar setelah ditambah", String.valueOf(calendar.getTime()));
        long timeInMillis = calendar.getTimeInMillis();
        Intent intent2 = new Intent(context, AlarmReceiver.class);
        Bundle bundle = new Bundle();
        if (chosenRingtone.equals("Default")){
            bundle.putString("ringtone_alarm", null);
        } else {
            bundle.putString("ringtone_alarm", chosenRingtone);
        }
        bundle.putInt("durasi", duration);
        intent2.putExtras(bundle);

        intent2.putExtra("repeat", repeat);
        intent2.putExtra("duration", duration);
        intent2.putExtra("id2",id2);
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(context, id2, intent2,
                PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent1);
        Date setDate = calendar.getTime();
        long diff = setDate.getTime() - now.getTime();
        long sminute = diff / (60 * 1000) % 60;
        long shour = diff / (60 * 60 * 1000) % 24;
        long sday = diff / (60 * 60 * 24 * 1000) % 365;
        /*Toast.makeText(context, "Your next alarm will be set in " + sday + " day(s), " +
                shour + " hour(s), " + sminute + " minute(s)", Toast.LENGTH_LONG).show();*/
        Log.v("Alarm", "Your next alarm will be set in " + sday + " day(s), " +
                shour + " hour(s), " + sminute + " minute(s)");
    }
}