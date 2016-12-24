package com.example.axellageraldinc.smartalarm.RecyclerViewListAlarm;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.axellageraldinc.smartalarm.Database.AlarmModel;
import com.example.axellageraldinc.smartalarm.Database.DBHelper;
import com.example.axellageraldinc.smartalarm.R;
import com.example.axellageraldinc.smartalarm.Receiver.AlarmReceiver;
import com.example.axellageraldinc.smartalarm.TambahAlarmBaru.SettingAlarm;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Axellageraldinc A on 10-Dec-16.
 */

public class ListAdapter extends BaseAdapter {

    private List<AlarmModel> alarmModelList = new ArrayList<>();
    private AlarmModel alarmModel;
    private DBHelper dbHelper;
    private Context context;
    public static String hour, minute;
    SettingAlarm settingAlarm;

    public ListAdapter(Context context, List<AlarmModel> alarmModelList) {
        dbHelper = new DBHelper(context);
        this.context = context;
        this.alarmModelList = alarmModelList;
    }

    @Override
    public int getCount() {
        return alarmModelList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View MyView = convertView;
        if (convertView == null) {
         /*we define the view that will display on the grid*/
            //Inflate the layout
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            MyView = inflater.inflate(R.layout.list_item, viewGroup, false);

            TextView txtShowWaktu = (TextView) MyView.findViewById(R.id.txtShowWaktu);
            TextView txtSetDay = (TextView) MyView.findViewById(R.id.txtSetDay);
            TextView txtJudulAlarm = (TextView)MyView.findViewById(R.id.txtJudulAlarm);
            TextView txtID = (TextView)MyView.findViewById(R.id.txtId);
            TextView txtID2 = (TextView) MyView.findViewById(R.id.txtID2);
            Switch switchAlarmStatus = (Switch) MyView.findViewById(R.id.SwitchAlarmStatus);
            final String id = String.valueOf(alarmModelList.get(position).getId());
            final String id2 = String.valueOf(alarmModelList.get(position).getID2());
            txtID.setText(id);
            txtID2.setText(id2);
            String output = String.format("%02d : %02d", Integer.parseInt(alarmModelList.get(position).getHour()), Integer.parseInt(alarmModelList.get(position).getMinute()));
            txtShowWaktu.setText(output);
            hour = alarmModelList.get(position).getHour();
            minute = alarmModelList.get(position).getMinute();
            txtSetDay.setText(alarmModelList.get(position).getSet_day());
            String judul = alarmModelList.get(position).getJudul_bel();
            txtJudulAlarm.setText(judul);

            int status = alarmModelList.get(position).getStatus();
            if (status==1){
                switchAlarmStatus.setChecked(true);
            }
            else{
                switchAlarmStatus.setChecked(false);
            }

            switchAlarmStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b){
                        Toast.makeText(context, "Check : " + id, Toast.LENGTH_SHORT).show();
                        dbHelper.updateAlarmStatus(Integer.parseInt(String.valueOf(id)), 1);
                        //alarm ON
                    }
                    else
                    {
                        Toast.makeText(context, "Uncheck : " + id, Toast.LENGTH_SHORT).show();
                        dbHelper.updateAlarmStatus(Integer.parseInt(String.valueOf(id)), 0);
                        //alarm OFF
                        //Cari id2 berdasar id dari yang di off, lalu pendingIntent id2 disini. pendingintent di cancel
                        Intent intent2 = new Intent(context, AlarmReceiver.class);
                        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(context, Integer.parseInt(id2), intent2,
                                PendingIntent.FLAG_UPDATE_CURRENT);
                        pendingIntent1.cancel();
                    }
                }
            });

        }
        return MyView;
    }

}