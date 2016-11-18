package com.example.axellageraldinc.smartalarm.RecyclerViewListAlarm;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.example.axellageraldinc.smartalarm.Database.AlarmModel;
import com.example.axellageraldinc.smartalarm.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by almanalfaruq on 16/11/2016.
 */

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder> {

    private List<AlarmModel> alarmModelList = new ArrayList<>();

    public AlarmAdapter(List<AlarmModel> alarmModelList) {
        this.alarmModelList = alarmModelList;
    }
    @Override
    public AlarmAdapter.AlarmViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        AlarmViewHolder alarmViewHolder = new AlarmViewHolder(view);
        return alarmViewHolder;
    }

    @Override
    public void onBindViewHolder(AlarmAdapter.AlarmViewHolder holder, int position) {
        String output = String.format("%02d : %02d", Integer.parseInt(alarmModelList.get(position).getHour()), Integer.parseInt(alarmModelList.get(position).getMinute()));
        holder.txtShowWaktu.setText(output);
        /*holder.txtHour.setText(alarmModelList.get(position).getHour());
        holder.txtMinute.setText(alarmModelList.get(position).getMinute());*/
//        holder.txtId.setText(alarmModelList.get(position).getId());
        holder.txtSetDay.setText(alarmModelList.get(position).getSet_day());
    }

    @Override
    public int getItemCount() {
        return alarmModelList.size();
    }

    public static class AlarmViewHolder extends RecyclerView.ViewHolder {

        TextView txtHour, txtMinute, txtId, txtSetDay, txtShowWaktu;
        Switch btnSwitch;

        public AlarmViewHolder(View view) {
            super(view);
/*
            txtHour = (TextView) view.findViewById(R.id.txtHour);
            txtMinute = (TextView) view.findViewById(R.id.txtMinute);*/
            txtId = (TextView) view.findViewById(R.id.txtId);
            txtSetDay = (TextView) view.findViewById(R.id.txtSetDay);
            btnSwitch = (Switch) view.findViewById(R.id.btnSwitch);
            txtShowWaktu = (TextView) view.findViewById(R.id.txtShowWaktu);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
