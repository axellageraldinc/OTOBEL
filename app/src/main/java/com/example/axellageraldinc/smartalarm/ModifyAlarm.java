package com.example.axellageraldinc.smartalarm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.axellageraldinc.smartalarm.Database.BelOtomatisModel;
import com.example.axellageraldinc.smartalarm.Database.DBHelper;
import com.example.axellageraldinc.smartalarm.Receiver.AlarmReceiver;
import com.example.axellageraldinc.smartalarm.TambahBelOtomatis.ModelSettingAlarm;
import com.example.axellageraldinc.smartalarm.TambahBelOtomatis.MyCustomBaseAdapter;
import com.example.axellageraldinc.smartalarm.TambahBelOtomatis.SettingAlarm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

// TODO : Masih ada bug bagian UI setelah alarm di modify

public class ModifyAlarm extends AppCompatActivity {
    TimePicker alarmTimePicker;
    public static PendingIntent pendingIntent;
    public static AlarmManager alarmManager;
    Intent intent1;
    public String chosenRingtone;
    DBHelper dbHelper;
    AlertDialog d;
    public static long time;
    ActionBar actionBar;
    public int hourNow, minuteNow;
    public static int durasifix=10000, duration, ID2, ID, status, hourModify, menitModify, DurasiDB;
    public static String durasi, title;
    public static String repeat, JudulBel, ringtone;
    private Button btnSave, btnCancel, btnDelete;
    ModelSettingAlarm fullObject, sr;
    ArrayList<Integer> daysOfWeek;
    MyCustomBaseAdapter adapter;
    ListView lv;
    ArrayList<ModelSettingAlarm> results;
    long idItem;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_bel_otomatis);

        actionBar = getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.JudulModify));
        actionBar.setIcon(R.drawable.logoooo);

        alarmTimePicker = (TimePicker) findViewById(R.id.timePicker);
        alarmTimePicker.setIs24HourView(true);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        dbHelper = new DBHelper(ModifyAlarm.this);

        Intent i = getIntent();
        ID = i.getIntExtra("ID", 0);
        hourModify = i.getIntExtra("jam", 0);
        menitModify = i.getIntExtra("menit", 0);
        repeat = i.getStringExtra("repeat");
        JudulBel = i.getStringExtra("judul_bel");
        ringtone = i.getStringExtra("ringtone");
        duration = i.getIntExtra("duration", 0);
        ID2 = i.getIntExtra("ID2", 0);
        status = i.getIntExtra("status", 0);

        DurasiDB = dbHelper.GetDuration()*1000;

        //Set di timepicker tanggal sesuai yang diklik
        alarmTimePicker.setCurrentHour(hourModify);
        alarmTimePicker.setCurrentMinute(menitModify);

        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Update data ke database
                    UpdateData();
            }
        });

        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Set result cancel / sama dengan back terus finish
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });

        btnDelete = (Button) findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Hapus alarm yang dipilih
                DeleteData();
            }
        });

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
                        //dbHelper.InsertDuration(duration);
                        break;
                    case 3:
                        JudulBel();
                        break;
                }

            }
        });

    }

    public void JudulBel(){
        final Dialog d = new Dialog(ModifyAlarm.this);
        d.setTitle("INPUT NAMA BEL");
        d.setContentView(R.layout.input_box);

        final EditText txtInput = (EditText)d.findViewById(R.id.txtInput);
        InputMethodManager imm = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
        imm.showSoftInput(txtInput, InputMethodManager.SHOW_IMPLICIT);
        txtInput.setText(JudulBel);
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

    public void Repeat(){
        final CharSequence[] items = {"Don't repeat", "Everyday", "Customize"};
        final AlertDialog.Builder b = new AlertDialog.Builder(ModifyAlarm.this);
        b.setTitle("Repeat Alarm");
        b.setCancelable(true);
        int checkedItem;
        if (repeat.equals("Don't repeat")) {
            checkedItem = 0;
        } else if (repeat.equals("Everyday")) {
            checkedItem = 1;
        } else {
            checkedItem = 2;
        }
        b.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
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
                if (daysOfWeek == null) {
                    fullObject.setSub(repeat);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(ModifyAlarm.this, repeat, Toast.LENGTH_SHORT).show();
                } else {
                    ArrayList<String> stDaysOfWeek = SettingAlarm.getDaysOfWeek(daysOfWeek);
                    StringBuilder sb = new StringBuilder();
                    for (int a=0;a<stDaysOfWeek.size();a++) {
                        sb.append(stDaysOfWeek.get(a));
                        if (a<stDaysOfWeek.size()-1) {
                            sb.append(", ");
                        }
                    }
                    repeat = sb.toString();
                    fullObject.setSub(repeat);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(ModifyAlarm.this, repeat, Toast.LENGTH_SHORT).show();
                }
            }
        });
        d = b.create();
        d.show();
    }

    public void SetDuration(){
        final CharSequence[] items = {" Pakai Settingan Global ", " Set khusus alarm ini "};

        final AlertDialog.Builder b = new AlertDialog.Builder(ModifyAlarm.this);
        b.setTitle("Durasi Alarm");
        int checkedItem;
        if (duration*1000==DurasiDB){
            checkedItem = 0;
        } else{
            checkedItem = 1;
        }
        b.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
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
        final Dialog d = new Dialog(ModifyAlarm.this);
        d.setTitle("INPUT DURASI BEL");
        d.setContentView(R.layout.input_box);

        final EditText txtInput = (EditText)d.findViewById(R.id.txtInput);
        InputMethodManager imm = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
        imm.showSoftInput(txtInput, InputMethodManager.SHOW_IMPLICIT);
        txtInput.setText(String.valueOf(duration));
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

    public void CustomRepeat() {

        Intent i = new Intent(ModifyAlarm.this, CustomRepeat.class); //.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ArrayList<String> stRepeat = new ArrayList<String>();
        stRepeat.addAll(Arrays.asList(repeat.split("\\s*,\\s*")));
        ArrayList<Integer> intDaysOfWeek = SettingAlarm.getIntDaysOfWeek(stRepeat);
        i.putIntegerArrayListExtra("daysOfWeek", intDaysOfWeek);
        startActivityForResult(i, 1);
//        final CharSequence[] items = {" Monday ", " Tuesday ", " Wednesday ", " Thursday ", " Friday ", " Saturday ", " Sunday "};
//
//        final AlertDialog.Builder b = new AlertDialog.Builder(ModifyAlarm.this);
//        b.setTitle("Customize Day");
//        b.setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i, boolean isChecked) {
//                if (isChecked) {
//                    //Kalau user milih hari itu, trus gimana (insert ke database)
//                    //Set ke textview juga
//                } else {
//                    //Kalau item udah ada, remove (mbuh maksute piye)
//                }
//            }
//        });
//
//        // Button OK
//        b.setPositiveButton("OK",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//
//        //Button Cancel
//        b.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//
//        });
//        d = b.create();
//        d.show();
    }

    public void setAlarmRepeat(int daysOfWeek) {
        Calendar calendar = Calendar.getInstance();
        if (daysOfWeek != 0) {
            calendar.set(Calendar.DAY_OF_WEEK, daysOfWeek);
        }
        calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getCurrentHour());
        calendar.set(Calendar.MINUTE, alarmTimePicker.getCurrentMinute());
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if(calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 7);
        }
        hourNow = alarmTimePicker.getCurrentHour();
        minuteNow = alarmTimePicker.getCurrentMinute();
        intent1 = new Intent(this, AlarmReceiver.class);
        Bundle b = new Bundle();
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
        intent1.putExtra("id2",ID2);
        time=(calendar.getTimeInMillis()-(calendar.getTimeInMillis()%60000));

        pendingIntent = PendingIntent.getBroadcast(this, ID2, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        if(System.currentTimeMillis()>time)
        {
            if (calendar.AM_PM == 0)
                time = time + (1000*60*60*12);
            else
                time = time + (1000*60*60*24);
        }
        if (daysOfWeek == 0) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        } else {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, 0, pendingIntent);
        }
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
                chosenRingtone = null;
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == 1) {
            daysOfWeek = intent.getIntegerArrayListExtra("daysOfWeek");
        }
    }

    public String getChosenRingtone() {
        return chosenRingtone;
    }

    // KARENA METHOD DI BAWAH INI CUMA KE UPDATE UI NYA AJA
    // +++ UPDATE RINGTONE DLL
    public void UpdateData()
    {
        final AlertDialog.Builder d = new AlertDialog.Builder(this);
        d.setCancelable(true);
        d.setTitle("Apakah anda yakin ingin simpan?");

        d.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                int hourEdited = alarmTimePicker.getCurrentHour();
                int minuteEdited = alarmTimePicker.getCurrentMinute();
                ArrayList<String> stRepeat = new ArrayList<String>();
                stRepeat.addAll(Arrays.asList(repeat.split("\\s*,\\s*")));
                ArrayList<Integer> intRepeat = SettingAlarm.getIntDaysOfWeek(stRepeat);
                int uye = (int) time;
                dbHelper.updateAlarm(new BelOtomatisModel(hourEdited, minuteEdited, chosenRingtone, repeat, status,
                        duration*1000, ID2, JudulBel, uye));
                if (status==1){
//                    alarmManager.setRepeating(AlarmManager.RTC, time, 0, pendingIntent);
                    if (repeat.equals("Don't repeat")) {
                        setAlarmRepeat(0);
                    } else if (repeat.equals("Everyday")){
                        for (int a=1;a<=7;a++) {
                            setAlarmRepeat(a);
                        }
                    } else if (repeat.equals("Weekday")) {
                        for (int a=2;a<=6;a++) {
                            setAlarmRepeat(a);
                        }
                    } else if (repeat.equals("Weekend")) {
                        setAlarmRepeat(1);
                        setAlarmRepeat(7);
                    } else {
                        int list;
                        for (int a=0;a<intRepeat.size();a++) {
                            list = intRepeat.get(a);
                            setAlarmRepeat(list);
                        }
                    }
                }
                else{
                    if (repeat.equals("Don't repeat")) {
                        setAlarmRepeat(0);
                        pendingIntent.cancel();
                    } else if (repeat.equals("Everyday")){
                        for (int a=1;a<=7;a++) {
                            setAlarmRepeat(a);
                            pendingIntent.cancel();
                        }
                    } else if (repeat.equals("Weekday")) {
                        for (int a=2;a<=6;a++) {
                            setAlarmRepeat(a);
                            pendingIntent.cancel();
                        }
                    } else if (repeat.equals("Weekend")) {
                        setAlarmRepeat(1);
                        pendingIntent.cancel();
                        setAlarmRepeat(7);
                        pendingIntent.cancel();
                    } else {
                        int list;
                        for (int a=0;a<intRepeat.size();a++) {
                            list = intRepeat.get(a);
                            setAlarmRepeat(list);
                            pendingIntent.cancel();
                        }
                    }
                    pendingIntent.cancel();
                }
                dialog.dismiss();
                // Set result ok, terus finish(di back ga balik sini)
                setResult(Activity.RESULT_OK);
                finish();
            }
        });

        d.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        d.create();
        d.show();
    }

    public void DeleteData()
    {
        final AlertDialog.Builder d = new AlertDialog.Builder(this);
        d.setCancelable(true);
        d.setTitle("Apakah anda yakin untuk hapus bel?");

        d.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dbHelper.deleteAlarm(ID);
                AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
                ArrayList<String> stRepeat = new ArrayList<String>();
                stRepeat.addAll(Arrays.asList(repeat.split("\\s*,\\s*")));
                ArrayList<Integer> intRepeat = SettingAlarm.getIntDaysOfWeek(stRepeat);
                if (repeat.equals("Don't repeat")) {
                    setAlarmRepeat(0);
                    pendingIntent.cancel();
                } else if (repeat.equals("Everyday")){
                    for (int a=1;a<=7;a++) {
                        setAlarmRepeat(a);
                        pendingIntent.cancel();
                    }
                } else if (repeat.equals("Weekday")) {
                    for (int a=2;a<=6;a++) {
                        setAlarmRepeat(a);
                        pendingIntent.cancel();
                    }
                } else if (repeat.equals("Weekend")) {
                    setAlarmRepeat(1);
                    pendingIntent.cancel();
                    setAlarmRepeat(7);
                    pendingIntent.cancel();
                } else {
                    int list;
                    for (int a=0;a<intRepeat.size();a++) {
                        list = intRepeat.get(a);
                        setAlarmRepeat(list);
                        pendingIntent.cancel();
                    }
                }
                pendingIntent.cancel();
                //pendingIntentDelete.cancel();
                //pendingIntent.cancel();
                dialog.dismiss();
                // Set result ok, terus finish(di back ga balik sini)
                setResult(Activity.RESULT_OK);
                finish();
            }
        });

        d.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        d.create();
        d.show();
    }

    private ArrayList<ModelSettingAlarm> GetSearchResults(){
        results = new ArrayList<ModelSettingAlarm>();

        sr = new ModelSettingAlarm();
        sr.setJudul("Repeat");
        sr.setSub(repeat);
        results.add(sr);

        sr = new ModelSettingAlarm();
        sr.setJudul("Ringtone");
        sr.setSub(ringtone);
        results.add(sr);

        sr = new ModelSettingAlarm();
        sr.setJudul("Durasi Bel");
        sr.setSub(String.valueOf(duration) + " detik");
        results.add(sr);

        sr = new ModelSettingAlarm();
        sr.setJudul("Nama Bel");
        if (JudulBel==null)
            sr.setSub("Belum di-set");
        else
            sr.setSub(JudulBel);
        results.add(sr);

        return results;
    }

}
