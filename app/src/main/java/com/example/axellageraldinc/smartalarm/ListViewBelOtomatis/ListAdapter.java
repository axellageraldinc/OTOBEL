package com.example.axellageraldinc.smartalarm.ListViewBelOtomatis;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.view.menu.MenuView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.axellageraldinc.smartalarm.Database.BelOtomatisModel;
import com.example.axellageraldinc.smartalarm.Database.DBHelper;
import com.example.axellageraldinc.smartalarm.R;
import com.example.axellageraldinc.smartalarm.Receiver.AlarmReceiver;
import com.example.axellageraldinc.smartalarm.TambahBelOtomatis.SettingAlarm;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Axellageraldinc A on 10-Dec-16.
 */

public class ListAdapter extends BaseAdapter {

    private List<BelOtomatisModel> belOtomatisModelList = new ArrayList<>();
    private BelOtomatisModel belOtomatisModel;
    private DBHelper dbHelper;
    private Context context;
    public static int hour, minute;
    private String id, id2, ringtone, output;
    private int selectedButton=-1; // Kalo -1, ga ada yg dipencet
    SettingAlarm settingAlarm;
    MediaPlayer mp;
    AudioManager am;
    int DefaultVolume;
    Ringtone r;
    String ring;

    public ListAdapter(Context context, List<BelOtomatisModel> belOtomatisModelList) {
        dbHelper = new DBHelper(context);
        this.context = context;
        this.belOtomatisModelList = belOtomatisModelList;
    }

    @Override
    public int getCount() {
        return belOtomatisModelList.size();
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
            MyView = inflater.inflate(R.layout.activity_bel_otomatis_view_adapter, viewGroup, false);
            holder = new ItemViewHolder();
            holder.txtShowWaktu = (TextView) MyView.findViewById(R.id.txtShowWaktu);
            holder.txtSetDay = (TextView) MyView.findViewById(R.id.txtSetDay);
            holder.txtJudulAlarm = (TextView)MyView.findViewById(R.id.txtJudulAlarm);
            holder.txtID = (TextView)MyView.findViewById(R.id.txtId);
            holder.txtID2 = (TextView) MyView.findViewById(R.id.txtID2);
            holder.switchAlarmStatus = (Switch) MyView.findViewById(R.id.SwitchAlarmStatus);
            holder.btnPlay = (ImageButton) MyView.findViewById(R.id.btnPlay);
            MyView.setTag(holder);
        } else {
            holder = (ItemViewHolder) MyView.getTag();
        }
        // btnPlay enable kalo ga ada yg dipencet
        holder.btnPlay.setEnabled(selectedButton == -1);
        id = String.valueOf(belOtomatisModelList.get(position).getId());
        id2 = String.valueOf(belOtomatisModelList.get(position).getID2());
        ringtone = belOtomatisModelList.get(position).getRingtone();
        holder.txtID.setText(id);
        holder.txtID2.setText(id2);
        output = String.format("%02d : %02d", belOtomatisModelList.get(position).getHour(), belOtomatisModelList.get(position).getMinute());
        holder.txtShowWaktu.setText(output);
        hour = belOtomatisModelList.get(position).getHour();
        minute = belOtomatisModelList.get(position).getMinute();
        holder.txtSetDay.setText(belOtomatisModelList.get(position).getSet_day());
        String judul = belOtomatisModelList.get(position).getJudul_bel();
        holder.txtJudulAlarm.setText(judul);
        final int duration = belOtomatisModelList.get(position).getAlarm_duration();

        final int VolumeDB = dbHelper.GetVolume();
        am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        DefaultVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        am.setStreamVolume(AudioManager.STREAM_MUSIC, VolumeDB, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        holder.btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Toast.makeText(context, "Ringtone : " + ringtone + "Volume : " + VolumeDB, Toast.LENGTH_SHORT).show();
                //Stop(); //Supaya cuma sekali setel aja, gak loop terus terusan
                ((ImageButton) view).setEnabled(false); // btnPlay yg di pencet di disable
                selectedButton = position; // selectedButton diubah biar button lainnya ga bisa dipencet
                notifyDataSetChanged(); // Ngasih tau adapter kalo btnPlay ga bisa dipencet(disable)
                if (ringtone.equals("Default")){
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
                    final Uri uri = Uri.parse(ringtone);
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
                am.setStreamVolume(AudioManager.STREAM_MUSIC, DefaultVolume, 0);
            }
        });

        int status = belOtomatisModelList.get(position).getStatus();
        if (status==1){
            holder.switchAlarmStatus.setChecked(true);
        }
        else{
            holder.switchAlarmStatus.setChecked(false);
        }

        holder.switchAlarmStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
        TextView txtShowWaktu, txtSetDay, txtJudulAlarm, txtID, txtID2;
        Switch switchAlarmStatus;
        ImageButton btnPlay;
    }

}
