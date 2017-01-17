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
import android.util.Log;
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
import java.util.Date;

// TODO : Masih ada bug bagian UI setelah alarm di modify
// TODO : Bug set ringtone

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
        ArrayList<String> stRepeat = new ArrayList<String>();
        stRepeat.addAll(Arrays.asList(repeat.split("\\s*,\\s*")));
        daysOfWeek = SettingAlarm.getIntDaysOfWeek(stRepeat);
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
        d.setTitle(R.string.InputNamaBel);
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

        b.setPositiveButton(R.string.ButtonOK, new DialogInterface.OnClickListener() {
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
        final CharSequence[] items = {" Default (10 detik) ", " Set khusus alarm ini "};

        final AlertDialog.Builder b = new AlertDialog.Builder(ModifyAlarm.this);
        b.setTitle(R.string.InputDurasiBel);
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
        b.setPositiveButton(R.string.ButtonOK,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        fullObject.setSub(duration + " detik");
                        adapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });

        //Button Cancel
        b.setNegativeButton(R.string.ButtonCancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        d = b.create();
        d.show();
    }

    public void SetDurasiNew(){
        final Dialog d = new Dialog(ModifyAlarm.this);
        d.setTitle(R.string.InputDurasiBel);
        d.setContentView(R.layout.input_box_number);

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

    }

    public void setAlarmOn() {
        hourNow = alarmTimePicker.getCurrentHour();
        minuteNow = alarmTimePicker.getCurrentMinute();
        intent1 = new Intent(this, AlarmReceiver.class);
        Bundle b = new Bundle();
        // Biar di database chosenRingtone gak kosong
        if (chosenRingtone.equals("Default") || chosenRingtone == null){
            b.putString("ringtone_alarm", null);
        } else {
            b.putString("ringtone_alarm", chosenRingtone);
        }
        b.putInt("durasi", duration*1000);
        intent1.putExtras(b);

        intent1.putExtra("repeat", repeat);
        intent1.putExtra("duration", duration);
        intent1.putExtra("jam", hourNow);
        intent1.putExtra("menit", minuteNow);
        intent1.putExtra("id2", ID2);
        pendingIntent = PendingIntent.getBroadcast(this, ID2, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        if (repeat.equals("Everyday")) {
            setEverydayAlarm();
        } else {
            if (repeat.equals("Don't repeat")) {
                setRepeatAlarm(0, 0);
            } else {
                // Variable buat ngecek hari alarm di set
                int i = 0, j = 0, k = 0, x = 0;

                // calNow buat ngecek hari ini (DAY_OF_WEEK)
                Calendar calNow = Calendar.getInstance();

                // Fungsi while buat ngecek apa ada hari yang lebih dari hari ini
                // contoh: set hari selasa, rabu, kamis .. hari ini selasa, berarti ada yg lebih
                // dari hari ini yaitu rabu dan kamis
                while (j < daysOfWeek.size()) {
                    if (calNow.get(Calendar.DAY_OF_WEEK) > daysOfWeek.get(j)) {
                        i = daysOfWeek.size();
                        x = j;
                    } else {
                        i  = 0;
                        k++;
                    }
                    j++;
                }

                // Fungsi buat ngeset alarm kalo ga ada hari yang lebih dari hari ini
                if (k == 0) {
                    setRepeatAlarm(daysOfWeek.get(0), x);
                    i = daysOfWeek.size();
                }

                // Fungsi buat ngeset alarm kalo ada hari yang lebih dari hari ini
                while (i < daysOfWeek.size()) {
                    if (calNow.get(Calendar.DAY_OF_WEEK) <= daysOfWeek.get(i)) {
                        setRepeatAlarm(daysOfWeek.get(i), i);
                        break;
                    }
                    // Ga perlu di ganti urutannya, cukup ditambah variable "I"
//                    int temp = daysOfWeek.get(i);
//                    daysOfWeek.remove(i);
//                    daysOfWeek.add(temp);
                    i++;
                }
            }
        }
    }

    public void setRepeatAlarm(int daysOfWeek, int index) {
        Calendar calendar = Calendar.getInstance();
        Date now = new Date();
        calendar.setTime(now);
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        /*
        Buat debug aja
        - Hari ini = hari ini
        - DOW = hari yang ada di parameter
         */
        Log.v("Hari ini", String.valueOf(day));
        Log.v("DOW", String.valueOf(daysOfWeek));

        // Mulai setting alarm
        calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getCurrentHour());
        calendar.set(Calendar.MINUTE, alarmTimePicker.getCurrentMinute());
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // Ngecek alarm kurang dari jam sekarang atau tidak
        if(calendar.getTimeInMillis() < System.currentTimeMillis()) {
            // Kalo kurang dari jam sekarang, bakal jalan semua fungsinya

            // Kalo don't repeat, bakal nambah 1 hari (besok)
            if (daysOfWeek == 0) {
                calendar.add(Calendar.DATE, 1);

                // Fungsi jalan buat selain don't repeat
            } else {
                // Fungsi jalan kalo hari ini = hari yang di set
                if (day == daysOfWeek) {
                    // Variable pembantu
                    int dayOfYear = 0;
                    // Fungsi jalan kalo size dari arraylist udah maks, bakal ngeset buat arraylist ke-0
                    if (this.daysOfWeek.size() == index+1) {
                        dayOfYear = day - this.daysOfWeek.get(0);
                        // Fungsi jalan kalo size dari arraylist belom maks
                    } else {
                        dayOfYear = day - this.daysOfWeek.get(index+1);
                        daysOfWeek = this.daysOfWeek.get(index+1);
                    }
                    // Fungsi jalan buat nambah hari dari hari ini / ngeset day of week kalo belom ganti minggu
                    if (dayOfYear > 0) {
                        calendar.add(Calendar.DATE, Math.abs(7-dayOfYear));
                    } else {
                        calendar.set(Calendar.DAY_OF_WEEK, daysOfWeek);
                    }
                    // Fungsi jalan kalo hari ini bukan hari yang di set
                } else {
                    int dayOfYear = day - daysOfWeek;
                    // Debug aja sih
                    Log.v("DayofYear", String.valueOf(dayOfYear));
                    // Fungsi jalan buat nambah hari dari hari ini / ngeset day of week kalo belom ganti minggu
                    if (dayOfYear > 0) {
                        calendar.add(Calendar.DATE, Math.abs(7-dayOfYear));
                    } else {
                        calendar.set(Calendar.DAY_OF_WEEK, daysOfWeek);
                    }
                }
            }
            // Fungsi jalan kalo jam sekarang kurang dari jam yang di set
        } else if (daysOfWeek != 0) {
            // Ngecek hari yang di set >= hari ini
            if (daysOfWeek >= day) {
                calendar.set(Calendar.DAY_OF_WEEK, daysOfWeek);
            } else {
                int dayOfYear = day - daysOfWeek;
                calendar.add(Calendar.DATE, Math.abs(7-dayOfYear));
            }
        }
        // Debug lagi
        Log.v("Tanggal ini", String.valueOf(now));
        Log.v("Alarm set on", String.valueOf(calendar.getTime()));

        time=calendar.getTimeInMillis(); //(calendar.getTimeInMillis()-(calendar.getTimeInMillis()%60000));
        Date setDate = calendar.getTime();
        long diff = setDate.getTime() - now.getTime();
        long minute = diff / (60 * 1000) % 60;
        long hour = diff / (60 * 60 * 1000) % 24;
        long sday = diff / (60 * 60 * 24 * 1000) % 365;
        Log.v("Alarm", "Your alarm will be set in " + sday + " day(s), " +
                hour + " hour(s), " + minute + " minute(s)");

        alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
    }

    public void setEverydayAlarm() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getCurrentHour());
        calendar.set(Calendar.MINUTE, alarmTimePicker.getCurrentMinute());
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
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
                setAlarmOn();
                dbHelper.updateAlarm(new BelOtomatisModel(hourEdited, minuteEdited, chosenRingtone, repeat, 1,
                        duration*1000, ID2, JudulBel, uye));
                /*if (status==1){
//                    alarmManager.setRepeating(AlarmManager.RTC, time, 0, pendingIntent);
                    setAlarmOn();
                }
                else{
                    setAlarmOn();
                    alarmManager.cancel(pendingIntent);
                }*/
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

        d.setPositiveButton(R.string.ButtonOK, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dbHelper.deleteAlarm(ID);
                AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
                setAlarmOn();
                alarmManager.cancel(pendingIntent);
                //pendingIntentDelete.cancel();
                //pendingIntent.cancel();
                dialog.dismiss();
                // Set result ok, terus finish(di back ga balik sini)
                setResult(Activity.RESULT_OK);
                finish();
            }
        });

        d.setNegativeButton(R.string.ButtonCancel, new DialogInterface.OnClickListener() {
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
        if (ringtone == null || ringtone.equals("Default")) {
            chosenRingtone = "Default";
        } else {
            chosenRingtone = ringtone;
        }
        sr.setSub(chosenRingtone);
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
