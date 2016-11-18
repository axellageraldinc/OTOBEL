package com.example.axellageraldinc.smartalarm.RecyclerViewListAlarm;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.axellageraldinc.smartalarm.Database.AlarmModel;
import com.example.axellageraldinc.smartalarm.Database.DBHelper;
import com.example.axellageraldinc.smartalarm.R;

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
    private String hour, minute;


    public AlarmAdapter(Context context, List<AlarmModel> alarmModelList) {
        dbHelper = new DBHelper(context);
        this.context = context;
        this.alarmModelList = alarmModelList;
    }
    @Override
    public AlarmAdapter.AlarmViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        AlarmViewHolder alarmViewHolder = new AlarmViewHolder(context, view, hour, minute);
        return alarmViewHolder;
    }

    @Override
    public void onBindViewHolder(AlarmAdapter.AlarmViewHolder holder, int position) {
        String output = String.format("%02d : %02d", Integer.parseInt(alarmModelList.get(position).getHour()), Integer.parseInt(alarmModelList.get(position).getMinute()));
        holder.txtShowWaktu.setText(output);
        hour = alarmModelList.get(position).getHour(); minute = alarmModelList.get(position).getMinute();
        holder.txtSetDay.setText(alarmModelList.get(position).getSet_day());
        holder.btnSwitch.setChecked(dbHelper.getAlarmStatus(hour, minute));
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
            btnSwitch = (Switch) view.findViewById(R.id.btnSwitch);
            txtShowWaktu = (TextView) view.findViewById(R.id.txtShowWaktu);
            btnSwitch.setOnCheckedChangeListener(this);
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
                Toast.makeText(context, "Status alarm = 0", Toast.LENGTH_SHORT).show();
            } else {
                dbHelper.updateAlarmStatus(1, hour, minute);
                Toast.makeText(context, "Status alarm = 1", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
