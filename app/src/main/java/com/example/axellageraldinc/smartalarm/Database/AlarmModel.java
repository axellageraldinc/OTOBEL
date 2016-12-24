package com.example.axellageraldinc.smartalarm.Database;

/**
 * Created by almantera on 15/11/16.
 */

public class AlarmModel {

    private int id;
    private int hour;
    private int minute;
    private String ringtone;
    private String set_day;
    private int status;
    private int alarm_duration;
    private int volume_alarm;
    private int ID2;
    private String judul_bel;
    private int order_alarm;

    public AlarmModel() {
        this.id = 0;
        this.hour = 0;
        this.minute = 0;
        this.ringtone = "";
        this.set_day = "";
        this.status = 0;
        this.alarm_duration=0;
        this.ID2=0;
        this.judul_bel="";
        this.order_alarm=0;
    }

    public AlarmModel(int hour, int minute, String ringtone, String set_day, int status, int alarm_duration,
                      int ID2, String judul_bel, int order_alarm) {
        this.hour = hour;
        this.minute = minute;
        this.ringtone = ringtone;
        this.set_day = set_day;
        this.status = status;
        this.alarm_duration = alarm_duration;
        this.ID2 = ID2;
        this.judul_bel = judul_bel;
        this.order_alarm = order_alarm;
    }

    public int getOrder_alarm() {
        return order_alarm;
    }

    public void setOrder_alarm(int order_alarm) {
        this.order_alarm = order_alarm;
    }

    public String getJudul_bel() {
        return judul_bel;
    }

    public void setJudul_bel(String judul_bel) {
        this.judul_bel = judul_bel;
    }

    public int getID2() {
        return ID2;
    }

    public void setID2(int ID2) {
        this.ID2 = ID2;
    }

    public int getVolume_alarm() {
        return volume_alarm;
    }

    public void setVolume_alarm(int volume_alarm) {
        this.volume_alarm = volume_alarm;
    }

    public int getAlarm_duration() {
        return alarm_duration;
    }

    public void setAlarm_duration(int alarm_duration) {
        this.alarm_duration = alarm_duration;
    }

    public int getId() { return id; }

    public void setId(int id) {
        this.id = id;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
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
}
