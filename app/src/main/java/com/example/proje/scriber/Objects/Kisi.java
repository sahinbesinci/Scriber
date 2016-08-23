package com.example.proje.scriber.Objects;

/**
 * Created by sahin on 9.05.2016.
 */
public class Kisi {
    private String UyeID;
    private String AdSoyad;
    private String Email;

    public Kisi(String uyeID, String adSoyad, String email)
    {
        this.UyeID = uyeID;
        this.AdSoyad = adSoyad;
        this.Email = email;
    }
    public void setUyeID(String uyeID){this.UyeID = uyeID;}
    public String getUyeID(){return UyeID;}
    public void setAdSoyad(String adSoyad){this.AdSoyad = adSoyad;}
    public String getAdSoyad(){return AdSoyad;}
    public void setEmail(String email){this.Email = email;}
    public String getEmail(){return Email;}
}
