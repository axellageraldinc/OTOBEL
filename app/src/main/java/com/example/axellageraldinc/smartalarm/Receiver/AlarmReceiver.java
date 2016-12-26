package com.example.axellageraldinc.smartalarm.Receiver;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.widget.Toast;

import com.example.axellageraldinc.smartalarm.Database.DBHelper;
import com.example.axellageraldinc.smartalarm.R;

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
        //Pakai duration di setting umum
        //duration = dbH.GetDuration();
        /*BelOtomatisModel a = dbH.getAlarmModel(String.valueOf(SettingAlarm.hourNow), String.valueOf(SettingAlarm.minuteNow));
        duration = a.getAlarm_duration();*/

        /*myAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        Toast.makeText(context, "Alarm! Wake up! Wake up!", Toast.LENGTH_LONG).show();*/
        /*int originalVolume = myAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);*/
        /*myAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, myAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);*/
        /*myAudioManager.adjustVolume(AudioManager.ADJUST_SAME, AudioManager.FLAG_PLAY_SOUND);*/
/*      Intent i = new Intent(context, ShowAlarm.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);*/
        //Log.d("Pilihan ringtone", intent.getStringExtra("ringtone_alarm"));
        /*int maxVolume = myAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);*/
        myAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        /*defaultRinger = myAudioManager.getRingerMode();*/
        /*myAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);*/
        DefaultVolume = myAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        /*if (volume==0)
        {*/
            int maxVolume = myAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            myAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, VolumeDB, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE); //REMOVE SOUND AND VIBRATE BIAR GAK ADA SUARA TUT PAS MAU MULAI ALARM
        Toast.makeText(context, "Volume : " + VolumeDB + " & Duration : " + duration, Toast.LENGTH_SHORT).show();
        //}
        /*else
        {
            myAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, MenuSetting.volume, AudioManager.FLAG_PLAY_SOUND);
        }*/
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
        /*Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        settingAlarm = new SettingAlarm();*/
//        if (uriuri == null)
//        {
//            uriuri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        }
//        Ringtone ringtone = RingtoneManager.getRingtone(context, uriuri);
//        ringtone.play();
    }

    public void OtomatisMati(){

        int start = 0;
        int end = duration;

        Runnable stopPlayerTask = new Runnable(){
            @Override
            public void run() {
                mp.stop();
            }};

        mp.seekTo(start);
        mp.start();

        Handler handler = new Handler();
        handler.postDelayed(stopPlayerTask, end);
        String[] repeatID = dbH.getAlarmID2(id2);
        if (repeatID != null) {
            if (repeatID[0].equals("Don't repeat")) {
                dbH.updateAlarmStatus(Integer.parseInt(repeatID[1]), 0);
                Intent intent2 = new Intent(context, AlarmReceiver.class);
                PendingIntent pendingIntent1 = PendingIntent.getBroadcast(context, id2, intent2,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                pendingIntent1.cancel();
            }
        }

        /*CountDownTimer c = new CountDownTimer(duration*1000, 1000) { //10000 = 10detik (diganti dengan yang di setting nantinya)
            @Override
            public void onTick(long l) {
                mp.start();
            }

            @Override
            public void onFinish() {
                mp.stop();
                myAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, DefaultVolume, 0);
                    *//*alarmManager.cancel(pendingIntent);
                    Toast.makeText(context, "ALARM OFF", Toast.LENGTH_SHORT).show();*//*
                    *//*myAudioManager.setRingerMode(defaultRinger);*//*
            }
        }; c.start();*/
    }

}