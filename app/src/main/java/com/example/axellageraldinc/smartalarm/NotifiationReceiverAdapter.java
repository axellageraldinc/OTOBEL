package com.example.axellageraldinc.smartalarm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.axellageraldinc.smartalarm.Database.DBHelper;
import com.example.axellageraldinc.smartalarm.Database.KalimatNotifBarModel;
import com.example.axellageraldinc.smartalarm.ListViewBelManual.ListManualAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Axellageraldinc on 12-Jan-17.
 */

public class NotifiationReceiverAdapter extends BaseAdapter {
    private DBHelper dbHelper;
    private Context context;
    private List<KalimatNotifBarModel> kmm = new ArrayList<>();

    public NotifiationReceiverAdapter(Context context, List<KalimatNotifBarModel> kmm) {
        dbHelper = new DBHelper(context);
        this.context = context;
        this.kmm = kmm;
    }

    @Override
    public int getCount() {
        return kmm.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ItemViewHolder holder = null;
        View MyView = convertView;
        if (convertView==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            MyView = inflater.inflate(R.layout.activity_notification_receiver_adapter, viewGroup, false);
            holder = new ItemViewHolder();

            holder.txtKalimat = (TextView)MyView.findViewById(R.id.txtBelSudahBunyi);
            MyView.setTag(holder);
        }
        else {
            holder = (NotifiationReceiverAdapter.ItemViewHolder)MyView.getTag();
        }
        holder.txtKalimat.setText(kmm.get(position).getKalimat());
        return MyView;
    }

    private static class ItemViewHolder {
        TextView txtKalimat;
    }

}
