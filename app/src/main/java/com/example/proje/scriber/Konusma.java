package com.example.proje.scriber;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.proje.scriber.Adapter.MesajBaseAdapter;
import com.example.proje.scriber.Adapter.PostBaseAdapter;
import com.example.proje.scriber.Objects.Mesaj;
import com.example.proje.scriber.Objects.Posts;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.emitter.Emitter;

/**
 * Created by sahin on 9.05.2016.
 */
public class Konusma extends AppCompatActivity {
    ListView lvMesajlar;
    MesajBaseAdapter mesajBaseAdapter;
    ArrayList<Mesaj> mesajlar;
    private io.socket.client.Socket mSocket;
    int lvPosition=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.konusmalar_layout);
        bilesenleri_yukle();
        mSocket.emit("konusmalar", getKayitBilgileri());
        mSocket.on("konusmalar", konusmalarListener);
        mSocket.on("konusmaSilindi",konusmaSil);
        mSocket.on("konusmaEkle",konusmaEkle);
    }
    private Emitter.Listener konusmalarListener = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Konusma.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONArray obj = (JSONArray) args[0];
                    for(int i=0;i<obj.length();i++)
                    {
                        try {
                            JSONObject json = obj.getJSONObject(i);
                            String KonusmaID = json.getString("KonusmaID");
                            String KonusmaAdi = json.getString("AdSoyad");
                            mesajlar.add(new Mesaj(KonusmaID,KonusmaAdi));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    mesajBaseAdapter.notifyDataSetChanged();
                }
            });
        }
    };
    private Emitter.Listener konusmaEkle = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Konusma.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONArray obj = (JSONArray) args[0];
                    for (int i=0;i<obj.length();i++)
                    {
                        try {
                            JSONObject json = obj.getJSONObject(i);
                            if (!getIntent().getExtras().getString("UyeID").equals(json.getString("UyeID")))
                            {
                                String KonusmaID = json.getString("KonusmaID");
                                String KonusmaAdi = getIntent().getExtras().getString("AdSoyad");
                                mesajlar.add(0,new Mesaj(KonusmaID,KonusmaAdi));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    mesajBaseAdapter.notifyDataSetChanged();
                }
            });
        }
    };
    private Emitter.Listener konusmaSil = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Konusma.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject json = (JSONObject) args[0];
                    try {
                        String KonusmaID = json.getString("KonusmaID");
                        for (int i=0;i<mesajlar.size();i++)
                        {
                            if (mesajlar.get(i).getKonusmaID().equals(KonusmaID))
                            {
                                mesajlar.remove(i);
                                break;
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mesajBaseAdapter.notifyDataSetChanged();
                }
            });
        }
    };
    private AdapterView.OnItemClickListener lvMesajlarItem = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Mesaj mesaj = (Mesaj) (lvMesajlar.getItemAtPosition(position));
            Intent i = new Intent(Konusma.this,MesajYaz.class);
            i.putExtra("UyeID",getIntent().getExtras().getString("UyeID"));
            i.putExtra("AdSoyad",getIntent().getExtras().getString("AdSoyad"));
            i.putExtra("Email", getIntent().getExtras().getString("Email"));
            i.putExtra("KonusmaID",mesaj.getKonusmaID());
            startActivity(i);
        }
    };

    private AdapterView.OnItemLongClickListener secenekler = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Konusma.this);

            // set title
            alertDialogBuilder.setTitle("Konuşmayı Sil");

            // set dialog message
            alertDialogBuilder
                    .setMessage("Silmek İstediğinizden Emin misiniz ?")
                    .setCancelable(false)
                    .setPositiveButton("Evet",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {

                            JSONObject json = new JSONObject();
                            try {
                                json.put("KonusmaID",mesajlar.get(lvPosition).getKonusmaID());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            mSocket.emit("mesajSil", json);
                        }
                    })
                    .setNegativeButton("Hayır",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            dialog.cancel();
                        }
                    });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
            return true;
        }
    };

    private void bilesenleri_yukle()
    {
        mSocket = SocketIOClient.getInstance(this);
        lvMesajlar = (ListView) findViewById(R.id.lvmesajlar);
        mesajlar = new ArrayList<>();
        mesajBaseAdapter = new MesajBaseAdapter(getApplicationContext(),mesajlar);
        lvMesajlar.setAdapter(mesajBaseAdapter);
        lvMesajlar.setOnItemClickListener(lvMesajlarItem);
        lvMesajlar.setOnItemLongClickListener(secenekler);
    }

    private JSONObject getKayitBilgileri()  {
        JSONObject json = new JSONObject();
        try {
            json.put("UyeID", getIntent().getExtras().getString("UyeID"));
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
            i = new Intent(Konusma.this,Profil.class);
        } else if (item.getItemId() == R.id.anasayfa) {
            i = new Intent(Konusma.this,Anasayfa.class);
        } else if (item.getItemId() == R.id.ara) {
            i = new Intent(Konusma.this,Arama.class);
        }
        i.putExtra("UyeID",getIntent().getExtras().getString("UyeID"));
        i.putExtra("AdSoyad",getIntent().getExtras().getString("AdSoyad"));
        i.putExtra("Email", getIntent().getExtras().getString("Email"));
        if (!(item.getItemId() == R.id.mesaj))
            startActivity(i);
        return super.onOptionsItemSelected(item);
    }
}
