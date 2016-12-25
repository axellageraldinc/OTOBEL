package com.example.axellageraldinc.smartalarm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.example.axellageraldinc.smartalarm.TambahAlarmBaru.SettingAlarm;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by Axellageraldinc A on 24-Dec-16.
 */

public class CustomRepeat extends AppCompatActivity {

    private Button btnOK;
    private CheckBox chkSenin, chkSelasa, chkRabu, chkKamis, chkJumat, chkSabtu, chkMinggu;
    private static final int[] DAY_ORDER = {
            Calendar.SUNDAY,
            Calendar.MONDAY,
            Calendar.TUESDAY,
            Calendar.WEDNESDAY,
            Calendar.THURSDAY,
            Calendar.FRIDAY,
            Calendar.SATURDAY
    };
    private ArrayList<Integer> daysOfWeek = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_repeat);
        findView();
        chkMinggu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) daysOfWeek.add(1);
                else if (!b && daysOfWeek.contains(1)) daysOfWeek.remove(Integer.valueOf(1));
            }
        });
        chkSenin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) daysOfWeek.add(2);
                else if (!b && daysOfWeek.contains(2)) daysOfWeek.remove(Integer.valueOf(2));
            }
        });
        chkSelasa.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) daysOfWeek.add(3);
                else if (!b && daysOfWeek.contains(3)) daysOfWeek.remove(Integer.valueOf(3));
            }
        });
        chkRabu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) daysOfWeek.add(4);
                else if (!b && daysOfWeek.contains(4)) daysOfWeek.remove(Integer.valueOf(4));
            }
        });
        chkKamis.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) daysOfWeek.add(5);
                else if (!b && daysOfWeek.contains(5)) daysOfWeek.remove(Integer.valueOf(5));
            }
        });
        chkJumat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) daysOfWeek.add(6);
                else if (!b && daysOfWeek.contains(6)) daysOfWeek.remove(Integer.valueOf(6));
            }
        });
        chkSabtu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) daysOfWeek.add(6);
                else if (!b && daysOfWeek.contains(6)) daysOfWeek.remove(Integer.valueOf(6));
            }
        });
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                Collections.sort(daysOfWeek);
                intent.putIntegerArrayListExtra("daysOfWeek", daysOfWeek);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }

    private void findView() {
        btnOK = (Button) findViewById(R.id.btnOK);
        chkSenin = (CheckBox) findViewById(R.id.chkSenin);
        chkSelasa = (CheckBox) findViewById(R.id.chkSelasa);
        chkRabu = (CheckBox) findViewById(R.id.chkRabu);
        chkKamis = (CheckBox) findViewById(R.id.chkKamis);
        chkJumat = (CheckBox) findViewById(R.id.chkJumat);
        chkSabtu = (CheckBox) findViewById(R.id.chkSabtu);
        chkMinggu = (CheckBox) findViewById(R.id.chkMinggu);
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }
}
