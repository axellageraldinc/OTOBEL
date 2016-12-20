package com.example.axellageraldinc.smartalarm.RecyclerViewListAlarm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.axellageraldinc.smartalarm.Database.AlarmModel;
import com.example.axellageraldinc.smartalarm.Database.DBHelper;
import com.example.axellageraldinc.smartalarm.R;
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
            String output = String.format("%02d : %02d", Integer.parseInt(alarmModelList.get(position).getHour()), Integer.parseInt(alarmModelList.get(position).getMinute()));
            txtShowWaktu.setText(output);
            hour = alarmModelList.get(position).getHour();
            minute = alarmModelList.get(position).getMinute();
            txtSetDay.setText(alarmModelList.get(position).getSet_day());

        }
        return MyView;
    }
}
