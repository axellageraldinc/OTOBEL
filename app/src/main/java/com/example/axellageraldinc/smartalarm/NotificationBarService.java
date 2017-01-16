package com.example.axellageraldinc.smartalarm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;

import com.example.axellageraldinc.smartalarm.Database.DBHelper;

/**
 * Created by Axellageraldinc on 12-Jan-17.
 */

public class NotificationBarService extends BroadcastReceiver {

    private String parseUri;
    private Uri uriuri;
    private MediaPlayer mp;

    @Override
    public void onReceive(Context context, Intent intent) {
        parseUri = intent.getStringExtra("uri");
        if (parseUri!=null){
            uriuri = Uri.parse(intent.getStringExtra("uri"));
            mp = MediaPlayer.create(context, uriuri);
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mp.stop();
        }
    }
}
