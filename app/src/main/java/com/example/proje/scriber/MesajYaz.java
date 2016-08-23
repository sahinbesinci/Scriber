package com.example.proje.scriber;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.proje.scriber.Objects.Mesaj;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.emitter.Emitter;

/**
 * Created by sahin on 9.05.2016.
 */
public class MesajYaz extends AppCompatActivity {
    EditText yazi;
    ArrayList<String> yazisma;
    ArrayAdapter arrayAdapter;
    ListView lvYazisma;
    Button btnGonder;
    private io.socket.client.Socket mSocket;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mesaj_yaz_layout);
        bilesenleri_yukle();
        mSocket.emit("konusmalariGetir", getKonusmaBilgileri());
        mSocket.on("konusmalariGetir", konusmalarListener);
        mSocket.on("mesajGetir",mesajGetir);
    }
    private Emitter.Listener konusmalarListener = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            MesajYaz.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONArray obj = (JSONArray) args[0];
                    for(int i=0;i<obj.length();i++)
                    {
                        try {
                            JSONObject json = obj.getJSONObject(i);
                            String AdSoyad = json.getString("AdSoyad");
                            String Mesaj = json.getString("Mesaj");
                            yazisma.add(AdSoyad + ": " + Mesaj);
                            Log.v("AdSoyad: ", AdSoyad);
                            Log.v("Mesaj: ",Mesaj);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    arrayAdapter.notifyDataSetChanged();
                }
            });
        }
    };
    private Emitter.Listener mesajGetir = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            MesajYaz.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject json = (JSONObject) args[0];
                    try {
                        String AdSoyad = json.getString("AdSoyad");
                        String Mesaj = json.getString("Mesaj");
                        yazisma.add(AdSoyad + ": " + Mesaj);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    arrayAdapter.notifyDataSetChanged();
                }
            });
        }
    };
    private View.OnClickListener gonder = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            JSONObject obj = getKonusmaBilgileri();
            try {
                obj.put("Mesaj",yazi.getText().toString());
                obj.put("AdSoyad",getIntent().getExtras().getString("AdSoyad"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mSocket.emit("mesajGonder",obj);
            yazi.setText("");
        }
    };

    @Override
    public void onBackPressed() {
        mSocket.emit("pencereyiKapat", getKonusmaBilgileri());
        Intent i = new Intent(MesajYaz.this,Konusma.class);
        i.putExtra("UyeID",getIntent().getExtras().getString("UyeID"));
        i.putExtra("AdSoyad",getIntent().getExtras().getString("AdSoyad"));
        i.putExtra("Email", getIntent().getExtras().getString("Email"));
        Log.v("MesajYaz __ UyeID-AdSoyad-Email: ", getIntent().getExtras().getString("UyeID") + "-" + getIntent().getExtras().getString("AdSoyad") + "-" + getIntent().getExtras().getString("Email"));
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    private JSONObject getKonusmaBilgileri()  {
        JSONObject json = new JSONObject();
        try {
            json.put("KonusmaID", getIntent().getExtras().getString("KonusmaID"));
            json.put("UyeID", getIntent().getExtras().getString("UyeID"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
    private void bilesenleri_yukle()
    {
        mSocket = SocketIOClient.getInstance(this);
        lvYazisma = (ListView) findViewById(R.id.lvChat);
        btnGonder = (Button) findViewById(R.id.btnGonder);
        btnGonder.setOnClickListener(gonder);
        yazisma = new ArrayList<>();
        arrayAdapter = new ArrayAdapter(getApplicationContext(),android.R.layout.simple_list_item_1, yazisma);
        lvYazisma.setAdapter(arrayAdapter);
        yazi = (EditText) findViewById(R.id.etYazi);
    }

}
