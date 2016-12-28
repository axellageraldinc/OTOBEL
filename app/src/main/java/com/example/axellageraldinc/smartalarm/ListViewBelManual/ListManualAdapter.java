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
    private int selectedButton=-1; // Kalo -1, ga ada yg dipencet
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
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        ItemViewHolder holder = null;
        View MyView = convertView;
        if (convertView == null) {
         /*we define the view that will display on the grid*/
            //Inflate the layout
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            MyView = inflater.inflate(R.layout.activity_bel_manual_view_adapter, viewGroup, false);
            holder = new ItemViewHolder();

            holder.txtID = (TextView) MyView.findViewById(R.id.txtId);
            holder.txtJudul = (TextView) MyView.findViewById(R.id.txtJudul);
            holder.btnPlay = (ImageButton) MyView.findViewById(R.id.btnPlay);
            holder.txtRingtone = (TextView) MyView.findViewById(R.id.txtRingtone);
            MyView.setTag(holder);
        }
        else {
            holder = (ItemViewHolder)MyView.getTag();
        }
        holder.btnPlay.setEnabled(selectedButton == -1);

        String id = String.valueOf(belManualModelList.get(position).getId_manual());
        String judul = belManualModelList.get(position).getNama_bel_manual();
        final int duration = belManualModelList.get(position).getDurasi_manual();

        final int VolumeDB = dbHelper.GetVolume();
        am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        am.setStreamVolume(AudioManager.STREAM_MUSIC, VolumeDB, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);

        holder.txtID.setText(String.valueOf(belManualModelList.get(position).getId_manual()));
        holder.txtJudul.setText(belManualModelList.get(position).getNama_bel_manual());
        holder.txtRingtone.setText(belManualModelList.get(position).getRingtone_manual());
        final TextView txtRingtone = holder.txtRingtone;
        holder.btnPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    Toast.makeText(context, "Ringtone : " + txtRingtone.getText().toString() + " Volume : " + VolumeDB, Toast.LENGTH_SHORT).show();
                    //Stop();
                    ((ImageButton) view).setEnabled(false); // btnPlay yg di pencet di disable
                    selectedButton = position; // selectedButton diubah biar button lainnya ga bisa dipencet
                    notifyDataSetChanged(); // Ngasih tau adapter kalo btnPlay ga bisa dipencet(disable)
                    if (txtRingtone.getText().toString().equals("Default") || txtRingtone.getText().toString() == null
                            || txtRingtone.getText().toString().equals("")){
                        mp = MediaPlayer.create(context, R.raw.iphone7__2016);
                        int start = 0;
                        int end = duration;

                        Runnable stopPlayerTask = new Runnable(){
                            @Override
                            public void run() {
                                mp.stop();
                                ((ImageButton) view).setEnabled(true); // btnPlay yg di pencet di enable lagi
                                selectedButton = -1; // selectedButton diubar biar button lainnya bisa dipencet
                                notifyDataSetChanged(); // Ngasih tau adapter kalo btnPlay bisa dipencet lagi (enable)
                            }};

                        mp.seekTo(start);
                        mp.start();

                        Handler handler = new Handler();
                        handler.postDelayed(stopPlayerTask, end);
                    }
                    else{
                        final Uri uri = Uri.parse(txtRingtone.getText().toString());
                        mp = MediaPlayer.create(context, uri);
                        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        int start = 0;
                        int end = duration;

                        Runnable stopPlayerTask = new Runnable(){
                            @Override
                            public void run() {
                                mp.stop();
                                ((ImageButton) view).setEnabled(true);
                                selectedButton = -1;
                                notifyDataSetChanged();
                            }};

                        mp.seekTo(start);
                        mp.start();

                        Handler handler = new Handler();
                        handler.postDelayed(stopPlayerTask, end);
                    }
                    mp.start();
                }
            });
        return MyView;
    }

    public void Stop(){
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }
    }

    private static class ItemViewHolder {
        TextView txtID, txtJudul, txtRingtone;
        ImageButton btnPlay;
    }
}
