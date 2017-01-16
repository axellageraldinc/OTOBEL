package com.example.axellageraldinc.smartalarm;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ActionBar ab = getSupportActionBar();
        ab.hide();

        ChangeFont();

        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(2000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    Intent intent = new Intent(SplashScreen.this,HomeScreen.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        };
        timerThread.start();
        /*Intent mServiceIntent = new Intent(getApplicationContext(), BackgroundService.class);
        ComponentName service = getApplicationContext().startService(mServiceIntent);
        if (null == service) {
            Toast.makeText(getApplicationContext(), "Gagal menjalankan service", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Berhasil menjalankan service", Toast.LENGTH_LONG).show();
        }*/
    }

    //Biar gak balik ke splash screen pas mencet tombol back
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }

    private void ChangeFont(){
        TextView Splash = (TextView)findViewById(R.id.txtSplash);

        Typeface bold= Typeface.createFromAsset(getAssets(), "fonts/avenir_black.otf");
        Typeface medium= Typeface.createFromAsset(getAssets(), "fonts/sansation_light.ttf");
        Typeface regular= Typeface.createFromAsset(getAssets(), "fonts/sansation_regular.ttf");

        Splash.setTypeface(bold);

    }

}
