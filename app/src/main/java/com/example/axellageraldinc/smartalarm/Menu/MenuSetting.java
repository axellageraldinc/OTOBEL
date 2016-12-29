package com.example.axellageraldinc.smartalarm.Menu;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.axellageraldinc.smartalarm.Database.DBHelper;
import com.example.axellageraldinc.smartalarm.R;

public class MenuSetting extends AppCompatActivity {

    ListView listMenu;
    AudioManager myAudioManager;
    public static int volume, VolumeDB;
    ActionBar actionBar;
    public static String durasi=null;
    public static int durasifix=10000;
    private DBHelper dbH;
    private MediaPlayer mp;
    public static int maxVolume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_setting);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        //actionBar.setBackgroundDrawable(new ColorDrawable(Color.GRAY));
        actionBar.setTitle("Setting");

        dbH = new DBHelper(MenuSetting.this);

        myAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        listMenu = (ListView) findViewById(R.id.listMenuSetting);
        String[] values = new String[] { "Volume"};

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
                /*else if (position==1)
                {
                    //Show builder editText durasinya
                    //Lihat di bagian set hari alarm di setting alarm
                    SetDurasi();
                }*/
            }
        });

    }

    public void SetDurasi()
    {
        int duration = dbH.GetDuration();
        Log.d("Duration: ", String.valueOf(duration));
        durasi = String.valueOf(duration);
        final Dialog d = new Dialog(MenuSetting.this);
        d.setTitle("INPUT DURASI BEL");
        d.setContentView(R.layout.input_box_number);

        final EditText txtInput = (EditText)d.findViewById(R.id.txtInput);
        txtInput.setText(durasi);
        txtInput.setSelection(txtInput.getText().length());

        Button OK = (Button) d.findViewById(R.id.btnOK);
        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                durasi = txtInput.getText().toString();
                if(durasi!=null)
                {
                    durasifix = Integer.parseInt(durasi)*1000;
                    dbH.InsertDuration(durasifix/1000);
                }
                else if (durasi==null)
                {
                    durasifix=10000;
                    dbH.InsertDuration(durasifix/1000);
                }
                Toast.makeText(MenuSetting.this, "The duration is " + durasi + " seconds", Toast.LENGTH_LONG).show();
                d.dismiss();
            }
        });
        d.show();
    }

    //Setting volume dari seekbar
    public void ShowSetVolume()
    {
        final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
        final SeekBar seek = new SeekBar(this);
        maxVolume = myAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int curVolume = myAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        popDialog.setTitle("Set Alarm Volume");
        popDialog.setView(seek);

        seek.setMax(maxVolume);

        VolumeDB = dbH.GetVolume();
        seek.setProgress(VolumeDB);
        //Set default volume

        //volume=8;

        volume=VolumeDB;

        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                volume = progress;
                //myAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
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
                        dbH.InsertVolume(volume);
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

    public static int getVolume() {
        return volume;
    }
}
