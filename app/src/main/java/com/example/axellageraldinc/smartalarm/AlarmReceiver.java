package com.example.axellageraldinc.smartalarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.widget.Toast;

import com.example.axellageraldinc.smartalarm.TambahAlarmBaru.SettingAlarm;

public class AlarmReceiver extends BroadcastReceiver
{
    SettingAlarm settingAlarm;
    private AudioManager myAudioManager;

    @Override
    public void onReceive(Context context, Intent intent)
    {

        myAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        Toast.makeText(context, "Alarm! Wake up! Wake up!", Toast.LENGTH_LONG).show();
        myAudioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
        myAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
/*        Intent i = new Intent(context, ShowAlarm.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);*/
        //Log.d("Pilihan ringtone", intent.getStringExtra("ringtone_alarm"));
        Uri uriuri = Uri.parse(intent.getStringExtra("ringtone_alarm"));
        MediaPlayer mp = MediaPlayer.create(context, uriuri);
        mp.start();
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