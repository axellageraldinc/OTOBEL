package com.example.axellageraldinc.smartalarm.Menu;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.axellageraldinc.smartalarm.R;

public class MenuSetting extends AppCompatActivity {

    ListView listMenu;
    AudioManager myAudioManager;
    public static int volume = 10;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_setting);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.GRAY));
        actionBar.setTitle("Setting");

        myAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        listMenu = (ListView) findViewById(R.id.listMenuSetting);
        String[] values = new String[] { "Set Volume"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MenuSetting.this, R.layout.activity_menu_setting_adapter, R.id.txtJudul, values);
        listMenu.setAdapter(adapter);

        listMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int position = i;
                String itemValue = (String) listMenu.getItemAtPosition(position);

                if (position==0)
                {
                    //Show builder set volume
                    ShowSetVolume();
                }

            }
        });

    }

    //Setting volume dari seekbar
    public void ShowSetVolume()
    {
        final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
        final SeekBar seek = new SeekBar(this);
        seek.setMax(300);

        popDialog.setTitle("Set Alarm Volume");
        popDialog.setView(seek);

        int maxVolume = myAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int curVolume = myAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        seek.setMax(maxVolume);
        //Set default volume
        seek.setProgress(10);

        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                myAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                volume = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(getApplicationContext(), "Volume: " + Integer.toString(volume), Toast.LENGTH_SHORT).show();
            }
        });

        // Button OK
        popDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        //Button Cancel
        popDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        popDialog.create();
        popDialog.show();
    }

}
