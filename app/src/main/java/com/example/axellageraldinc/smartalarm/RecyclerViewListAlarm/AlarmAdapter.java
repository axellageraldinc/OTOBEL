package com.example.axellageraldinc.smartalarm.RecyclerViewListAlarm;

import android.app.AlarmManager;
import android.content.Context;
import android.database.DataSetObserver;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.axellageraldinc.smartalarm.Database.AlarmModel;
import com.example.axellageraldinc.smartalarm.Database.DBHelper;
import com.example.axellageraldinc.smartalarm.R;
import com.example.axellageraldinc.smartalarm.TambahAlarmBaru.SettingAlarm;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by almanalfaruq on 16/11/2016.
 */

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder> {

    private List<AlarmModel> alarmModelList = new ArrayList<>();
    private AlarmModel alarmModel;
    private DBHelper dbHelper;
    private Context context;
    public static String hour, minute;
    SettingAlarm settingAlarm;


    public AlarmAdapter(Context context, List<AlarmModel> alarmModelList) {
        dbHelper = new DBHelper(context);
        this.context = context;
        this.alarmModelList = alarmModelList;
    }
    @Override
    public AlarmAdapter.AlarmViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        AlarmViewHolder alarmViewHolder = new AlarmViewHolder(context, view, hour, minute);
        settingAlarm = new SettingAlarm();
        return alarmViewHolder;
    }

    @Override
    public void onBindViewHolder(AlarmAdapter.AlarmViewHolder holder, int position) {
        String output = String.format("%02d : %02d", Integer.parseInt(alarmModelList.get(position).getHour()), Integer.parseInt(alarmModelList.get(position).getMinute()));
        holder.txtShowWaktu.setText(output);
        hour = alarmModelList.get(position).getHour();
        minute = alarmModelList.get(position).getMinute();
        holder.txtSetDay.setText(alarmModelList.get(position).getSet_day());
    }

    @Override
    public int getItemCount() {
        return alarmModelList.size();
    }

    public static class AlarmViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, CompoundButton.OnCheckedChangeListener {

        TextView txtHour, txtMinute, txtId, txtSetDay, txtShowWaktu;
        Switch btnSwitch;
        DBHelper dbHelper;
        String hour, minute;
        Context context;

        public AlarmViewHolder(Context context, View view, String hour, String minute) {
            super(view);
            this.hour = hour;
            this.minute = minute;
            this.context = context;
            txtId = (TextView) view.findViewById(R.id.txtId);
            txtSetDay = (TextView) view.findViewById(R.id.txtSetDay);
            txtShowWaktu = (TextView) view.findViewById(R.id.txtShowWaktu);
            dbHelper = new DBHelper(context);
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {

        }

        // Todo : matiin sama hidupin alarm
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (!b) {
                dbHelper.updateAlarmStatus(0, hour, minute);
                /*SettingAlarm.alarmManager.cancel(SettingAlarm.pendingIntent);
                btnSwitch.setChecked(false);*/
            } else {
                dbHelper.updateAlarmStatus(1, hour, minute);
                /*SettingAlarm.alarmManager.setRepeating(AlarmManager.RTC, SettingAlarm.time, 0, SettingAlarm.pendingIntent);
                btnSwitch.setChecked(true);*/
            }
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
