package com.example.proje.scriber;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by sahin on 14.05.2016.
 */
public class Post extends AppCompatActivity {
    TextView AdSoyad,Post;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_onizleme_layout);
        bilesenleri_yukle();
    }
    private void bilesenleri_yukle()
    {
        AdSoyad = (TextView) findViewById(R.id.tvAdSoyad);
        AdSoyad.setText(getIntent().getExtras().getString("AdSoyad"));
        Post = (TextView) findViewById(R.id.tvPost);
        Post.setText(getIntent().getExtras().getString("Post"));

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
            i = new Intent(Post.this,Profil.class);
        } else if (item.getItemId() == R.id.anasayfa) {
            i = new Intent(Post.this,Anasayfa.class);
        } else if (item.getItemId() == R.id.mesaj) {
            i = new Intent(Post.this,Konusma.class);
        } else if (item.getItemId() == R.id.ara ) {
            i = new Intent(Post.this,Arama.class);
        }
        i.putExtra("UyeID",getIntent().getExtras().getString("UyeID"));
        i.putExtra("AdSoyad",getIntent().getExtras().getString("AdSoyad"));
        i.putExtra("Email", getIntent().getExtras().getString("Email"));
        startActivity(i);
        return super.onOptionsItemSelected(item);
    }
}
