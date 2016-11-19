package com.example.axellageraldinc.smartalarm.RecyclerViewListAlarm;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.axellageraldinc.smartalarm.Database.AlarmModel;
import com.example.axellageraldinc.smartalarm.Database.DBHelper;
import com.example.axellageraldinc.smartalarm.Menu.MenuSetting;
import com.example.axellageraldinc.smartalarm.ModifyAlarm;
import com.example.axellageraldinc.smartalarm.R;
import com.example.axellageraldinc.smartalarm.TambahAlarmBaru.SettingAlarm;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {

//    private DummyDataAdapter adapter;
//    private Switch btnSwitch;
    private RecyclerView recView;
    private LinearLayoutManager layoutManager;
    private AlarmAdapter alarmAdapter;
    private DBHelper dbHelper;
    private List<AlarmModel> alarmModelList = new ArrayList<>();
    private FloatingActionButton btnAddNew;
    private Switch btnSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        recView = (RecyclerView) findViewById(R.id.recView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recView.setItemAnimator(new DefaultItemAnimator());
        recView.setLayoutManager(mLayoutManager);

        initRecyclerView();

//        btnSwitch = (Switch) findViewById(R.id.btnSwitch);
//        btnSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                if (b) {
//
//                }
//            }
//        });
        dbHelper = new DBHelper(ListActivity.this);
        alarmModelList = dbHelper.getAllAlarm();
        alarmAdapter = new AlarmAdapter(ListActivity.this, alarmModelList);
        recView.setAdapter(alarmAdapter);
        alarmAdapter.notifyDataSetChanged();

//        adapter = new DummyDataAdapter(DummyData.getListData(), this);
//        recView.setAdapter(adapter);

        btnAddNew = (FloatingActionButton) findViewById(R.id.btnAddNew);
        btnAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ListActivity.this, SettingAlarm.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

    }

    //Apply menu supaya ada titik 3 di kanan atas
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.MenuSetting:
                Intent i = new Intent(ListActivity.this, MenuSetting.class);
                startActivity(i);
                return true;
            case R.id.MenuAbout:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Todo : off in alarm pake switch
    private void initRecyclerView(){
        recView.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), recView, new RecyclerItemClickListener.ClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        AlarmModel alarmModel = alarmModelList.get(position);
                        String waktu = alarmModel.getHour() + ":" + alarmModel.getMinute();
                        String id = String.valueOf(alarmModel.getId());

//                        Toast.makeText(ListActivity.this, "Klik di " + id, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }
/*
                    @Override public void onItemClick(View view, int position) {
                        // TODO Handle item click
                    }*/
                })
        );
    }
}
