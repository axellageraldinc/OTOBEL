package com.example.axellageraldinc.smartalarm.ListViewBelOtomatis;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.axellageraldinc.smartalarm.Database.BelOtomatisModel;
import com.example.axellageraldinc.smartalarm.Database.DBHelper;
import com.example.axellageraldinc.smartalarm.ModifyAlarm;
import com.example.axellageraldinc.smartalarm.R;
import com.example.axellageraldinc.smartalarm.TambahBelOtomatis.SettingAlarm;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends Fragment {

    private RecyclerView recView;
    private LinearLayoutManager layoutManager;
    private ListAdapter alarmAdapter;
    private DBHelper dbHelper;
    private List<BelOtomatisModel> belOtomatisModelList = new ArrayList<>();
    private FloatingActionButton btnAddNew;
    ActionBar actionBar;
    private ListView listView;
    private TextView txtJudulAlarm;
    private int jam, menit;
    private int id;

    public ListActivity(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_bel_otomatis_view);

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.activity_bel_otomatis_manual_list_view, container, false);
        // set the icon
        //actionBar.setIcon(R.drawable.ico_actionbar);

        dbHelper = new DBHelper(getActivity());
        belOtomatisModelList.addAll(dbHelper.getAllAlarm());
        listView = (ListView) view.findViewById(R.id.listView);
        listView.setEmptyView(view.findViewById(R.id.empty));
        alarmAdapter = new ListAdapter(getContext(), belOtomatisModelList);
        listView.setAdapter(alarmAdapter);
        alarmAdapter.notifyDataSetChanged();

        txtJudulAlarm = (TextView)view.findViewById(R.id.txtJudulAlarm);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                BelOtomatisModel belOtomatisModel = belOtomatisModelList.get(position);
                id = belOtomatisModel.getId();
                BelOtomatisModel a = dbHelper.getAlarmModel(id);
                jam = a.getHour();
                menit = a.getMinute();
                String repeat = a.getSet_day();
                String JudulBel = a.getJudul_bel();
                String ringtone = a.getRingtone();
                int duration = a.getAlarm_duration();
                int durasi = duration/1000;
                int ID2 = a.getID2();
                int status = a.getStatus();

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

                // Ganti ini biar bisa cancel
                startActivityForResult(ii, 10);

                Toast.makeText(getContext(), "Klik di list : " + id, Toast.LENGTH_LONG).show();
            }
        });

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

    @Override
    public void onResume() {
        super.onResume();
        alarmAdapter.notifyDataSetChanged();
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

    // Kalo di modify langsung ke refresh
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 10) {
            Log.v("Fragment result", "refresh adapter");
            belOtomatisModelList.clear();
            belOtomatisModelList.addAll(dbHelper.getAllAlarm());
            alarmAdapter.notifyDataSetChanged();
        }
    }
}
