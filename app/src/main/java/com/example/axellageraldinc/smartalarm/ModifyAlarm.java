package com.example.axellageraldinc.smartalarm;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TimePicker;

import com.example.axellageraldinc.smartalarm.Database.DBHelper;

public class ModifyAlarm extends AppCompatActivity {

    // TODO : Ambil data dari database dan di set kesini (Contoh bisa lihat di PencatatKuliah Axell)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_alarm);

    }
}
