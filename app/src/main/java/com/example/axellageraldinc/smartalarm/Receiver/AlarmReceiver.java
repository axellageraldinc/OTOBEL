package com.example.axellageraldinc.smartalarm.Receiver;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.axellageraldinc.smartalarm.Database.BelOtomatisModel;
import com.example.axellageraldinc.smartalarm.Database.DBHelper;
import com.example.axellageraldinc.smartalarm.Database.KalimatNotifBarModel;
import com.example.axellageraldinc.smartalarm.NotificationBarService;
import com.example.axellageraldinc.smartalarm.NotificationReceiver;
import com.example.axellageraldinc.smartalarm.R;
import com.example.axellageraldinc.smartalarm.TambahBelOtomatis.SettingAlarm;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

public class AlarmReceiver extends BroadcastReceiver
{
    private AudioManager myAudioManager;
    Uri uriuri;
    public static MediaPlayer mp;
    private int DefaultVolume, VolumeDB, duration, id2, hour, minute, hourModify, minuteModify;
    private DBHelper dbH;
    private Context context;
    private String pasrseUri;
    private NotificationManager mNotifyMgr;
    final static String GROUP_KEY_BEL = "group_key_bel";
    private int mNotificationId=0, i=0;
    private String JamMenit, dateString;
    private String kalimat;
    private long date;
    private int color;

    @Override
    public void onReceive(final Context context, Intent intent)
    {

        /*Intent background = new Intent(context, BackgroundService.class);
        context.startService(background);*/

        dbH = new DBHelper(context);
        this.context = context;
        VolumeDB = dbH.GetVolume();
        duration = intent.getIntExtra("durasi", 0);
        id2 = intent.getIntExtra("id2", 0);
        hour = intent.getIntExtra("jam", 0);
        minute = intent.getIntExtra("menit", 0);
        if (hour==0){
            hour = dbH.GetHour(id2);
        }
        if (minute==0){
            minute = dbH.GetMinute(id2);
        }

        myAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        DefaultVolume = myAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            int maxVolume = myAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            myAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, VolumeDB, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE); //REMOVE SOUND AND VIBRATE BIAR GAK ADA SUARA TUT PAS MAU MULAI ALARM
        //Toast.makeText(context, "Volume : " + VolumeDB + "\nDuration : " + duration + "\nVolumeDefault : " + DefaultVolume, Toast.LENGTH_SHORT).show();
        pasrseUri = intent.getStringExtra("ringtone_alarm");

        if (pasrseUri != null) {
            uriuri = Uri.parse(intent.getStringExtra("ringtone_alarm"));
            }
            //OTOMATIS MATI
            //Kalau user gak pilih lagu
            if (uriuri==null)
            {
                //Gak ada lagu yang dipilih
                mp = MediaPlayer.create(context, R.raw.iphone7__2016);
                //ShowNotification();
                OtomatisMati();
                myAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, DefaultVolume, 0);
            }
            //Kalau user pilih lagu
            else {
                mp = MediaPlayer.create(context, uriuri);
                mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                JamMenit = String.format("%02d : %02d", hour, minute);
                //mNotificationId=mNotificationId+1;
                ShowNotification();
                //i++;
                OtomatisMati();
                //mNotifyMgr.cancelAll();
                myAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, DefaultVolume, 0);
            }
    }

    public void ShowNotification(){
        int color = context.getResources().getColor(R.color.colorPrimary);
        date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd-MMM-yyyy");
        dateString = sdf.format(date);
        kalimat= "Klik untuk melihat bel yang sudah bunyi";
        dbH.KalimatNotifBar(new KalimatNotifBarModel(kalimat));
        Intent resultIntent = new Intent(context, NotificationReceiver.class);
        resultIntent.putExtra("uri", pasrseUri);
        android.support.v4.app.NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.ic_stat_social_notifications_on)
                            .setColor(color)
                            .setContentTitle("OTOBEL")
                            .setContentText("Klik untuk melihat bel yang sudah bunyi")
                            .setGroup(GROUP_KEY_BEL)
                            .setAutoCancel(true);

// Because clicking the notification opens a new ("special") activity, there's
// no need to create an artificial back stack.
            //mBuilder.setOngoing(true);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context,  (int) System.currentTimeMillis()
                , resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
            // Sets an ID for the notification
            //mNotificationId = id2;
// Gets an instance of the NotificationManager service
        mNotifyMgr = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
