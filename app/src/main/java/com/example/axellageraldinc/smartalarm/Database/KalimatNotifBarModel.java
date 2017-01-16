package com.example.axellageraldinc.smartalarm.Database;

/**
 * Created by Axellageraldinc on 12-Jan-17.
 */

public class KalimatNotifBarModel {
    private String kalimat;

    public KalimatNotifBarModel(){
        this.kalimat="";
    }

    public KalimatNotifBarModel(String kalimat){
        this.kalimat=kalimat;
    }

    public String getKalimat() {
        return kalimat;
    }

    public void setKalimat(String kalimat) {
        this.kalimat = kalimat;
    }
}
