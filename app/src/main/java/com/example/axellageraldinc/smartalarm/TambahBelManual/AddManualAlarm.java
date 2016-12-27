package com.example.axellageraldinc.smartalarm.TambahBelManual;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.axellageraldinc.smartalarm.Database.BelManualModel;
import com.example.axellageraldinc.smartalarm.Database.DBHelper;
import com.example.axellageraldinc.smartalarm.HomeScreen;
import com.example.axellageraldinc.smartalarm.R;
import com.example.axellageraldinc.smartalarm.TambahBelOtomatis.ModelSettingAlarm;
import com.example.axellageraldinc.smartalarm.TambahBelOtomatis.MyCustomBaseAdapter;

import java.util.ArrayList;

public class AddManualAlarm extends AppCompatActivity {

    public static AlarmManager alarmManager;
    public String chosenRingtone;
    DBHelper dbHelper;
    AlertDialog d;
    ArrayList<ModelSettingAlarm> results;
    public static int durasifix=10000, jumlah_waktu, duration;
    public static String durasi;
    ModelSettingAlarm fullObject, sr;
    MyCustomBaseAdapter adapter;
    ListView lv;
    private Uri uri;
    private String repeat, title, JudulBel;
    long idItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bel_manual);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.JudulAddManual));
        actionBar.setIcon(R.drawable.logoooo);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        dbHelper = new DBHelper(AddManualAlarm.this);

        ArrayList<ModelSettingAlarm> searchResults = GetSearchResults();

        lv = (ListView) findViewById(R.id.listView);
        adapter = new MyCustomBaseAdapter(this, searchResults);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Object o = lv.getItemAtPosition(position);
                idItem = lv.getItemIdAtPosition(position);
                fullObject = (ModelSettingAlarm) o;

                switch((int) idItem){
                    case 0:
                        JudulBel();
                        break;
                    case 1:
                        SetRingtone();
                        break;
                    case 2:
                        SetDuration();
                        dbHelper.InsertDuration(duration);
                        break;
                }

            }
        });

        Button btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (chosenRingtone==null || JudulBel==null){
                    Toast.makeText(AddManualAlarm.this, "Mohon beri judul bel dan pilih ringtone", Toast.LENGTH_LONG).show();
                }
                else{
                    dbHelper.createBelManual(new BelManualModel(JudulBel, chosenRingtone, duration*1000));
                    Intent i = new Intent(AddManualAlarm.this, HomeScreen.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
            }
        });
        Button btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent home = new Intent(AddManualAlarm.this, HomeScreen.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(home);
            }
        });
    }

    public void JudulBel(){
        final Dialog d = new Dialog(AddManualAlarm.this);
        d.setTitle("INPUT NAMA BEL");
        d.setContentView(R.layout.input_box);

        final EditText txtInput = (EditText)d.findViewById(R.id.txtInput);
        txtInput.setSelection(txtInput.getText().length());

        Button OK = (Button) d.findViewById(R.id.btnOK);
        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JudulBel = txtInput.getText().toString();
                fullObject.setSub(JudulBel);
                adapter.notifyDataSetChanged();
                d.dismiss();
            }
        });
        d.show();
    }

    public void SetDuration(){
        final CharSequence[] items = {" Pakai Settingan Global ", " Set khusus alarm ini "};

        final AlertDialog.Builder b = new AlertDialog.Builder(AddManualAlarm.this);
        b.setTitle("Durasi Alarm");
        b.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                switch (item)
                {
                    case 0:
                        //Kalau pakai settingan global
                        duration=dbHelper.GetDuration();
                        break;
                    case 1:
                        //Kalau pakai settingan ini sendiri
                        SetDurasiNew();
                        dialog.dismiss();
                        break;
                }
            }
        });
        // Button OK
        b.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        fullObject.setSub(duration + " detik");
                        adapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });

        //Button Cancel
        b.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        d = b.create();
        d.show();
    }

    public void SetDurasiNew(){
        final Dialog d = new Dialog(AddManualAlarm.this);
        d.setTitle("INPUT DURASI BEL");
        d.setContentView(R.layout.input_box_number);

        final EditText txtInput = (EditText)d.findViewById(R.id.txtInput);
        txtInput.setSelection(txtInput.getText().length());

        Button OK = (Button) d.findViewById(R.id.btnOK);
        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                durasi = txtInput.getText().toString();
                if(durasi!=null)
                {
                    durasifix = Integer.parseInt(durasi)*1000;
                }
                else if (durasi==null)
                {
                    durasifix=10000;
                }
                duration = durasifix/1000;
                fullObject.setSub(duration + " detik");
                adapter.notifyDataSetChanged();
                d.dismiss();
            }
        });
        d.show();
    }

    public void SetRingtone()
    {
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALL);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
        startActivityForResult(intent, 5);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent intent)
    {
        if (resultCode == Activity.RESULT_OK && requestCode == 5)
        {
            uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

            if (uri != null)
            {
                chosenRingtone = uri.toString();
                Ringtone ringtone = RingtoneManager.getRingtone(this, uri);
                title = ringtone.getTitle(this);
                fullObject.setSub(title);
                adapter.notifyDataSetChanged();
            }
            else
            {
                uri = Uri.parse("android.resource://com.example.axellageraldinc.smartalarm.TambahAlarmBaru/raw/iphone7__2016");
                chosenRingtone = uri.toString();
            }
        }
    }

    public String getChosenRingtone() {
        return chosenRingtone;
    }

    private ArrayList<ModelSettingAlarm> GetSearchResults(){
        results = new ArrayList<ModelSettingAlarm>();

        sr = new ModelSettingAlarm();
        sr.setJudul("Nama Bel");
        sr.setSub("Belum di-set");
        results.add(sr);

        sr = new ModelSettingAlarm();
        sr.setJudul("Ringtone");
        sr.setSub("Default");
        results.add(sr);

        duration = dbHelper.GetDuration();
        sr = new ModelSettingAlarm();
        sr.setJudul("Durasi Bel");
        sr.setSub(duration + " detik");
        results.add(sr);

        return results;
    }
}
