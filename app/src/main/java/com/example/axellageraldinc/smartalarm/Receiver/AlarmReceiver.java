package com.example.axellageraldinc.smartalarm.Receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.axellageraldinc.smartalarm.Database.BelOtomatisModel;
import com.example.axellageraldinc.smartalarm.Database.DBHelper;
import com.example.axellageraldinc.smartalarm.R;
import com.example.axellageraldinc.smartalarm.TambahBelOtomatis.SettingAlarm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class AlarmReceiver extends BroadcastReceiver
{
    private AudioManager myAudioManager;
    Uri uriuri;
    private MediaPlayer mp;
    private int DefaultVolume, VolumeDB, duration, id2;
    private DBHelper dbH;
    private Context context;

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

        myAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        DefaultVolume = myAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            int maxVolume = myAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            myAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, VolumeDB, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE); //REMOVE SOUND AND VIBRATE BIAR GAK ADA SUARA TUT PAS MAU MULAI ALARM
        //Toast.makeText(context, "Volume : " + VolumeDB + "\nDuration : " + duration + "\nVolumeDefault : " + DefaultVolume, Toast.LENGTH_SHORT).show();
        String pasrseUri = intent.getStringExtra("ringtone_alarm");

        if (pasrseUri != null) {
            uriuri = Uri.parse(intent.getStringExtra("ringtone_alarm"));
            }
            //OTOMATIS MATI
            //Kalau user gak pilih lagu
            if (uriuri==null)
            {
                //Gak ada lagu yang dipilih
                mp = MediaPlayer.create(context, R.raw.iphone7__2016);
                OtomatisMati();
                myAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, DefaultVolume, 0);
            }
            //Kalau user pilih lagu
            else {
                mp = MediaPlayer.create(context, uriuri);
                mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                OtomatisMati();
                myAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, DefaultVolume, 0);
            }
    }

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
                handler.postDelayed(updateAlarm, 60*1000);
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
                        calendar.set(Calendar.DAY_OF_WEEK, daysOfWeek.get(0));
                        dayOfYear = day - daysOfWeek.get(0);
                        break;
                    } else {
                        calendar.set(Calendar.DAY_OF_WEEK, daysOfWeek.get(i+1));
                        dayOfYear = day - daysOfWeek.get(i+1);
                        break;
                    }
                }
            } else {
                calendar.set(Calendar.DAY_OF_WEEK, daysOfWeek.get(i));
                dayOfYear = day - daysOfWeek.get(i);
                break;
            }
            i++;
        }
        Log.v("Day of Week", String.valueOf(calendar.get(Calendar.DAY_OF_WEEK)));
        calendar.set(Calendar.HOUR_OF_DAY, belOtomatisModel.getHour());
        Log.v("Hour of day", String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)));
        calendar.set(Calendar.MINUTE, belOtomatisModel.getMinute());
        Log.v("Minute", String.valueOf(calendar.get(Calendar.MINUTE)));
        calendar.set(Calendar.SECOND, 0);
        Log.v("Calendar millis", String.valueOf(calendar.getTimeInMillis()));
        Log.v("System millis", String.valueOf(System.currentTimeMillis()));
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            if (Objects.equals(daysOfWeek.get(0), day)) {
                calendar.add(Calendar.DAY_OF_YEAR, 7);
            } else {
                calendar.add(Calendar.DAY_OF_YEAR, 8-dayOfYear);
            }
        }
        Date setDate = calendar.getTime();
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