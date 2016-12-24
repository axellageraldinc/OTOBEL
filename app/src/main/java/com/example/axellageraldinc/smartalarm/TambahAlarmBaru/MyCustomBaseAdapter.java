package com.example.axellageraldinc.smartalarm.TambahAlarmBaru;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.axellageraldinc.smartalarm.R;
import com.example.axellageraldinc.smartalarm.TambahAlarmBaru.ModelSettingAlarm;

import java.util.ArrayList;

/**
 * Created by Axellageraldinc A on 21-Dec-16.
 */

public class MyCustomBaseAdapter extends BaseAdapter {
    public static ArrayList<ModelSettingAlarm> searchArrayList;

    private LayoutInflater mInflater;

    public MyCustomBaseAdapter(Context context, ArrayList<ModelSettingAlarm> results) {
        searchArrayList = results;
        mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return searchArrayList.size();
    }

    public Object getItem(int position) {
        return searchArrayList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.activity_setting_alarm_listview_adapter, null);
            holder = new ViewHolder();
            holder.txtJudul = (TextView) convertView.findViewById(R.id.txtJudul);
            holder.txtSub = (TextView) convertView.findViewById(R.id.txtSub);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtJudul.setText(searchArrayList.get(position).getJudul());
        holder.txtSub.setText(searchArrayList.get(position).getSub());

        return convertView;
    }

    static class ViewHolder {
        TextView txtJudul;
        TextView txtSub;
    }
}