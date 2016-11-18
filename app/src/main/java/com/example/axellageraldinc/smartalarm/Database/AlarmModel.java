package com.example.axellageraldinc.smartalarm.Database;

/**
 * Created by almantera on 15/11/16.
 */

public class AlarmModel {

    private int id;
    private String hour;
    private String minute;
    private String ringtone;
    private String set_day;
    private int status;
    private int vibrate;

    public AlarmModel() {
        this.hour = "";
        this.minute = "";
        this.ringtone = "";
        this.set_day = "";
        this.status = 0;
        this.vibrate = 0;
    }

    public AlarmModel(String hour, String minute, String ringtone, String set_day, int status, int vibrate) {
        this.hour = hour;
        this.minute = minute;
        this.ringtone = ringtone;
        this.set_day = set_day;
        this.status = status;
        this.vibrate = vibrate;
    }

    public int getId() { return id; }

    public void setId(int id) {
        this.id = id;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getMinute() {
        return minute;
    }

    public void setMinute(String minute) {
        this.minute = minute;
    }

    public String getRingtone() {
        return ringtone;
    }

    public void setRingtone(String ringtone) {
        this.ringtone = ringtone;
    }

    public String getSet_day() {
        return set_day;
    }

    public void setSet_day(String set_day) {
        this.set_day = set_day;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getVibrate() {
        return vibrate;
    }

    public void setVibrate(int vibrate) {
        this.vibrate = vibrate;
    }
}
