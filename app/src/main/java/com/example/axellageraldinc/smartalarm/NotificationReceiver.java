package com.example.axellageraldinc.smartalarm;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.example.axellageraldinc.smartalarm.Database.DBHelper;
import com.example.axellageraldinc.smartalarm.Database.KalimatNotifBarModel;
import com.example.axellageraldinc.smartalarm.Menu.MenuSetting;
import com.example.axellageraldinc.smartalarm.TambahBelOtomatis.SettingAlarm;

import java.util.ArrayList;
import java.util.List;

public class NotificationReceiver extends AppCompatActivity {
    private ListView list;
    private DBHelper dbHelper;
    private NotifiationReceiverAdapter nra;
    private List<KalimatNotifBarModel> kmm = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_receiver);

        ActionBar ab = getSupportActionBar();
        ab.setTitle("Daftar bel yang sudah bunyi");

        list = (ListView) findViewById(R.id.list);
        dbHelper = new DBHelper(NotificationReceiver.this);
        kmm.addAll(dbHelper.GetKalimatNotifBar());
        list.setEmptyView(findViewById(R.id.empty));
        nra = new NotifiationReceiverAdapter(getApplicationContext(), kmm);
        list.setAdapter(nra);
        nra.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        nra.notifyDataSetChanged();
    }

    //Apply menu supaya ada titik 3 di kanan atas
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.MenuDelete:
                //Delete All list
                dbHelper.DeleteKalimatNotifBar();
                finish();
                startActivity(getIntent().addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
