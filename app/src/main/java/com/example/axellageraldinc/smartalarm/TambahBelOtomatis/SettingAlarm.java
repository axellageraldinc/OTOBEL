package com.example.axellageraldinc.smartalarm.TambahBelOtomatis;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.axellageraldinc.smartalarm.CustomRepeat;
import com.example.axellageraldinc.smartalarm.Database.BelOtomatisModel;
import com.example.axellageraldinc.smartalarm.HomeScreen;
import com.example.axellageraldinc.smartalarm.Receiver.AlarmReceiver;
import com.example.axellageraldinc.smartalarm.Database.DBHelper;
import com.example.axellageraldinc.smartalarm.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class SettingAlarm extends AppCompatActivity
{
    TimePicker alarmTimePicker;
    public static PendingIntent pendingIntent1;
    public static AlarmManager alarmManager;
    Intent intent1;
    public String chosenRingtone="Default";
    DBHelper dbHelper;
    AlertDialog d;
    ArrayList<ModelSettingAlarm> results;
    ArrayList<Integer> daysOfWeek;
    public static long time;
    ActionBar actionBar;
    public static int durasifix=10000, duration, hour, minute;
    public static String durasi;
    ModelSettingAlarm fullObject, sr;
    MyCustomBaseAdapter adapter;
    ListView lv;
    private Uri uri;
    private String repeat, title, JudulBel;
    long idItem;
    public static int id2=0, selected=0;


    public SettingAlarm() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bel_otomatis);
        actionBar = getSupportActionBar();
        actionBar.setTitle(this.getResources().getString(R.string.JudulSettingAlarm));
        actionBar.setIcon(R.drawable.logoooo);
        repeat="Don't repeat";
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
                        //dbHelper.InsertDuration(duration);
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
                /*if (chosenRingtone==null){
                    uri = Uri.parse("android.resource://com.example.axellageraldinc.smartalarm.TambahAlarmBaru/raw/iphone7__2016");
                    chosenRingtone = uri.toString();
                }*/
                int uye = (int) time;
                if (chosenRingtone=="Default"){
                    Toast.makeText(SettingAlarm.this, "Harap pilih ringtone", Toast.LENGTH_SHORT).show();
                }
                else{
                    setAlarmOn();
//                    Log.v("Repeat", repeat);
//                    ArrayList<String> dow = getDaysOfWeek(daysOfWeek);
//                    StringBuilder sb = new StringBuilder();
//                    for (int a=0;a<dow.size();a++) {
//                        sb.append(dow.get(a));
//                        if (a<dow.size()-1) {
//                            sb.append(", ");
//                        }
//                    }
//                    Log.v("Days of week (shfl)", String.valueOf(sb));
                    dbHelper.createAlarm(new BelOtomatisModel(hour, minute, chosenRingtone, repeat, 1
                            , duration*1000, id2, JudulBel, uye));
                    Intent i = new Intent(SettingAlarm.this, HomeScreen.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    finish();
                }
            }
        });
        Button btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent home = new Intent(SettingAlarm.this, HomeScreen.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(home);
                finish();
            }
        });
    }

    public void JudulBel(){
        final Dialog d = new Dialog(SettingAlarm.this);
        d.setTitle(R.string.InputNamaBel);
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
        final CharSequence[] items = {"Don't repeat", "Everyday", "Customize"};
        final AlertDialog.Builder b = new AlertDialog.Builder(SettingAlarm.this);
        b.setTitle("Repeat Alarm");
        b.setCancelable(true);
        int checkedItem;
        if (selected == 0 || repeat.equals("Don't repeat")) {
            checkedItem = 0;
        } else if (selected == 1 || repeat.equals("Everyday")) {
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
                        selected = item;
                        repeat = "Don't repeat";
                        break;
                    case 1:
                        //Kalau klik everyday
                        selected = item;
                        repeat = "Everyday";
                        break;
                    case 2:
                        //Klik kalau customize
                        selected = item;
                        CustomRepeat();
                        break;
                }
            }
        });

        b.setPositiveButton(R.string.ButtonOK, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (selected == 0 || selected == 1) {
                    fullObject.setSub(repeat);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(SettingAlarm.this, repeat, Toast.LENGTH_SHORT).show();
                } else {
                    ArrayList<String> stDaysOfWeek = getDaysOfWeek(daysOfWeek);
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
                    Toast.makeText(SettingAlarm.this, repeat, Toast.LENGTH_SHORT).show();
                }
            }
        });
        d = b.create();

        d.show();
    }

    public void SetDuration(){
        final CharSequence[] items = {" Default (10 detik) ", " Set khusus alarm ini "};

        final AlertDialog.Builder b = new AlertDialog.Builder(SettingAlarm.this);
        b.setTitle(R.string.InputDurasiBel);
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
        final Dialog d = new Dialog(SettingAlarm.this);
        d.setTitle(R.string.InputDurasiBel);
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
        Intent i = new Intent(SettingAlarm.this, CustomRepeat.class); //.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ArrayList<String> stRepeat = new ArrayList<String>();
        stRepeat.addAll(Arrays.asList(repeat.split("\\s*,\\s*")));
        ArrayList<Integer> intDaysOfWeek = SettingAlarm.getIntDaysOfWeek(stRepeat);
        i.putIntegerArrayListExtra("daysOfWeek", intDaysOfWeek);
        startActivityForResult(i, 1);
    }

    /**
     * Method buat Set On Alarm tak ganti ini, biar sekalian bisa repeat.
     * Penjelasan penggunaan :
     * - Kalo mau repeat everyday berarti pake for 1-7, didalem for dipanggil method ini, parameter diisi dengan variabel
     *   untuk for.
     * - Kalo mau repeat weekday berarti pake for 2-6, didalem for dipanggil method ini, parameter diisi dengan variabel
     *   untuk for.
     * - Kalo mau repeat hari lain, isi angka parameter seperti yg dijelaskan di atas.
     *
     */
    public void setAlarmOn() {
        hour = alarmTimePicker.getCurrentHour();
        minute = alarmTimePicker.getCurrentMinute();
        intent1 = new Intent(this, AlarmReceiver.class);
        Bundle b = new Bundle();
        // Biar di database chosenRingtone gak kosong
        if (chosenRingtone.equals("Default")){
            b.putString("ringtone_alarm", null);
        } else {
            b.putString("ringtone_alarm", chosenRingtone);
        }
        b.putInt("durasi", duration*1000);
        intent1.putExtras(b);

        intent1.putExtra("repeat", repeat);
        intent1.putExtra("duration", duration);
        intent1.putExtra("jam", hour);
        intent1.putExtra("menit", minute);

        //Supaya bisa multiple alarms
        id2 = (int) System.currentTimeMillis(); //id2 adalah id utk alarm-nya, supaya tiap alarm memiliki ID berbeda
        //Ora nganggo ID DB, soale raiso, nek meh nganggo ID DB kuwi kan kudu wes ono sek ning DB
        //Sedangkan iki kan rung mlebu ning DB
        intent1.putExtra("id2",id2);
        pendingIntent1 = PendingIntent.getBroadcast(this, id2, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        Log.v("Repeat alarm", repeat);
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

        alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent1);
    }

    public void setEverydayAlarm() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getCurrentHour());
        calendar.set(Calendar.MINUTE, alarmTimePicker.getCurrentMinute());
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent1);
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
        } else if (resultCode == Activity.RESULT_OK && requestCode == 1) {
            daysOfWeek = intent.getIntegerArrayListExtra("daysOfWeek");
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
        sr.setSub("None");
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

    /**
     * Method buat convert daysOfWeek Integer jadi String (buat ngeset sub-judul).
     * @param daysOfWeek Berupa ArrayList<Integer> biar bisa dijabarin satu-satu jadi bentuk string
     * @return Berupa ArrayList<String> biar bisa tau maksud hari/ngeset sub-judul.
     *          Kalo size dari parameternya 8, return "Everyday".
     *          Kalo isinya 2,3,4,5,6, return "Weekday".
     *          Kalo isinya 1,7, return "Weekend".
     */
    public static ArrayList<String> getDaysOfWeek(ArrayList<Integer> daysOfWeek) {
        ArrayList<String> stDaysOfWeek = new ArrayList<>();
        Collections.sort(daysOfWeek);
        if (daysOfWeek.contains(1)) {
            stDaysOfWeek.add("Minggu");
        }
        if (daysOfWeek.contains(2)) {
            stDaysOfWeek.add("Senin");
        }
        if (daysOfWeek.contains(3)) {
            stDaysOfWeek.add("Selasa");
        }
        if (daysOfWeek.contains(4)) {
            stDaysOfWeek.add("Rabu");
        }
        if (daysOfWeek.contains(5)) {
            stDaysOfWeek.add("Kamis");
        }
        if (daysOfWeek.contains(6)) {
            stDaysOfWeek.add("Jumat");
        }
        if (daysOfWeek.contains(7)) {
            stDaysOfWeek.add("Sabtu");
        }
        if (daysOfWeek.size() == 7) {
            stDaysOfWeek.clear();
            stDaysOfWeek.add("Everyday");
        } else if (daysOfWeek.contains(2) && daysOfWeek.contains(3) && daysOfWeek.contains(4)
                && daysOfWeek.contains(5) && daysOfWeek.contains(6)) {
            stDaysOfWeek.clear();
            stDaysOfWeek.add("Weekday");
        } else if (daysOfWeek.contains(1) && daysOfWeek.contains(7) && !daysOfWeek.contains(2)
                && !daysOfWeek.contains(3) && !daysOfWeek.contains(4) && !daysOfWeek.contains(5)
                && !daysOfWeek.contains(6)) {
            stDaysOfWeek.clear();
            stDaysOfWeek.add("Weekend");
        }
        return stDaysOfWeek;
    }

    /**
     * Method buat convert daysOfWeek String jadi Integer (buat ngeset di method setAlarmRepeat).
     * @param daysOfWeek Berupa ArrayList<String> biar bisa dijabarin satu-satu jadi bentuk integer.
     * @return Berupa ArrayList<String> biar bisa ngeset method setAlarmRepeat.
     *         Kalo isinya "Everyday", return 1-7.
     *         Kalo isinya "Weekday", return 2-6.
     *         Kalo isinya "Weekend", return 1,7.
     */
    public static ArrayList<Integer> getIntDaysOfWeek(ArrayList<String> daysOfWeek) {
        ArrayList<Integer> intDaysOfWeek = new ArrayList<>();
        if (daysOfWeek.contains("Minggu")) {
            intDaysOfWeek.add(1);
        }
        if (daysOfWeek.contains("Senin")) {
            intDaysOfWeek.add(2);
        }
        if (daysOfWeek.contains("Selasa")) {
            intDaysOfWeek.add(3);
        }
        if (daysOfWeek.contains("Rabu")) {
            intDaysOfWeek.add(4);
        }
        if (daysOfWeek.contains("Kamis")) {
            intDaysOfWeek.add(5);
        }
        if (daysOfWeek.contains("Jumat")) {
            intDaysOfWeek.add(6);
        }
        if (daysOfWeek.contains("Sabtu")) {
            intDaysOfWeek.add(7);
        }
        if (daysOfWeek.contains("Everyday")) {
            intDaysOfWeek.clear();
            for (int a=1;a<8;a++) {
                intDaysOfWeek.add(a);
            }
        } else if (daysOfWeek.contains("Weekday")) {
            intDaysOfWeek.clear();
            for (int a=2;a<7;a++) {
                intDaysOfWeek.add(a);
            }
        } else if (daysOfWeek.contains("Weekend")) {
            intDaysOfWeek.clear();
            intDaysOfWeek.add(1);
            intDaysOfWeek.add(7);
        }
        Collections.sort(intDaysOfWeek);
        return intDaysOfWeek;
    }

}