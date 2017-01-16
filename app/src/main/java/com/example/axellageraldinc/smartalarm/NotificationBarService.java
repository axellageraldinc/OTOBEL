package com.example.axellageraldinc.smartalarm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;

import com.example.axellageraldinc.smartalarm.Database.DBHelper;

/**
 * Created by Axellageraldinc on 12-Jan-17.
 */

public class NotificationBarService extends Service {
    private boolean isRunning;
    private Context context;
    private Thread backgroundThread;
    private DBHelper dbHelper; // Manggil database

    @Nullable
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
                            .setContentTitle("OTOBEL")
                            .setContentText("Service is running");
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
        return super.onStartCommand(intent, flags, startId);
    }
}
