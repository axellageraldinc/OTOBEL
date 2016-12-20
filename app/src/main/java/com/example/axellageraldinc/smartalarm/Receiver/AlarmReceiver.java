package com.example.axellageraldinc.smartalarm.Receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.widget.Toast;

import com.example.axellageraldinc.smartalarm.Menu.MenuSetting;
import com.example.axellageraldinc.smartalarm.R;

public class AlarmReceiver extends BroadcastReceiver
{
    private AudioManager myAudioManager;
    Uri uriuri;
    private MediaPlayer mp;

    @Override
    public void onReceive(final Context context, Intent intent)
    {

        /*myAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        Toast.makeText(context, "Alarm! Wake up! Wake up!", Toast.LENGTH_LONG).show();*/
        /*int originalVolume = myAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);*/
        /*myAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, myAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);*/
        /*myAudioManager.adjustVolume(AudioManager.ADJUST_SAME, AudioManager.FLAG_PLAY_SOUND);*/
/*        Intent i = new Intent(context, ShowAlarm.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);*/
        //Log.d("Pilihan ringtone", intent.getStringExtra("ringtone_alarm"));
        /*int maxVolume = myAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);*/
        myAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        /*defaultRinger = myAudioManager.getRingerMode();*/
        /*myAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);*/
        int volume = myAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (volume==0)
        {
            int maxVolume = myAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            myAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, AudioManager.FLAG_PLAY_SOUND);
        }
        else
        {
            myAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, MenuSetting.volume, AudioManager.FLAG_PLAY_SOUND);
        }
        String pasrseUri = intent.getStringExtra("ringtone_alarm");

        if (pasrseUri != null) {
            uriuri = Uri.parse(intent.getStringExtra("ringtone_alarm"));
        }

        //Kalau user gak pilih lagu
        if (uriuri==null)
        {
            //Gak ada lagu yang dipilih
            mp = MediaPlayer.create(context, R.raw.iphone7__2016);
            CountDownTimer c = new CountDownTimer(MenuSetting.durasifix, 1000) {
                @Override
                public void onTick(long l) {
                    mp.start();
                }

                @Override
                public void onFinish() {
                    mp.stop();
                    /*alarmManager.cancel(pendingIntent);
                    Toast.makeText(context, "ALARM OFF", Toast.LENGTH_SHORT).show();*/
                    /*myAudioManager.setRingerMode(defaultRinger);*/
                }
            }; c.start();
        }
        //Kalau user pilih lagu
        else {
            mp = MediaPlayer.create(context, uriuri);
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            CountDownTimer c = new CountDownTimer(MenuSetting.durasifix, 1000) {
                @Override
                public void onTick(long l) {
                    mp.start();
                }

                @Override
                public void onFinish() {
                    mp.stop();
                    /*alarmManager.cancel(pendingIntent);
                    Toast.makeText(context, "ALARM OFF", Toast.LENGTH_SHORT).show();*/
                    /*myAudioManager.setRingerMode(defaultRinger);*/
                }
            };
            c.start();
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



}