// Builds the notification and issues it.
        mNotifyMgr.notify(1, mBuilder.build());
    }

    /*public void ShowStackNotification(){
        kalimat= "Bel " + JamMenit + " sudah berbunyi";
        dbH.KalimatNotifBar(new KalimatNotifBarModel(kalimat));
        KalimatNotifBarModel km = dbH.GetKalimatNotifBar();
        String kalimatShow = km.getKalimat();
        Notification summaryNotification =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("OTOBEL")
                        .setStyle(new NotificationCompat.InboxStyle()
                                .addLine("jancuk"))
                        .setGroup(GROUP_KEY_BEL)
                        .setGroupSummary(true)
                        .build();
        //mNotificationId = id2;
        mNotifyMgr =
                (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationId, summaryNotification);
    }*/

    public void OtomatisMati(){

        int start = 0;
        int end = duration;

        final Handler handler = new Handler();

        final Runnable updateAlarm = new Runnable() {
            @Override
            public void run() {
                String[] repeatID = dbH.getAlarmID2(id2);
                BelOtomatisModel belOtomatisModel = dbH.getOneAlarmID2(id2);
                if (repeatID != null) {
                    if (!repeatID[0].equals("Don't repeat") || !repeatID[0].equals("Everyday")) {
                        if (belOtomatisModel.getStatus() == 1) {
                            onAlarm(context, belOtomatisModel);
                        }
                    }
                }
            }
        };

        Runnable stopPlayerTask = new Runnable(){
            @Override
            public void run() {
                mp.stop();
                String[] repeatID = dbH.getAlarmID2(id2);
                BelOtomatisModel belOtomatisModel = dbH.getOneAlarmID2(id2);
                if (repeatID != null) {
                    if (repeatID[0].equals("Don't repeat")) {
                        dbH.updateAlarmStatus(Integer.parseInt(repeatID[1]), 0);
                        offAlarm();
                    }
                }
                handler.postDelayed(updateAlarm, 3*1000);
            }};

        mp.seekTo(start);
        mp.start();

        handler.postDelayed(stopPlayerTask, end);

    }

    private void offAlarm() {
        Intent intent2 = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(context, id2, intent2,
                PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        pendingIntent1.cancel();
        alarmManager.cancel(pendingIntent1);
    }

    private void onAlarm(Context context, BelOtomatisModel belOtomatisModel) {
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        Integer day = calendar.get(Calendar.DAY_OF_WEEK);
        ArrayList<String> stRepeat = new ArrayList<String>();
        stRepeat.addAll(Arrays.asList(belOtomatisModel.getSet_day().split("\\s*,\\s*")));
        ArrayList<Integer> daysOfWeek = SettingAlarm.getIntDaysOfWeek(stRepeat);
        int dayOfYear = 1;
        int i = 0;
        while (i < daysOfWeek.size()) {
            if (daysOfWeek.contains(day)) {
                if (daysOfWeek.get(i).equals(day)) {
                    if (i+1 == daysOfWeek.size()) {
                        i=0;
                        dayOfYear = day - daysOfWeek.get(i);
                        break;
                    } else {
                        i++;
                        dayOfYear = day - daysOfWeek.get(i);
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
        calendar.set(Calendar.HOUR_OF_DAY, belOtomatisModel.getHour());
        Log.v("Hour of day", String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)));
        calendar.set(Calendar.MINUTE, belOtomatisModel.getMinute());
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
        Date setDate = calendar.getTime();//new Date(time);
        long time = calendar.getTimeInMillis();
        Log.v("Time set", String.valueOf(time));
        Intent intent2 = new Intent(context, AlarmReceiver.class);
        Bundle b = new Bundle();
        if (belOtomatisModel.getRingtone().equals("Default")){
            b.putString("ringtone_alarm", null);
        } else {
            b.putString("ringtone_alarm", belOtomatisModel.getRingtone());
        }
        b.putInt("durasi", belOtomatisModel.getAlarm_duration());
        intent2.putExtras(b);

        intent2.putExtra("repeat", belOtomatisModel.getSet_day());
        intent2.putExtra("duration", belOtomatisModel.getAlarm_duration());
        intent2.putExtra("id2",id2);
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(context, id2, intent2,
                PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent1);
        long diff = setDate.getTime() - now.getTime();
        long minute = diff / (60 * 1000) % 60;
        long hour = diff / (60 * 60 * 1000) % 24;
        long sday = diff / (60 * 60 * 24 * 1000) % 365;
        Toast.makeText(context, "Your next alarm will be set in " + sday + " day(s), " +
                hour + " hour(s), " + minute + " minute(s)", Toast.LENGTH_LONG).show();
        Log.v("Alarm", "Your next alarm will be set in " + sday + " day(s), " +
                hour + " hour(s), " + minute + " minute(s)");
    }

}