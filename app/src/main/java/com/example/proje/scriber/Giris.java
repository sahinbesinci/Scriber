package com.example.proje.scriber;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.emitter.Emitter;

/**
 * Created by sahin on 9.05.2016.
 */
public class Giris extends AppCompatActivity {
    EditText Email,Sifre;
    Button btnGiris, btnKayit;
    private io.socket.client.Socket mSocket;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.giris_layout);
        bilesenleri_yukle();
        mSocket.on("girisyap",girisyap);
        mSocket.connect();
    }
    private Emitter.Listener girisyap = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Giris.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject json = (JSONObject) args[0];
                    if (json != null) {
                        try {
                            Intent intent = new Intent(Giris.this, Anasayfa.class);
                            intent.putExtra("AdSoyad", (String) json.get("AdSoyad"));
                            intent.putExtra("UyeID", (String) json.get("UyeID"));
                            intent.putExtra("Email", (String) json.get("Email"));
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    };
    private void bilesenleri_yukle()
    {
        mSocket = SocketIOClient.getInstance(this);
        Email = (EditText) findViewById(R.id.etEmail);
        Sifre = (EditText) findViewById(R.id.etSifre);
        btnGiris = (Button) findViewById(R.id.btnGiris);
        btnKayit = (Button) findViewById(R.id.btnKayitOl);
        btnGiris.setOnClickListener(giris);
        btnKayit.setOnClickListener(kayitol);
    }
    private View.OnClickListener giris = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                mSocket.emit("uyeGirisi",getKayitBilgileri());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    private View.OnClickListener kayitol = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(Giris.this,Kayit.class);
            startActivity(i);
        }
    };
    private JSONObject getKayitBilgileri() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("Email",Email.getText());
        json.put("Sifre",Sifre.getText());
        return json;
    }
}
