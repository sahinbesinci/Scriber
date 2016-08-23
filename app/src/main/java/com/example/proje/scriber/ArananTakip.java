package com.example.proje.scriber;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.proje.scriber.Adapter.KisiBaseAdapter;
import com.example.proje.scriber.Adapter.PostBaseAdapter;
import com.example.proje.scriber.Objects.Kisi;
import com.example.proje.scriber.Objects.Posts;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.emitter.Emitter;

/**
 * Created by sahin on 9.05.2016.
 */
public class ArananTakip extends AppCompatActivity {
    ListView lvTakip;
    EditText ara;
    Button btnAra;

    ArrayList<Kisi> Kisiler;
    KisiBaseAdapter kisiBaseAdapter;
    private io.socket.client.Socket mSocket;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aranan_takip_layout);
        bilesenleri_yukle();
        if (getIntent().getExtras().getString("Takip").equals("Takipci"))
            mSocket.emit("arananTakipcileriGetir", getProfilBilgileri());
        else
            mSocket.emit("arananTakipEdilenleriGetir", getProfilBilgileri());
        mSocket.on("arananKisiler",kisiler);
    }
    private Emitter.Listener kisiler = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ArananTakip.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONArray obj = (JSONArray) args[0];
                    for(int i=0;i<obj.length();i++)
                    {
                        try {
                            JSONObject json = obj.getJSONObject(i);
                            String uyeID = json.getString("UyeID");
                            String adSoyad = json.getString("AdSoyad");
                            String Email = json.getString("Email");
                            Kisiler.add(new Kisi(uyeID, adSoyad, Email));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    kisiBaseAdapter.notifyDataSetChanged();
                }
            });
        }
    };
    private AdapterView.OnItemClickListener takipListesi = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Kisi kisi = (Kisi) (lvTakip.getItemAtPosition(position));
            if(kisi.getUyeID().equals(getIntent().getExtras().getString("UyeID")))
            {
                Intent i = new Intent(ArananTakip.this,Profil.class);
                i.putExtra("UyeID",kisi.getUyeID());
                i.putExtra("AdSoyad",kisi.getAdSoyad());
                i.putExtra("Email", kisi.getEmail());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
            else {
                Intent i = new Intent(ArananTakip.this, ArananProfil.class);
                i.putExtra("AdSoyad",kisi.getAdSoyad());
                i.putExtra("Email",kisi.getEmail());
                i.putExtra("arananUyeID",kisi.getUyeID());
                i.putExtra("hesapAdi",getIntent().getExtras().getString("hesapAdi"));
                i.putExtra("hesapEmail",getIntent().getExtras().getString("hesapEmail"));
                i.putExtra("UyeID",getIntent().getExtras().getString("UyeID"));
                startActivity(i);
                Toast.makeText(getApplicationContext(),kisi.getUyeID()+"\n"+kisi.getAdSoyad()+"\n"+kisi.getEmail(),Toast.LENGTH_SHORT).show();
            }
        }
    };
    private View.OnClickListener arama = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ArananTakip.this.kisiBaseAdapter.getFilter().filter(ara.getText().toString());
        }
    };
    private void bilesenleri_yukle()
    {
        mSocket = SocketIOClient.getInstance(this);
        lvTakip = (ListView) findViewById(R.id.lvAranantakip);
        lvTakip.setOnItemClickListener(takipListesi);
        ara = (EditText) findViewById(R.id.etArananAra);
        btnAra = (Button) findViewById(R.id.btnArananAra);
        btnAra.setOnClickListener(arama);
        Kisiler = new ArrayList<>();
        kisiBaseAdapter = new KisiBaseAdapter(getApplicationContext(),Kisiler);
        lvTakip.setAdapter(kisiBaseAdapter);
    }
    private JSONObject getProfilBilgileri() {
        JSONObject json = new JSONObject();
        try {
            json.put("UyeID",getIntent().getExtras().getString("UyeID"));
            json.put("arananUyeID",getIntent().getExtras().getString("arananUyeID"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent i = new Intent();
        if (item.getItemId() == R.id.profil) {
            i = new Intent(ArananTakip.this,Profil.class);
        } else if (item.getItemId() == R.id.anasayfa) {
            i = new Intent(ArananTakip.this,Anasayfa.class);
        } else if (item.getItemId() == R.id.mesaj) {
            i = new Intent(ArananTakip.this,Konusma.class);
        } else if (item.getItemId() == R.id.ara ) {
            i = new Intent(ArananTakip.this,Arama.class);
        }
        i.putExtra("UyeID",getIntent().getExtras().getString("UyeID"));
        i.putExtra("AdSoyad",getIntent().getExtras().getString("hesapAdi"));
        i.putExtra("Email",getIntent().getExtras().getString("hesapEmail"));
        startActivity(i);
        return super.onOptionsItemSelected(item);
    }
}
