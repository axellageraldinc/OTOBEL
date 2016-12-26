package com.example.axellageraldinc.smartalarm.Database;

/**
 * Created by Axellageraldinc A on 25-Dec-16.
 */

public class BelManualModel {

    private int id_manual;
    private String nama_bel_manual;
    private String ringtone_manual;
    private int durasi_manual;

    public BelManualModel(){
        this.id_manual=0;
        this.nama_bel_manual="";
        this.ringtone_manual="";
        this.durasi_manual=0;
    }

    public BelManualModel(String nama_bel_manual, String ringtone_manual, int durasi_manual){
        this.nama_bel_manual=nama_bel_manual;
        this.ringtone_manual=ringtone_manual;
        this.durasi_manual=durasi_manual;
    }

    public int getId_manual() {
        return id_manual;
    }

    public void setId_manual(int id_manual) {
        this.id_manual = id_manual;
    }

    public String getNama_bel_manual() {
        return nama_bel_manual;
    }

    public void setNama_bel_manual(String nama_bel_manual) {
        this.nama_bel_manual = nama_bel_manual;
    }

    public String getRingtone_manual() {
        return ringtone_manual;
    }

    public void setRingtone_manual(String ringtone_manual) {
        this.ringtone_manual = ringtone_manual;
    }

    public int getDurasi_manual() {
        return durasi_manual;
    }

    public void setDurasi_manual(int durasi_manual) {
        this.durasi_manual = durasi_manual;
    }
}
