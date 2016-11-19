package com.example.axellageraldinc.smartalarm.TambahAlarmBaru;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.axellageraldinc.smartalarm.Database.AlarmModel;
import com.example.axellageraldinc.smartalarm.Receiver.AlarmReceiver;
import com.example.axellageraldinc.smartalarm.Database.DBHelper;
import com.example.axellageraldinc.smartalarm.R;
import com.example.axellageraldinc.smartalarm.RecyclerViewListAlarm.ListActivity;

import java.util.Calendar;

public class SettingAlarm extends AppCompatActivity
{
    TimePicker alarmTimePicker;
    PendingIntent pendingIntent;
    AlarmManager alarmManager;
    Intent intent1;
    public String chosenRingtone;
    String hour, minute;
    DBHelper dbHelper;
    AlertDialog d;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_alarm);
        alarmTimePicker = (TimePicker) findViewById(R.id.timePicker);
        alarmTimePicker.setIs24HourView(true);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        dbHelper = new DBHelper(SettingAlarm.this);


        SetRingtone();

        Button btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Ambil nilai hour dan minute dari timePicker masuk ke variabel hour dan minute
                hour = alarmTimePicker.getCurrentHour().toString();
                minute = alarmTimePicker.getCurrentMinute().toString();
                //Insert nilai-nilai variabel ke database
                SetAlarmOn();
                dbHelper.createAlarm(new AlarmModel(hour, minute, "", "", 1, 0));
                Intent i = new Intent(SettingAlarm.this, ListActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

        Button btnSetDay = (Button) findViewById(R.id.btnSetDay);
        btnSetDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final CharSequence[] items = {" Don't repeat "," Everyday "," Customize "};

                final AlertDialog.Builder b = new AlertDialog.Builder(SettingAlarm.this);
                b.setTitle("Repeat Alarm");
                b.setCancelable(true);
                b.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {

                        switch (item)
                        {
                            case 0:
                                //Kalau klik don't repeat

                                break;
                            case 1:
                                //Kalau klik everyday
                                break;
                            case 2:
                                //Klik kalau customize
                                CustomRepeat();
                                break;
                        }
                    }
                });
                d = b.create();
                d.show();
            }
        });

    }

    public void CustomRepeat() {
        final CharSequence[] items = {" Monday ", " Tuesday ", " Wednesday ", " Thursday ", " Friday ", " Saturday ", " Sunday "};

        final AlertDialog.Builder b = new AlertDialog.Builder(SettingAlarm.this);
        b.setTitle("Customize Day");
        b.setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean isChecked) {
                if (isChecked) {
                    //Kalau user milih hari itu, trus gimana (insert ke database)
                } else {
                    //Kalau item udah ada, remove (mbuh maksute piye)
                }
            }
        });

        // Button OK
        b.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        //Button Cancel
        b.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }

        });
        d = b.create();
        d.show();
    }


    public void SetAlarmOn()
    {
        long time;

        Toast.makeText(SettingAlarm.this, "ALARM ON", Toast.LENGTH_SHORT).show();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getCurrentHour());
        calendar.set(Calendar.MINUTE, alarmTimePicker.getCurrentMinute());
        intent1 = new Intent(this, AlarmReceiver.class);
        Bundle b = new Bundle();
        if (chosenRingtone != null){
            b.putString("ringtone_alarm", chosenRingtone);
        } else {
            b.putString("ringtone_alarm", null);
        }
        intent1.putExtras(b);
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent1, 0);

        time=(calendar.getTimeInMillis()-(calendar.getTimeInMillis()%60000));
        if(System.currentTimeMillis()>time)
        {
            if (calendar.AM_PM == 0)
                time = time + (1000*60*60*12);
            else
                time = time + (1000*60*60*24);
        }
        alarmManager.setRepeating(AlarmManager.RTC, time, 0, pendingIntent);
    }

    /*public void OnToggleClicked(View view)
    {
        long time;
        if (((ToggleButton) view).isChecked())
        {
            Toast.makeText(SettingAlarm.this, "ALARM ON", Toast.LENGTH_SHORT).show();
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getCurrentHour());
            calendar.set(Calendar.MINUTE, alarmTimePicker.getCurrentMinute());
            Log.d("Chosen ringtone", chosenRingtone);
            intent1 = new Intent(this, AlarmReceiver.class);
            Bundle b = new Bundle();
            b.putString("ringtone_alarm", chosenRingtone);
            intent1.putExtras(b);
            pendingIntent = PendingIntent.getBroadcast(this, 0, intent1, 0);

            time=(calendar.getTimeInMillis()-(calendar.getTimeInMillis()%60000));
            if(System.currentTimeMillis()>time)
            {
                if (calendar.AM_PM == 0)
                    time = time + (1000*60*60*12);
                else
                    time = time + (1000*60*60*24);
            }
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, 10000, pendingIntent);
        }
        else
        {
            alarmManager.cancel(pendingIntent);
            Toast.makeText(SettingAlarm.this, "ALARM OFF", Toast.LENGTH_SHORT).show();
        }
    }*/

    public void SetRingtone()
    {
        Button setRingtone = (Button) findViewById(R.id.btnRingtone);
        setRingtone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALL);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
                startActivityForResult(intent, 5);
            }
        });
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent intent)
    {
        if (resultCode == Activity.RESULT_OK && requestCode == 5)
        {
            Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

            if (uri != null)
            {
                chosenRingtone = uri.toString();
                System.out.print(chosenRingtone);
                Log.d("Chosen Ringtone", chosenRingtone);
                //intent.putExtra("ringtone_alarm", chosenRingtone);
            }
            else
            {
                chosenRingtone = null;
               // Log.d("Chosen Ringtone", chosenRingtone);
            }
        }
    }

    public String getChosenRingtone() {
        return chosenRingtone;
    }

}