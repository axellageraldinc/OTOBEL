package com.example.axellageraldinc.smartalarm.ListViewBelManual;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.axellageraldinc.smartalarm.Database.BelManualModel;
import com.example.axellageraldinc.smartalarm.Database.DBHelper;
import com.example.axellageraldinc.smartalarm.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Axellageraldinc A on 25-Dec-16.
 */

public class ListManualAdapter extends BaseAdapter {

    private List<BelManualModel> belManualModelList = new ArrayList<>();
    private BelManualModel bmm;
    private DBHelper dbHelper;
    private Context context;
    private MediaPlayer mp;
    AudioManager am;
    int DefaultVolume;
    Ringtone r;
    String ring;

    public ListManualAdapter(Context context, List<BelManualModel> belManualModelList) {
        dbHelper = new DBHelper(context);
        this.context = context;
        this.belManualModelList = belManualModelList;
    }

    @Override
    public int getCount() {
        return belManualModelList.size();
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
    public View getView(int position, View view, ViewGroup viewGroup) {
        View MyView = view;
        if (view == null) {
         /*we define the view that will display on the grid*/
            //Inflate the layout
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            MyView = inflater.inflate(R.layout.activity_bel_manual_view_adapter, viewGroup, false);

            TextView txtID = (TextView)MyView.findViewById(R.id.txtId);
            TextView txtJudul = (TextView)MyView.findViewById(R.id.txtJudul);
            ImageButton btnPlay = (ImageButton)MyView.findViewById(R.id.btnPlay);

            final String id = String.valueOf(belManualModelList.get(position).getId_manual());
            final String ringtone = belManualModelList.get(position).getRingtone_manual();
            final String judul = belManualModelList.get(position).getNama_bel_manual();
            final int duration = belManualModelList.get(position).getDurasi_manual();

            final int VolumeDB = dbHelper.GetVolume();
            am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            am.setStreamVolume(AudioManager.STREAM_MUSIC, VolumeDB, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);

            txtID.setText(id);
            txtJudul.setText(judul);

            btnPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "Ringtone : " + ringtone + " Volume : " + VolumeDB, Toast.LENGTH_SHORT).show();
                    //Stop();
                    if (ringtone.equals("Default")){
                        mp = MediaPlayer.create(context, R.raw.iphone7__2016);
                        int start = 0;
                        int end = duration;

                        Runnable stopPlayerTask = new Runnable(){
                            @Override
                            public void run() {
                                mp.stop();
                            }};

                        mp.seekTo(start);
                        mp.start();

                        Handler handler = new Handler();
                        handler.postDelayed(stopPlayerTask, end);
                    }
                    else{
                        final Uri uri = Uri.parse(ringtone);
                        mp = MediaPlayer.create(context, uri);
                        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        int start = 0;
                        int end = duration;

                        Runnable stopPlayerTask = new Runnable(){
                            @Override
                            public void run() {
                                mp.stop();
                            }};

                        mp.seekTo(start);
                        mp.start();

                        Handler handler = new Handler();
                        handler.postDelayed(stopPlayerTask, end);
                    }
                    mp.start();
                }
            });

        }
        return MyView;
    }

    public void Stop(){
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }
    }

}
