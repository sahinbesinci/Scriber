package com.example.proje.scriber;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sahin on 9.05.2016.
 */
public class PostYaz extends AppCompatActivity {
    EditText etPost;
    Button btnPaylas;
    private io.socket.client.Socket mSocket;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_yaz_layout);
        bilesenleri_yukle();
    }
    private View.OnClickListener paylas = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                mSocket.emit("postGonder",getPost());
                Toast.makeText(getApplicationContext(),"Yazınız gönderildi",Toast.LENGTH_SHORT).show();
                onBackPressed();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private void bilesenleri_yukle()
    {
        mSocket = SocketIOClient.getInstance(this);
        etPost = (EditText) findViewById(R.id.etPost);
        btnPaylas = (Button) findViewById(R.id.btnPaylas);
        btnPaylas.setOnClickListener(paylas);
    }

    private JSONObject getPost() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("Post", etPost.getText().toString());
        json.put("UyeID", getIntent().getExtras().getString("UyeID"));
        json.put("AdSoyad", getIntent().getExtras().getString("AdSoyad"));
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
            i = new Intent(PostYaz.this,Profil.class);
        } else if (item.getItemId() == R.id.anasayfa) {
            i = new Intent(PostYaz.this,Anasayfa.class);
        } else if (item.getItemId() == R.id.mesaj) {
            i = new Intent(PostYaz.this,Konusma.class);
        } else if (item.getItemId() == R.id.ara) {
            i = new Intent(PostYaz.this,Arama.class);
        }
        i.putExtra("UyeID",getIntent().getExtras().getString("UyeID"));
        i.putExtra("AdSoyad",getIntent().getExtras().getString("AdSoyad"));
        i.putExtra("Email", getIntent().getExtras().getString("Email"));
        startActivity(i);
        return super.onOptionsItemSelected(item);
    }
}
