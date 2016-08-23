package com.example.proje.scriber;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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
public class Profil extends AppCompatActivity {
    TextView AdSoyad,Email;
    Button btnTakipciSayisi,btnTakipSayisi;
    ListView lvPost;
    ArrayList<Posts> postList;
    PostBaseAdapter postBaseAdapter;
    int lvPosition=0;
    private io.socket.client.Socket mSocket;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profil_layout);
        bilesenleri_yukle();
        mSocket.emit("takipciSayisi", getProfilBilgileri());
        mSocket.emit("takipSayisi", getProfilBilgileri());
        mSocket.emit("uyePostlari",getProfilBilgileri());
        mSocket.on("takipciSayisi", takipciSay);
        mSocket.on("takipSayisi", takipSay);
        mSocket.on("uyePostlari", postlar);
        mSocket.on("profilYeniPost", yeniPost);
    }
    private Emitter.Listener takipciSay = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Profil.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject json = (JSONObject) args[0];
                    try {
                        String TakipciSayisi = json.getString("TakipciSayisi");
                        btnTakipciSayisi.setText("Takipçi Sayısı: " + TakipciSayisi);
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
            Profil.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject json = (JSONObject) args[0];
                    try {
                        String TakipSayisi = json.getString("TakipSayisi");
                        btnTakipSayisi.setText("Takip Sayısı: " + TakipSayisi);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };
    private Emitter.Listener postlar = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Profil.this.runOnUiThread(new Runnable() {
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
                            postList.add(new Posts(postID,uyeID,post,adSoyad));
                            postBaseAdapter = new PostBaseAdapter(getApplicationContext(),postList);
                            lvPost.setAdapter(postBaseAdapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    };
    private Emitter.Listener yeniPost = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Profil.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject json = (JSONObject) args[0];
                    try {
                        String postID = json.getString("PostID");
                        String uyeID = json.getString("UyeID");
                        String post = json.getString("Post");
                        String adSoyad = json.getString("AdSoyad");
                        if (postList.size() == 0)
                            postList.add(new Posts(postID,uyeID,post,adSoyad));
                        else
                            postList.add(0,new Posts(postID,uyeID,post,adSoyad));
                        postBaseAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private void bilesenleri_yukle()
    {
        mSocket = SocketIOClient.getInstance(this);
        AdSoyad = (TextView) findViewById(R.id.tvAdSoyad);
        AdSoyad.setText(getIntent().getExtras().getString("AdSoyad"));
        Email = (TextView) findViewById(R.id.tvEmail);
        Email.setText(getIntent().getExtras().getString("Email"));
        btnTakipciSayisi = (Button) findViewById(R.id.btnTakipciSayisi);
        btnTakipciSayisi.setOnClickListener(takipciSayisi);
        btnTakipSayisi = (Button) findViewById(R.id.btnTakipSayisi);
        btnTakipSayisi.setOnClickListener(takipSayisi);
        lvPost = (ListView) findViewById(R.id.posts);
        lvPost.setOnItemClickListener(secilenListeElemani);
        lvPost.setOnItemLongClickListener(secenekler);
        postList = new ArrayList<>();
        registerForContextMenu(lvPost);
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        menu.add(0, v.getId(), 0, "Sil");
    }

    public boolean onContextItemSelected(MenuItem item){
        if(item.getTitle()=="Sil")
        {
            JSONObject json = new JSONObject();
            try {
                json.put("PostID",postList.get(lvPosition).getPostID());
                json.put("UyeID",getIntent().getExtras().getString("UyeID"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mSocket.emit("postSil",json);
            postList.remove(lvPosition);
            postBaseAdapter.notifyDataSetChanged();
        }
        return true;
    }
    private AdapterView.OnItemLongClickListener secenekler = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            lvPosition = position;
            Log.v("Profil __ UyeID-AdSoyad-Email: ", getIntent().getExtras().getString("UyeID") + "-" + getIntent().getExtras().getString("AdSoyad") + "-" + getIntent().getExtras().getString("Email"));

            return false;
        }
    };
    private AdapterView.OnItemClickListener secilenListeElemani = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent i = new Intent(Profil.this,Post.class);
            i.putExtra("AdSoyad",postList.get(position).getAdSoyad());
            i.putExtra("UyeID",postList.get(position).getUyeID());
            i.putExtra("Email",getIntent().getExtras().getString("Email"));
            i.putExtra("Post",postList.get(position).getPost());
            startActivity(i);
        }
    };
    private View.OnClickListener takipciSayisi = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(Profil.this,Takip.class);
            i.putExtra("Takip","Takipci");
            i.putExtra("AdSoyad",getIntent().getExtras().getString("AdSoyad"));
            i.putExtra("Email",getIntent().getExtras().getString("Email"));
            i.putExtra("UyeID", getIntent().getExtras().getString("UyeID"));
            startActivity(i);
        }
    };
    private View.OnClickListener takipSayisi = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(Profil.this,Takip.class);
            i.putExtra("Takip","TakipEdilen");
            i.putExtra("AdSoyad",getIntent().getExtras().getString("AdSoyad"));
            i.putExtra("Email",getIntent().getExtras().getString("Email"));
            i.putExtra("UyeID",getIntent().getExtras().getString("UyeID"));
            startActivity(i);
            Toast.makeText(getApplicationContext(),"TakipSayisi tıklandı",Toast.LENGTH_SHORT).show();
        }
    };

    private JSONObject getProfilBilgileri() {
        JSONObject json = new JSONObject();
        try {
            json.put("UyeID",getIntent().getExtras().getString("UyeID"));
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
        if (item.getItemId() == R.id.anasayfa) {
            i = new Intent(Profil.this,Anasayfa.class);
        } else if (item.getItemId() == R.id.mesaj) {
            i = new Intent(Profil.this,Konusma.class);
        } else if (item.getItemId() == R.id.ara) {
            i = new Intent(Profil.this,Arama.class);
        }
        i.putExtra("UyeID",getIntent().getExtras().getString("UyeID"));
        i.putExtra("AdSoyad",getIntent().getExtras().getString("AdSoyad"));
        i.putExtra("Email", getIntent().getExtras().getString("Email"));
        if (!(item.getItemId() == R.id.profil))
            startActivity(i);
        return super.onOptionsItemSelected(item);
    }
}
