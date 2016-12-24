package com.example.axellageraldinc.smartalarm;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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
import com.example.axellageraldinc.smartalarm.Database.DBHelper;
import com.example.axellageraldinc.smartalarm.Receiver.AlarmReceiver;
import com.example.axellageraldinc.smartalarm.RecyclerViewListAlarm.ListActivity;
import com.example.axellageraldinc.smartalarm.TambahAlarmBaru.ModelSettingAlarm;
import com.example.axellageraldinc.smartalarm.TambahAlarmBaru.MyCustomBaseAdapter;
import com.example.axellageraldinc.smartalarm.TambahAlarmBaru.SettingAlarm;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Set;

public class ModifyAlarm extends AppCompatActivity {
    TimePicker alarmTimePicker;
    public static PendingIntent pendingIntent;
    public static AlarmManager alarmManager;
    Intent intent1;
    public String chosenRingtone;
    String hour, minute;
    DBHelper dbHelper;
    AlertDialog d;
    public static long time;
    ActionBar actionBar;
    int mHour, mMinute;
    ListView listViewSet;
    TextView txtRepeat;
    public int hourNow, minuteNow, jumlah_waktu;
    public static int durasifix=10000, duration, selected, ID2, ID, status, hourModify, menitModify;
    public static String durasi, title;
    public static String repeat, JudulBel, ringtone;
    private Button btnSave, btnCancel, btnDelete;
    ModelSettingAlarm fullObject, sr;
    MyCustomBaseAdapter adapter;
    ListView lv;
    ArrayList<ModelSettingAlarm> results;
    long idItem;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_alarm);

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
                Intent home = new Intent(ModifyAlarm.this, ListActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(home);
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
                        dbHelper.InsertDuration(duration);
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
        txtInput.setText(JudulBel);

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
        /*final Dialog d = new Dialog(ModifyAlarm.this);
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
                Toast.makeText(ModifyAlarm.this, "Klik : " + radioButton.getText() + " " + selected, Toast.LENGTH_SHORT).show();
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
        final AlertDialog.Builder b = new AlertDialog.Builder(ModifyAlarm.this);
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
                Toast.makeText(ModifyAlarm.this, repeat, Toast.LENGTH_SHORT).show();
            }
        });
        d = b.create();
        d.show();
    }

    public void SetDuration(){
        final CharSequence[] items = {" Pakai Settingan Global ", " Set khusus alarm ini "};

        final AlertDialog.Builder b = new AlertDialog.Builder(ModifyAlarm.this);
        b.setTitle("Durasi Alarm");
        b.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
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
        txtInput.setText(String.valueOf(duration));

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

        final CharSequence[] items = {" Monday ", " Tuesday ", " Wednesday ", " Thursday ", " Friday ", " Saturday ", " Sunday "};

        final AlertDialog.Builder b = new AlertDialog.Builder(ModifyAlarm.this);
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
        pendingIntent = PendingIntent.getBroadcast(this, ID2, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        //SettingAlarm.pendingIntent1.cancel(); //Cuma bisa cancel alarm yang terakhir kali aja. kalau ada 2 di edit, yg pertama tetep bunyi
        /*alarmManager.cancel(pendingIntent);*/

        time=(calendar.getTimeInMillis()-(calendar.getTimeInMillis()%60000));
        if(System.currentTimeMillis()>time)
        {
            if (calendar.AM_PM == 0)
                time = time + (1000*60*60*12);
            else
                time = time + (1000*60*60*24);
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
                SetAlarmOn();
                int uye = (int) time;
                dbHelper.updateAlarm(new AlarmModel(hourEdited, minuteEdited, chosenRingtone, repeat, status,
                        duration*1000, ID2, JudulBel, uye));
                if (status==1){
                    alarmManager.setRepeating(AlarmManager.RTC, time, 0, pendingIntent);
                }
                else{
                    pendingIntent.cancel();
                }
                dialog.dismiss();
                ReturnHome();
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
                SetAlarmOn();
                pendingIntent.cancel();
                //pendingIntentDelete.cancel();
                //pendingIntent.cancel();
                dialog.dismiss();
                ReturnHome();
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

    public void ReturnHome()
    {
        Intent home = new Intent(ModifyAlarm.this, HomeScreen.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(home);
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

        if (JudulBel==null){
            JudulBel="Belum di-set";
        }
        sr = new ModelSettingAlarm();
        sr.setJudul("Nama Bel");
        sr.setSub(JudulBel);
        results.add(sr);

        return results;
    }

}
