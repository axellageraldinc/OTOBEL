package com.example.axellageraldinc.smartalarm.ListViewBelManual;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.axellageraldinc.smartalarm.TambahBelManual.AddManualAlarm;
import com.example.axellageraldinc.smartalarm.Database.BelManualModel;
import com.example.axellageraldinc.smartalarm.Database.DBHelper;
import com.example.axellageraldinc.smartalarm.ModifyBelManual;
import com.example.axellageraldinc.smartalarm.R;

import java.util.ArrayList;
import java.util.List;

public class ManualAlarm extends Fragment {

    Button btnTest, btnAddNew;
    AudioManager am;
    MediaPlayer mp;
    DBHelper dbH;
    int VolumeDB, DefaultVolume;
    Context context;
    MediaMetadataRetriever metaRetriever;
    private ListView listManual;
    private List<BelManualModel> belManualModelList = new ArrayList<>();
    private BelManualModel bmm;
    private ListManualAdapter lma;

    public ManualAlarm(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_bel_manual_view);

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //DefaultVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);

        View view = inflater.inflate(R.layout.activity_bel_otomatis_manual_list_view, container, false);

        dbH = new DBHelper(getActivity());
        belManualModelList = dbH.getAllBelManual();

        listManual = (ListView)view.findViewById(R.id.listView);
        listManual.setEmptyView(view.findViewById(R.id.empty));
        lma = new ListManualAdapter(getContext(), belManualModelList);
        listManual.setAdapter(lma);
        lma.notifyDataSetChanged();

        listManual.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                BelManualModel bmm = belManualModelList.get(position);
                int id = bmm.getId_manual();
                String nama_bel = bmm.getNama_bel_manual();
                String ringtone = bmm.getRingtone_manual();
                int durasi = bmm.getDurasi_manual();
                BelManualModel bmm2 = dbH.getBelManualDetail(id);
                nama_bel = bmm2.getNama_bel_manual();
                ringtone = bmm2.getRingtone_manual();
                durasi = bmm2.getDurasi_manual();
                int durasi_cetak = durasi/1000;

                Intent ii = new Intent(getContext(), ModifyBelManual.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ii.putExtra("id", id);
                ii.putExtra("nama_bel", nama_bel);
                ii.putExtra("ringtone", ringtone);
                ii.putExtra("durasi", durasi);
                startActivity(ii);
            }
        });

        FloatingActionButton btnAddNew = (FloatingActionButton)view.findViewById(R.id.btnAddNew);
        btnAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), AddManualAlarm.class);
                startActivity(i);
            }
        });
        return view;
    }

    public void Stop(){
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }
    }

}
