package com.example.axellageraldinc.smartalarm.TambahAlarmBaru;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.axellageraldinc.smartalarm.Database.AlarmModel;
import com.example.axellageraldinc.smartalarm.HomeScreen;
import com.example.axellageraldinc.smartalarm.Menu.MenuSetting;
import com.example.axellageraldinc.smartalarm.Receiver.AlarmReceiver;
import com.example.axellageraldinc.smartalarm.Database.DBHelper;
import com.example.axellageraldinc.smartalarm.R;

import java.util.ArrayList;
import java.util.Calendar;

public class SettingAlarm extends AppCompatActivity
{
    TimePicker alarmTimePicker;
    public static PendingIntent pendingIntent1;
    public static AlarmManager alarmManager;
    Intent intent1;
    public String chosenRingtone;
    String hour, minute;
    DBHelper dbHelper;
    AlertDialog d;
    ArrayList<ModelSettingAlarm> results;
    public static long time;
    ActionBar actionBar;
    int mHour, mMinute;
    ListView listViewSet;
    TextView txtRepeat;
    public static int hourNow, minuteNow, off_method;
    public static int durasifix=10000, jumlah_waktu, duration;
    public static String durasi;
    ModelSettingAlarm fullObject, sr;
    MyCustomBaseAdapter adapter;
    ListView lv;
    private Uri uri;
    private String repeat, title, JudulBel;
    long idItem;
    public static int id2=0, selected;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_alarm_listview);
        actionBar = getSupportActionBar();
        actionBar.setTitle("ADD ALARM");

        alarmTimePicker = (TimePicker) findViewById(R.id.timePicker);
        alarmTimePicker.setIs24HourView(true);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        dbHelper = new DBHelper(SettingAlarm.this);

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
                        Repeat();
                        break;
                    case 1:
                        SetRingtone();
                        break;
                    case 2:
                        SetDuration();
                        dbHelper.InsertDuration(duration);
                        break;
                    case 3:
                        JudulBel();
                        break;
                }

            }
        });

        Button btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Ambil nilai hour dan minute dari timePicker masuk ke variabel hour dan minute
                //hour = alarmTimePicker.getCurrentHour().toString();
                //minute = alarmTimePicker.getCurrentMinute().toString();
                //Insert nilai-nilai variabel ke database
                if (selected==0){
                    repeat="Don't repeat";
                }
