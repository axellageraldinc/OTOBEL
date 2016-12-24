package com.example.axellageraldinc.smartalarm.RecyclerViewListAlarm;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.axellageraldinc.smartalarm.Database.AlarmModel;
import com.example.axellageraldinc.smartalarm.Database.DBHelper;
import com.example.axellageraldinc.smartalarm.ModifyAlarm;
import com.example.axellageraldinc.smartalarm.R;
import com.example.axellageraldinc.smartalarm.TambahAlarmBaru.SettingAlarm;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends Fragment {

    private RecyclerView recView;
    private LinearLayoutManager layoutManager;
    private ListAdapter alarmAdapter;
    private DBHelper dbHelper;
    private List<AlarmModel> alarmModelList = new ArrayList<>();
    private FloatingActionButton btnAddNew;
    ActionBar actionBar;
    private ListView listView;
    private TextView txtJudulAlarm;
    private String jam, menit;
    private int id;

    public ListActivity(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_list);

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.activity_list, container, false);
        // set the icon
        //actionBar.setIcon(R.drawable.ico_actionbar);

        dbHelper = new DBHelper(getActivity());
        alarmModelList = dbHelper.getAllAlarm();
        listView = (ListView) view.findViewById(R.id.listView);
        listView.setEmptyView(view.findViewById(R.id.empty));
        alarmAdapter = new ListAdapter(getContext(), alarmModelList);
        listView.setAdapter(alarmAdapter);
        alarmAdapter.notifyDataSetChanged();

        txtJudulAlarm = (TextView)view.findViewById(R.id.txtJudulAlarm);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                AlarmModel alarmModel = alarmModelList.get(position);
                String waktu = alarmModel.getHour() + ":" + alarmModel.getMinute();
                jam = alarmModel.getHour();
                menit = alarmModel.getMinute();
                id = alarmModel.getId();
                String repeat = alarmModel.getSet_day();
                String JudulBel = alarmModel.getJudul_bel();
                String ringtone = alarmModel.getRingtone();
                int duration = alarmModel.getAlarm_duration();
                int ID2 = alarmModel.getID2();
                int status = alarmModel.getStatus();
                AlarmModel a = dbHelper.getAlarmModel(id);
                jam = a.getHour();
                menit = a.getMinute();
                repeat = a.getSet_day();
                JudulBel = a.getJudul_bel();
                ringtone = a.getRingtone();
                duration = a.getAlarm_duration();
                int durasi = duration/1000;
                ID2 = a.getID2();
                status = a.getStatus();

                //ShowBox();

                Intent ii = new Intent(getContext(), ModifyAlarm.class);
                ii.putExtra("ID", id);
                ii.putExtra("jam", jam);
                ii.putExtra("menit", menit);
                ii.putExtra("repeat", repeat);
                ii.putExtra("judul_bel", JudulBel);
                ii.putExtra("ringtone", ringtone);
                ii.putExtra("duration", durasi);
                ii.putExtra("ID2", ID2);
                ii.putExtra("status", status);

                //Gak pergi ke class ModifyAlarm
                startActivity(ii);

                Toast.makeText(getContext(), "Klik di list : " + id, Toast.LENGTH_LONG).show();
            }
        });
        /*alarmAdapter = new AlarmAdapter(ListActivity.this, alarmModelList);
        recView.setAdapter(alarmAdapter);
        alarmAdapter.notifyDataSetChanged();*/

        btnAddNew = (FloatingActionButton) view.findViewById(R.id.btnAddNew);
        btnAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), SettingAlarm.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });
        return view;
    }

    public void ShowBox(){
        final AlertDialog.Builder d = new AlertDialog.Builder(getContext());
        d.setCancelable(true);
        d.setTitle("Apakah anda yakin ingin hapus bel ini?");

        d.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dbHelper.deleteAlarm(id);
                dialog.dismiss();
            }
        });

        d.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        d.create();
        d.show();
    }

}
