package com.example.proje.scriber.Objects;

/**
 * Created by sahin on 9.05.2016.
 */
public class Mesaj {
    private String KonusmaID;
    private String KonusmaAdi;

    public Mesaj(String konusmaID,String konusmaAdi)
    {
        this.KonusmaAdi = konusmaAdi;
        this.KonusmaID = konusmaID;
    }
    public void setKonusmaID(String konusmaID){this.KonusmaID = konusmaID;}
    public String getKonusmaID(){return KonusmaID;}
    public void setKonusmaAdi(String konusmaAdi){this.KonusmaAdi = konusmaAdi;}
    public String getKonusmaAdi(){return KonusmaAdi;}
}
