package com.example.proje.scriber;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
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
public class Anasayfa extends AppCompatActivity {
    ImageView mesajYaz;
    ListView posts;
    ArrayList<Posts> postList;
    PostBaseAdapter postBaseAdapter;
    private io.socket.client.Socket mSocket;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anasayfa_layout);
        bilesenleri_yukle();

        try {
            mSocket.emit("postlariGetir", getKayitBilgileri());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.on("postlar",gelenPostlar);
        mSocket.on("yeniPost",yeniPost);
        mSocket.on("postSilindi",postSil);
    }

    private Emitter.Listener gelenPostlar = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Anasayfa.this.runOnUiThread(new Runnable() {
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
                            postList.add(new Posts(postID, uyeID, post, adSoyad));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    postBaseAdapter = new PostBaseAdapter(getApplicationContext(),postList);
                    posts.setAdapter(postBaseAdapter);
                }
            });
        }
    };
    private Emitter.Listener yeniPost = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Anasayfa.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject json = (JSONObject) args[0];
                    try {
                        String postID = json.getString("PostID");
                        String uyeID = json.getString("UyeID");
                        String post = json.getString("Post");
                        String adSoyad = json.getString("AdSoyad");
                        if(postList.size() != 0)
                            postList.add(0,new Posts(postID,uyeID,post,adSoyad));
                        else
                        {
                            postList.add(new Posts(postID, uyeID, post, adSoyad));
                            postBaseAdapter = new PostBaseAdapter(getApplicationContext(),postList);
                            posts.setAdapter(postBaseAdapter);
                        }
                        postBaseAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };
    private Emitter.Listener postSil = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Anasayfa.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject json = (JSONObject) args[0];
                    try {
                        String postID = json.getString("PostID");
                        Toast.makeText(getApplicationContext(),postID,Toast.LENGTH_SHORT).show();
                        for (int i=0;i<postList.size();i++)
                        {
                            if (postList.get(i).getPostID().equals(postID))
                            {
                                postList.remove(i);
                                break;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    postBaseAdapter.notifyDataSetChanged();
                }
            });
        }
    };
    private JSONObject getKayitBilgileri() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("UyeID",getIntent().getExtras().getString("UyeID"));
        json.put("AdSoyad",getIntent().getExtras().getString("AdSoyad"));
        return json;
    }

    private void bilesenleri_yukle()
    {
        mSocket = SocketIOClient.getInstance(this);
        mesajYaz = (ImageView) findViewById(R.id.imgMesajYaz);
        mesajYaz.setOnClickListener(mesajYazClick);
        posts = (ListView) findViewById(R.id.posts);
        postList = new ArrayList<>();
    }
    private View.OnClickListener mesajYazClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(Anasayfa.this,PostYaz.class);
            i.putExtra("UyeID",getIntent().getExtras().getString("UyeID"));
            i.putExtra("Email",getIntent().getExtras().getString("Email"));
            i.putExtra("AdSoyad",getIntent().getExtras().getString("AdSoyad"));
            startActivity(i);
        }
    };

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Anasayfa.this);

        alertDialogBuilder.setTitle("Çıkış");

        alertDialogBuilder
                .setMessage("Çıkış yapmak istiyor musunuz?")
                .setCancelable(false)
                .setPositiveButton("Evet",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        try {
                            mSocket.emit("cikis",getKayitBilgileri());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Intent i = new Intent(Anasayfa.this, Giris.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }
                })
                .setNegativeButton("Hayır",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
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
            i = new Intent(Anasayfa.this,Profil.class);
        }else if (item.getItemId() == R.id.mesaj) {
            i = new Intent(Anasayfa.this,Konusma.class);
        } else if (item.getItemId() == R.id.ara) {
            i = new Intent(Anasayfa.this,Arama.class);
        }
        i.putExtra("UyeID",getIntent().getExtras().getString("UyeID"));
        i.putExtra("AdSoyad",getIntent().getExtras().getString("AdSoyad"));
        i.putExtra("Email", getIntent().getExtras().getString("Email"));
        if (!(item.getItemId() == R.id.anasayfa))
            startActivity(i);
        return super.onOptionsItemSelected(item);
    }
}
