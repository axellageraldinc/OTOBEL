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
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.axellageraldinc.smartalarm.R;

public class MenuSetting extends AppCompatActivity {

    ListView listMenu;
    AudioManager myAudioManager;
    public static int volume = 10;
    ActionBar actionBar;
    public static String durasi;
    public static int durasifix;

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
        String[] values = new String[] { "Volume", "Manual turn off alarm", "Song duration"};

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
                else if (position==1)
                {
                    //Show builder on / off (switch)
                    //BELUM PASTI, DARI ADIN INI PEMIKIRANNYA
                    //Kalau on, nanti dibuat notification di notif bar, kalau di klik pergi ke class utk turn off
                }
                else if (position==2)
                {
                    //Show builder editText durasinya
                    //Lihat di bagian set hari alarm di setting alarm
                    SetDurasi();
                }

            }
        });

    }

    public void SetDurasi()
    {
        durasi=null;
        final AlertDialog.Builder setDurasi = new AlertDialog.Builder(this);
        final EditText set = new EditText(this);
        set.setText(durasi);
        set.setInputType(InputType.TYPE_CLASS_NUMBER);
        set.isFocused();
        set.setTextSize(20);
        setDurasi.setTitle("Alarm duration in seconds");
        setDurasi.setView(set);

        // Button OK
        setDurasi.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        durasi = set.getText().toString();
                        if(durasi!=null)
                        {
                            durasifix = Integer.parseInt(durasi)*1000;
                        }
                        else if (durasi==null)
                        {
                            durasifix=10000;
                        }
                        Toast.makeText(MenuSetting.this, "The duration is " + durasi + " seconds", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                });

        //Button Cancel
        setDurasi.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertShow = setDurasi.create();
        alertShow.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        alertShow.show();
        alertShow.getWindow().setLayout(950,600);
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
