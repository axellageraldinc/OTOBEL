package com.example.axellageraldinc.smartalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Window;
import android.view.WindowManager.LayoutParams;

import com.example.axellageraldinc.smartalarm.Receiver.AlarmReceiver;

import java.util.Random;

public class ShowAlarm extends AppCompatActivity {

    private String[] operators = {"+", "-", "x"};
    private final int add=0, sub=1, mul=2;
    private int answer, number1, number2, operator, enteredAnswer;
    private String enteredtxtAnswer;

    Random random;

    TextView txtNumber1, txtNumber2, txtOperator;
    EditText txtAnswer;
    Button btnSubmit;
    Window window;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_alarm);

        //Supaya activity ini wake up android nya
        window = this.getWindow();
        window.addFlags(LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(LayoutParams.FLAG_TURN_SCREEN_ON);
        this.setFinishOnTouchOutside(false);

/*        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);*/
/*        pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "TAG");
        wakeLock.acquire();
        keyguardManager = (KeyguardManager) getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
        keyguardLock = keyguardManager.newKeyguardLock("TAG");
        keyguardLock.disableKeyguard();*/

        randomize();

        /*Button btnOff = (Button) findViewById(R.id.btnOff);

        btnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PendingIntent pendingIntent;
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                Intent intent1 = new Intent(getApplicationContext(), AlarmReceiver.class);
                pendingIntent = PendingIntent.getBroadcast(ShowAlarm.this, 0, intent1, 0);
                alarmManager.cancel(pendingIntent);
                Toast.makeText(ShowAlarm.this, "ALARM OFF", Toast.LENGTH_SHORT).show();
            }
        });*/

}
    //GAME
    public void randomize()
    {
        random = new Random();
        number1 = random.nextInt(50);
        number2 = random.nextInt(50);
        //Supaya kalau angka kedua >10, gak usah perkalian
        if (number2>10){
            operator = random.nextInt(2);
        }
        else if (number2<10){
            operator = random.nextInt(operators.length);
        }
        else if (operator == sub) {
            while(number1<number2){
                number1 = random.nextInt(50);
                number2 = random.nextInt(50);
            }
        }

        txtNumber1 = (TextView) findViewById(R.id.number1);
        txtNumber2 = (TextView) findViewById(R.id.number2);
        txtOperator = (TextView) findViewById(R.id.operator);
        txtAnswer = (EditText) findViewById(R.id.answerField);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);

        txtAnswer.setText(null);
        txtNumber1.setText(String.valueOf(number1));
        txtNumber2.setText(String.valueOf(number2));
        txtOperator.setText(operators[operator]);

        switch(operator)
        {
            case add:
                answer = number1+number2;
                break;
            case sub:
                answer = number1-number2;
                break;
            case mul:
                answer = number1*number2;
                break;
            default:
                break;
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                enteredtxtAnswer = txtAnswer.getText().toString();
                enteredAnswer = Integer.parseInt(enteredtxtAnswer);

                if (enteredAnswer == answer){
                    //ALARM STOP
                    PendingIntent pendingIntent;
                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                    Intent intent1 = new Intent(getApplicationContext(), AlarmReceiver.class);
                    pendingIntent = PendingIntent.getBroadcast(ShowAlarm.this, 0, intent1, 0);
                    alarmManager.cancel(pendingIntent);
                    Toast.makeText(ShowAlarm.this, "ALARM OFF", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else{
                    Toast.makeText(ShowAlarm.this, "JAWABAN ANDA SALAH", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

}
