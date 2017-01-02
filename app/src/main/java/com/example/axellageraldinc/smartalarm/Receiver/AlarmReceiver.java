package com.example.axellageraldinc.smartalarm.Receiver;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.example.axellageraldinc.smartalarm.Database.DBHelper;
import com.example.axellageraldinc.smartalarm.HomeScreen;
import com.example.axellageraldinc.smartalarm.ListViewBelOtomatis.ListActivity;
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
                // Biar adapter ke refresh setelah alarm mati
                /*Fragment belOtomatis = new HomeScreen().getFragment(0);
                if (belOtomatis != null) {
                    ((ListActivity) belOtomatis).refreshAdapter();
                }*/
            }
        }
    }

}