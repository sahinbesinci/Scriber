package com.example.proje.scriber;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.emitter.Emitter;

/**
 * Created by sahin on 9.05.2016.
 */
public class Kayit extends AppCompatActivity {
    EditText AdSoyad,Email,Sifre;
    Button btnKayitOl;
    TextView Uyari;
    private io.socket.client.Socket mSocket;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kayit_layout);
        bilesenleri_yukle();
        mSocket.on("girisyap",girisyap);
        mSocket.connect();
    }

    private Emitter.Listener girisyap = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Kayit.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject json = (JSONObject) args[0];
                    if (json != null) {
                        try {
                            Intent intent = new Intent(Kayit.this, Anasayfa.class);
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

    // Button Listener
    private View.OnClickListener kayitOl = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                mSocket.emit("uyeKayit", getKayitBilgileri());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    private JSONObject getKayitBilgileri() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("AdSoyad",AdSoyad.getText());
        json.put("Email",Email.getText());
        json.put("Sifre",Sifre.getText());
        return json;
    }

    private void bilesenleri_yukle()
    {
        mSocket = SocketIOClient.getInstance(this);
        AdSoyad = (EditText) findViewById(R.id.etAdSoyadKayit);
        Email = (EditText) findViewById(R.id.etEmailKayit);
        Sifre = (EditText) findViewById(R.id.etSifreKayit);
        Uyari = (TextView) findViewById(R.id.tvUyari);
        btnKayitOl = (Button) findViewById(R.id.btnKayitOl);
        btnKayitOl.setOnClickListener(kayitOl);
    }
}
