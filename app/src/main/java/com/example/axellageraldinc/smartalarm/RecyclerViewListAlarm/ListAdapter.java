package com.example.axellageraldinc.smartalarm.RecyclerViewListAlarm;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
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
    public static int hour, minute;
    SettingAlarm settingAlarm;
    MediaPlayer mp;
    AudioManager am;
    int DefaultVolume;
    Ringtone r;
    String ring;

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
            Button btnPlay = (Button) MyView.findViewById(R.id.btnPlay);

            final String id = String.valueOf(alarmModelList.get(position).getId());
            final String id2 = String.valueOf(alarmModelList.get(position).getID2());
            final String ringtone = alarmModelList.get(position).getRingtone();
            final Uri uri = Uri.parse(ringtone);
            r = RingtoneManager.getRingtone(context, uri);
            txtID.setText(id);
            txtID2.setText(id2);
            String output = String.format("%02d : %02d", alarmModelList.get(position).getHour(), alarmModelList.get(position).getMinute());
            txtShowWaktu.setText(output);
            hour = alarmModelList.get(position).getHour();
            minute = alarmModelList.get(position).getMinute();
            txtSetDay.setText(alarmModelList.get(position).getSet_day());
            String judul = alarmModelList.get(position).getJudul_bel();
            txtJudulAlarm.setText(judul);

            int VolumeDB = dbHelper.GetVolume();
            am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            DefaultVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
            am.setStreamVolume(AudioManager.STREAM_MUSIC, VolumeDB, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
            btnPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "Ringtone : " + ringtone, Toast.LENGTH_SHORT).show();
                    Stop(); //Supaya cuma sekali setel aja, gak loop terus terusan
                    mp = MediaPlayer.create(context, uri);
                    mp.start();
                    am.setStreamVolume(AudioManager.STREAM_MUSIC, DefaultVolume, 0);
                }
            });

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

    public void Stop(){
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }
    }

}
