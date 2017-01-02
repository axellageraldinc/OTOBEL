package com.example.axellageraldinc.smartalarm.Receiver;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.axellageraldinc.smartalarm.BackgroundService;

/**
 * @author Nilanchala
 *         <p/>
 *         Broadcast reciever, starts when the device gets starts.
 *         Start your repeating alarm here.
 */

//Untuk start service setiap device reboot
public class DeviceBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
//
//            /* Setting the alarm here */
//            Intent alarmIntent = new Intent(context, AlarmReceiver.class);
//            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
//
//            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//            int interval = 8000;
//            manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
//
//            Toast.makeText(context, "Alarm Set", Toast.LENGTH_SHORT).show();

            // Ganti bagian ini (setelah booting jalanin class BackgroundService
            Intent mServiceIntent = new Intent(context, BackgroundService.class);
            ComponentName service = context.startService(mServiceIntent);
            if (null == service) {
                Toast.makeText(context, "Alarm gagal di-set", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "Alarm set", Toast.LENGTH_LONG).show();
            }
        }
    }
}