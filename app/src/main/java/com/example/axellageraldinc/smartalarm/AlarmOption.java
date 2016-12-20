package com.example.axellageraldinc.smartalarm;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.axellageraldinc.smartalarm.Database.DBHelper;
import com.example.axellageraldinc.smartalarm.RecyclerViewListAlarm.ListActivity;

import org.w3c.dom.Text;

public class AlarmOption extends AppCompatActivity {

    private DBHelper dbHelper;
    private TimePicker tp;
    private Button btnUpdate, btnDelete;
    public static String hourModify, menitModify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_option);

        dbHelper = new DBHelper(this);

        Intent i = getIntent();
        hourModify = i.getStringExtra("jam");
        menitModify = i.getStringExtra("menit");

        String waktu = String.format("%02d : %02d", Integer.parseInt(hourModify), Integer.parseInt(menitModify));

        TextView txtWaktu = (TextView) findViewById(R.id.txtWaktu);
        txtWaktu.setText(waktu);

        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Pergi ke Modify Alarm
                Intent i = new Intent(AlarmOption.this, ModifyAlarm.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.putExtra("jam", hourModify);
                i.putExtra("menit", menitModify);
                startActivity(i);
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
    }

    public void DeleteData()
    {
        final AlertDialog.Builder d = new AlertDialog.Builder(this);
        d.setCancelable(true);
        final TextView t = new TextView(this);
        t.setText("Are you sure to delete this alarm?");
        d.setTitle("Are you sure?");
        d.setView(t);

        d.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dbHelper.deleteAlarm(hourModify, menitModify);
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
        Intent home = new Intent(AlarmOption.this, ListActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(home);
    }

}
