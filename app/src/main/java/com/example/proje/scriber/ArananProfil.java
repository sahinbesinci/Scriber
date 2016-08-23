package com.example.proje.scriber;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proje.scriber.Adapter.PostBaseAdapter;
import com.example.proje.scriber.Objects.Posts;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.emitter.Emitter;

/**
 * Created by sahin on 9.05.2016.
 */
public class ArananProfil extends AppCompatActivity {
    TextView AdSoyad,Email;
    Button takipci,takipEttigi,takipet,mesajYaz;
    ListView lvPosts;
    PostBaseAdapter postBaseAdapter;
    ArrayList<Posts> postlar;
    private io.socket.client.Socket mSocket;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aranan_profil);
        bilesenleri_yukle();
        mSocket.emit("arananTakipciSayisi", getProfilBilgileri());
        mSocket.emit("arananTakipSayisi", getProfilBilgileri());
        mSocket.emit("arananUyePostlari", getProfilBilgileri());
        mSocket.emit("takipEdildimi", getProfilBilgileri());
        mSocket.on("arananTakipciSayisi", takipciSay);
        mSocket.on("arananTakipSayisi", takipSay);
        mSocket.on("arananUyePostlari", postlariGetir);
        mSocket.on("takipEdildimi",takipDurumu);
        mSocket.on("takip",takip);
        mSocket.on("konusmaBaslat",konusmaBaslat);
    }
    private Emitter.Listener konusmaBaslat = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ArananProfil.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONArray obj = (JSONArray) args[0];
                    for (int i=0;i<obj.length();i++)
                    {
                        try {
                            JSONObject json = obj.getJSONObject(i);
                            if (getIntent().getExtras().getString("UyeID").equals(json.getString("UyeID")))
                            {
                                String KonusmaID = json.getString("KonusmaID");
                                String AdSoyad = getIntent().getExtras().getString("hesapAdi");
                                String Email = getIntent().getExtras().getString("hesapEmail");
                                String UyeID = getIntent().getExtras().getString("UyeID");
                                Intent intent = new Intent(ArananProfil.this,MesajYaz.class);
                                intent.putExtra("KonusmaID",KonusmaID);
                                intent.putExtra("AdSoyad",AdSoyad);
                                intent.putExtra("Email",Email);
                                intent.putExtra("UyeID",UyeID);
                                startActivity(intent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    };
    private Emitter.Listener takipciSay = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ArananProfil.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject json = (JSONObject) args[0];
                    try {
                        String TakipciSayisi = json.getString("TakipciSayisi");
                        takipci.setText("Takipçi Sayısı: " + TakipciSayisi);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };
    private Emitter.Listener takipSay = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ArananProfil.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject json = (JSONObject) args[0];
                    try {
                        String TakipSayisi = json.getString("TakipSayisi");
                        takipEttigi.setText("Takip Sayısı: " + TakipSayisi);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };
    private Emitter.Listener postlariGetir = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ArananProfil.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONArray obj = (JSONArray) args[0];
                    for(int i=0;i<obj.length();i++)
                    {
                        try {
                            JSONObject json = obj.getJSONObject(i);
                            String postID = json.getString("PostID");
                            String uyeID = json.getString("UyeID");
                            String post = json.getString("Post");
                            String adSoyad = json.getString("AdSoyad");
                            postlar.add(0,new Posts(postID,uyeID,post,adSoyad));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    postBaseAdapter.notifyDataSetChanged();
                }
            });
        }
    };
    private Emitter.Listener takipDurumu = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ArananProfil.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject json = (JSONObject) args[0];
                    try {
                        String TakipDurumu = json.getString("TakipDurumu");
                        if(TakipDurumu.equals("0"))
                            takipet.setText("EKLE");
                        else
                            takipet.setText("ÇIKAR");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };
    private Emitter.Listener takip= new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ArananProfil.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mSocket.emit("takipSayisi", getProfilBilgileri());
                    mSocket.emit("takipciSayisi", getProfilBilgileri());
                    mSocket.emit("arananTakipciSayisi", getProfilBilgileri());
                    mSocket.emit("arananTakipSayisi", getProfilBilgileri());
                }
            });
        }
    };

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
    private void bilesenleri_yukle()
    {
        mSocket = SocketIOClient.getInstance(this);
        AdSoyad = (TextView) findViewById(R.id.tvAdSoyad);
        AdSoyad.setText(getIntent().getExtras().getString("AdSoyad"));
        Email = (TextView) findViewById(R.id.tvEmail);
        Email.setText(getIntent().getExtras().getString("Email"));
        takipci = (Button) findViewById(R.id.btnTakipciSayisi);
        takipci.setOnClickListener(takipcibtn);
        takipEttigi = (Button) findViewById(R.id.btnTakipSayisi);
        takipEttigi.setOnClickListener(takipEttigibtn);
        takipet = (Button) findViewById(R.id.btnTakip);
        takipet.setOnClickListener(takipetbtn);
        mesajYaz = (Button) findViewById(R.id.btnMesaj);
        mesajYaz.setOnClickListener(mesajYazbtn);
        lvPosts = (ListView) findViewById(R.id.posts);
        postlar = new ArrayList<>();
        postBaseAdapter = new PostBaseAdapter(getApplicationContext(),postlar);
        lvPosts.setAdapter(postBaseAdapter);
    }
    private View.OnClickListener takipcibtn = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(ArananProfil.this,ArananTakip.class);
            i.putExtra("Takip","Takipci");
            i.putExtra("UyeID",getIntent().getExtras().getString("UyeID"));
            i.putExtra("arananUyeID",getIntent().getExtras().getString("arananUyeID"));
            i.putExtra("hesapAdi",getIntent().getExtras().getString("AdSoyad"));
            i.putExtra("hesapEmail",getIntent().getExtras().getString("Email"));
            startActivity(i);
        }
    };
    private View.OnClickListener takipEttigibtn = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(ArananProfil.this,ArananTakip.class);
            i.putExtra("Takip","TakipEdilen");
            i.putExtra("UyeID",getIntent().getExtras().getString("UyeID"));
            i.putExtra("arananUyeID",getIntent().getExtras().getString("arananUyeID"));
            i.putExtra("hesapAdi",getIntent().getExtras().getString("hesapAdi"));
            i.putExtra("hesapEmail",getIntent().getExtras().getString("hesapEmail"));
            startActivity(i);
        }
    };
    private View.OnClickListener takipetbtn = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(takipet.getText().equals("EKLE"))
            {
                takipet.setText("ÇIKAR");
                mSocket.emit("takipEkle", getProfilBilgileri());
            } else
            {
                takipet.setText("EKLE");
                mSocket.emit("takipCikar", getProfilBilgileri());
            }
        }
    };
    private View.OnClickListener mesajYazbtn = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            JSONObject json = new JSONObject();
            try {
                json.put("UyeID",getIntent().getExtras().getString("UyeID"));
                json.put("KonusmaciID",getIntent().getExtras().getString("arananUyeID"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mSocket.emit("konusmaOlustur",json);
        }
    };
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
            i = new Intent(ArananProfil.this,Profil.class);
        } else if (item.getItemId() == R.id.anasayfa) {
            i = new Intent(ArananProfil.this,Anasayfa.class);
        } else if (item.getItemId() == R.id.mesaj) {
            i = new Intent(ArananProfil.this,Konusma.class);
        } else if (item.getItemId() == R.id.ara ) {
            i = new Intent(ArananProfil.this,Arama.class);
        }
        i.putExtra("UyeID",getIntent().getExtras().getString("UyeID"));
        i.putExtra("AdSoyad",getIntent().getExtras().getString("hesapAdi"));
        i.putExtra("Email",getIntent().getExtras().getString("hesapEmail"));
        startActivity(i);
        return super.onOptionsItemSelected(item);
    }
}