/*                if (chosenRingtone==null){
                    chosenRingtone="Default";
                }*/
                SetAlarmOn();
                int uye = (int) time;
                dbHelper.createAlarm(new AlarmModel(hourNow, minuteNow, chosenRingtone, repeat, 1,
                        duration*1000, id2, JudulBel, uye));
                Intent i = new Intent(SettingAlarm.this, HomeScreen.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });
        Button btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent home = new Intent(SettingAlarm.this, HomeScreen.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(home);
            }
        });
    }

    public void JudulBel(){
        final Dialog d = new Dialog(SettingAlarm.this);
        d.setTitle("INPUT NAMA BEL");
        d.setContentView(R.layout.input_box);

        final EditText txtInput = (EditText)d.findViewById(R.id.txtInput);

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

    public void Repeat(){
        /*final Dialog d = new Dialog(SettingAlarm.this);
        d.setTitle("PERULANGAN BEL");
        d.setContentView(R.layout.input_box_radio_button_single);
        final RadioGroup radioGroup = (RadioGroup)d.findViewById(R.id.radioGroup);
        final RadioButton r1 = (RadioButton)d.findViewById(R.id.radioButton1);
        final RadioButton r2 = (RadioButton)d.findViewById(R.id.radioButton2);
        Button btnOK = (Button)d.findViewById(R.id.btnOK);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected = radioGroup.getCheckedRadioButtonId();
                RadioButton radioButton = (RadioButton)d.findViewById(selected);
                Toast.makeText(SettingAlarm.this, "Klik : " + radioButton.getText() + " " + selected, Toast.LENGTH_SHORT).show();
                d.dismiss();

                int idr1 = r1.getId();
                int idr2 = r2.getId();
                if (selected==idr1){
                    repeat="Don't repeat";
                }
                else if (selected==idr2){
                    CustomRepeat();
                }
                else{
                    repeat="gulo jowo";
                }
                fullObject.setSub(repeat);
                adapter.notifyDataSetChanged();

            }
        });*/

        final CharSequence[] items = {"Don't repeat", "Customize"};
        final AlertDialog.Builder b = new AlertDialog.Builder(SettingAlarm.this);
        b.setTitle("Repeat Alarm");
        b.setCancelable(true);
        b.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                switch (item)
                {
                    case 0:
                        //Kalau klik don't repeat
                        repeat = "Don't repeat";
                        break;
                    case 1:
                        //Kalau klik everyday
                        repeat = "Everyday";
                        break;
                    case 2:
                        //Klik kalau customize
                        CustomRepeat();
                        break;
                }
            }
        });

        b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                fullObject.setSub(repeat);
                adapter.notifyDataSetChanged();
                Toast.makeText(SettingAlarm.this, repeat, Toast.LENGTH_SHORT).show();
            }
        });
        d = b.create();

        d.show();
    }

    public void SetDuration(){
        final CharSequence[] items = {" Pakai Settingan Global ", " Set khusus alarm ini "};

        final AlertDialog.Builder b = new AlertDialog.Builder(SettingAlarm.this);
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
        final Dialog d = new Dialog(SettingAlarm.this);
        d.setTitle("INPUT DURASI BEL");
        d.setContentView(R.layout.input_box_number);

        final EditText txtInput = (EditText)d.findViewById(R.id.txtInput);

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

    public void CustomRepeat() {

        // TODO : Custom Repeat
        // TODO : PRE-CODING,
        // TODO : 1. misal sing dipilih Monday, Tuesday, metu ning setSub dadi Monday, Tuesday.
        // TODO : 2. trus Monday, Tuesday mau kuwi yo metu ning ListView ning ListActivity
        // TODO : 3. Monday, Tuesday kuwi yo mlebu ning DB kolom SetDay
        // TODO : 4. FINAL, nek kuwi kabeh wes, dicoba ning dina Monday, Tuesday muni ora, trus selain Monday, Tuesday apakah tetep muni
        // TODO : 5. Nek kuwi wes iso, trus dicoba ning pas ModifyAlarm, ning ModifyAlarm yo dicoba nomor 1-4 di atas.
        // TODO : Nek golek golek masalah start alarm, goleki bagian bagian PendingIntent ning SetAlarmOn (class SettingAlarm & ModifyAlarm)
        // TODO : Nek golek golek masalah stop alarm (ben alarm ra muni), goleki bagian PendingIntent (SetAlarmOn) ning ModifyAlarm, trus ngko ning bagian DeleteData (class ModifyAlarm)

        final CharSequence[] items = {" Monday ", " Tuesday ", " Wednesday ", " Thursday ", " Friday ", " Saturday ", " Sunday "};

        final AlertDialog.Builder b = new AlertDialog.Builder(SettingAlarm.this);
        b.setTitle("Customize Day");
        b.setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean isChecked) {
                if (isChecked) {
                    //Kalau user milih hari itu, trus gimana (insert ke database)
                    //Set ke textview juga
                } else {
                    //Kalau item udah ada, remove (mbuh maksute piye)
                }
            }
        });

        // Button OK
        b.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
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

    public void SetAlarmOn()
    {
        //Toast.makeText(SettingAlarm.this, "ALARM ON", Toast.LENGTH_SHORT).show();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getCurrentHour());
        calendar.set(Calendar.MINUTE, alarmTimePicker.getCurrentMinute());
        hourNow = alarmTimePicker.getCurrentHour();
        minuteNow = alarmTimePicker.getCurrentMinute();
        intent1 = new Intent(this, AlarmReceiver.class);
        Bundle b = new Bundle();
        jumlah_waktu = hourNow + minuteNow;
        //Toast.makeText(SettingAlarm.this, jumlah_waktu, Toast.LENGTH_SHORT).show();
        if (chosenRingtone != null){
            b.putString("ringtone_alarm", chosenRingtone);
        } else {
            b.putString("ringtone_alarm", null);
        }
        b.putInt("durasi", duration*1000);
        intent1.putExtras(b);

        intent1.putExtra("repeat", repeat);
        intent1.putExtra("duration", duration);

        //Supaya bisa multiple alarms
        id2 = (int) System.currentTimeMillis(); //id2 adalah id utk alarm-nya, supaya tiap alarm memiliki ID berbeda
                                                //Ora nganggo ID DB, soale raiso, nek meh nganggo ID DB kuwi kan kudu wes ono sek ning DB
                                                //Sedangkan iki kan rung mlebu ning DB
        time=(calendar.getTimeInMillis()-(calendar.getTimeInMillis()%60000));

        pendingIntent1 = PendingIntent.getBroadcast(this, id2, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        if(System.currentTimeMillis()>time)
        {
            if (calendar.AM_PM == 0)
                time = time + (1000*60*60*12);
            else
                time = time + (1000*60*60*24);
        }
        alarmManager.setRepeating(AlarmManager.RTC, time, 0, pendingIntent1);
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
                uri = Uri.parse("android.resource://" + getPackageName() + "/raw/iphone7__2016");
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
        sr.setJudul("Repeat");
        sr.setSub("Don't repeat");
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

        sr = new ModelSettingAlarm();
        sr.setJudul("Nama Bel");
        sr.setSub("Belum di-set");
        results.add(sr);

        return results;
    }

}