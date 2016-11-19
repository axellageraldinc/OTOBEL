package com.example.axellageraldinc.smartalarm;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.axellageraldinc.smartalarm.Database.AlarmModel;
import com.example.axellageraldinc.smartalarm.Database.DBHelper;
import com.example.axellageraldinc.smartalarm.RecyclerViewListAlarm.ListActivity;

public class ModifyAlarm extends AppCompatActivity {
    private DBHelper dbHelper;
    private TimePicker tp;
    private Button btnSave, btnDelete;
    public static String hourModify, menitModify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_alarm);

        dbHelper = new DBHelper(this);

        tp = (TimePicker) findViewById(R.id.timePicker);
        tp.setIs24HourView(true);

        Intent i = getIntent();
        hourModify = i.getStringExtra("jam");
        menitModify = i.getStringExtra("menit");

        //Set di timepicker tanggal sesuai yang diklik
        tp.setCurrentHour(Integer.parseInt(hourModify));
        tp.setCurrentMinute(Integer.parseInt(menitModify));

        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Update data ke database
                UpdateData();
            }
        });

        btnDelete = (Button) findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Hapus alarm yang dipilih
                DeleteData();
            }
        });
    }

    // TODO: UPDATE ALARM YANG JAM NYA JUGA KE UPDATE KE ALARM
    // KARENA METHOD DI BAWAH INI CUMA KE UPDATE UI NYA AJA
    // +++ UPDATE RINGTONE DLL
    public void UpdateData()
    {
        final AlertDialog.Builder d = new AlertDialog.Builder(this);
        d.setCancelable(true);
        final TextView t = new TextView(this);
        t.setText("Are you sure to save this?");
        d.setTitle("Are you sure?");
        d.setView(t);

        d.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                String hourEdited = tp.getCurrentHour().toString();
                String minuteEdited = tp.getCurrentMinute().toString();
                dbHelper.updateAlarm(hourEdited, minuteEdited);
                dialog.dismiss();
                ReturnHome();
            }
        });

        d.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        d.create();
        d.show();
    }

    public void DeleteData()
    {
        final AlertDialog.Builder d = new AlertDialog.Builder(this);
        d.setCancelable(true);
        final TextView t = new TextView(this);
        t.setText("Are you sure to delete this alarm?");
        d.setTitle("Are you sure?");
        d.setView(t);

        d.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dbHelper.deleteAlarm(hourModify, menitModify);
                dialog.dismiss();
                ReturnHome();
            }
        });

        d.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        d.create();
        d.show();
    }

    public void ReturnHome()
    {
        Intent home = new Intent(ModifyAlarm.this, ListActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(home);
    }

}
