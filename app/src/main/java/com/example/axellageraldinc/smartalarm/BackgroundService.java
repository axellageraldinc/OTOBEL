package com.example.axellageraldinc.smartalarm;

/**
 * Created by Axellageraldinc A on 25-Dec-16.
 */

import android.app.*;
import android.content.*;
import android.database.Cursor;
import android.os.*;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import com.example.axellageraldinc.smartalarm.Database.DBHelper;
import com.example.axellageraldinc.smartalarm.Receiver.AlarmReceiver;
import com.example.axellageraldinc.smartalarm.TambahBelOtomatis.SettingAlarm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class BackgroundService extends Service {

    private boolean isRunning;
    private Context context;
    private Thread backgroundThread;
    private DBHelper dbHelper;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        this.context = this;
        this.isRunning = false;
        this.dbHelper = new DBHelper(this);
        this.backgroundThread = new Thread(myTask);

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
            android.support.v4.app.NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("Bel Sekolah Well")
                            .setContentText("Jalan cuy");
            Intent resultIntent = new Intent(context, HomeScreen.class);
// Because clicking the notification opens a new ("special") activity, there's
// no need to create an artificial back stack.
            mBuilder.setOngoing(true);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
            // Sets an ID for the notification
            int mNotificationId = 001;
// Gets an instance of the NotificationManager service
            NotificationManager mNotifyMgr =
                    (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
// Builds the notification and issues it.
            mNotifyMgr.notify(mNotificationId, mBuilder.build());
        }
        Toast.makeText(this, "Service running", Toast.LENGTH_LONG).show();
        loadAlarmFromDB();
//        return START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }

    private void loadAlarmFromDB() {
        Cursor cursor = dbHelper.cursorAlarm();
        if (cursor.moveToFirst()) {
            do {
                int id2 = cursor.getInt(cursor.getColumnIndex(DBHelper.ID2));
                int date = cursor.getInt(cursor.getColumnIndex(DBHelper.order_alarm));
                String repeat = cursor.getString(cursor.getColumnIndex(DBHelper.SETDAY_ALARM));
                int hour = cursor.getInt(cursor.getColumnIndex(DBHelper.HOUR_ALARM));
                int minute = cursor.getInt(cursor.getColumnIndex(DBHelper.MINUTE_ALARM));
                activateAlarm(id2, date, repeat, hour, minute);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private void activateAlarm(int id2, int date, String repeat, int hour, int minute) {
        ArrayList<String> stRepeat = (ArrayList<String>) Arrays.asList(repeat.split("\\s*,\\s*"));
        ArrayList<Integer> intRepeat = SettingAlarm.getIntDaysOfWeek(stRepeat);
        Intent intent = new Intent(BackgroundService.this, AlarmReceiver.class);

        PendingIntent pi = PendingIntent.getBroadcast(BackgroundService.this, id2, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        long time = (long) date;
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (repeat.equals("Don't repeat")) {
            am.set(AlarmManager.RTC_WAKEUP, time, pi);
        } else {
            if (repeat.equals("Everyday")) {
                for (int i=1;i<8;i++) {
                    setRepeatAlarm(i, hour, minute, am, pi);
                }
            } else if (repeat.equals("Weekday")) {
                for (int i=2;i<7;i++) {
                    setRepeatAlarm(i, hour, minute, am, pi);
                }
            } else if (repeat.equals("Weekend")) {
                setRepeatAlarm(1, hour, minute, am, pi);
                setRepeatAlarm(7, hour, minute, am, pi);
            } else {
                int list;
                for (int a=0;a<intRepeat.size();a++) {
                    list = intRepeat.get(a);
                    setRepeatAlarm(list, hour, minute, am, pi);
                }
            }
        }

    }

    private void setRepeatAlarm(int daysOfWeek, int hour, int minute, AlarmManager am, PendingIntent pi) {
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
        long time=(calendar.getTimeInMillis()-(calendar.getTimeInMillis()%60000));
        if(System.currentTimeMillis()>time)
        {
            if (calendar.AM_PM == 0)
                time = time + (1000*60*60*12);
            else
                time = time + (1000*60*60*24);
        }
        am.setRepeating(AlarmManager.RTC_WAKEUP, time, 0, pi);
    }

}