package com.example.axellageraldinc.smartalarm;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.axellageraldinc.smartalarm.Database.DBHelper;

public class ManualAlarm extends Fragment {

    Button btnTest;
    AudioManager am;
    MediaPlayer mp;
    DBHelper dbH;
    int VolumeDB, DefaultVolume;
    Context context;
    MediaMetadataRetriever metaRetriever;

    public ManualAlarm(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_manual_alarm);

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        dbH = new DBHelper(getActivity());
        VolumeDB = dbH.GetVolume();

        am = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        DefaultVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);

        am.setStreamVolume(AudioManager.STREAM_MUSIC, VolumeDB, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);

        View view = inflater.inflate(R.layout.activity_manual_alarm, container, false);

        btnTest = (Button)view.findViewById(R.id.btnTEST);
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Stop(); //Supaya cuma sekali setel aja, gak loop terus terusan
                mp = MediaPlayer.create(getContext(), R.raw.bell_stasiun);
                mp.start();
                am.setStreamVolume(AudioManager.STREAM_MUSIC, DefaultVolume, 0);
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
