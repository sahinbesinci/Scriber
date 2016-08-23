package com.example.proje.scriber.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.proje.scriber.Objects.Posts;
import com.example.proje.scriber.R;

import java.util.ArrayList;

/**
 * Created by sahin on 9.05.2016.
 */
public class PostBaseAdapter extends BaseAdapter {

    private ArrayList<Posts> postlar=new ArrayList<>();
    private Context context;
    private LayoutInflater layoutInflater;
    public PostBaseAdapter(Context context, ArrayList<Posts> arrayList)
    {
        postlar = arrayList;
        this.context = context;
        layoutInflater = LayoutInflater.from(this.context);
    }


    @Override
    public int getCount() {
        return postlar.size();
    }

    @Override
    public Object getItem(int position) {
        return postlar.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PostViewHolder pViewHolder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.post_layout, parent, false);
            pViewHolder = new PostViewHolder(convertView);
            convertView.setTag(pViewHolder);
        } else {
            pViewHolder = (PostViewHolder) convertView.getTag();
        }
        Posts post = postlar.get(position);

        pViewHolder.AdSoyad.setText(post.getAdSoyad());
        pViewHolder.Post.setText(post.getPost());

        return convertView;
    }

    private class PostViewHolder {
        TextView AdSoyad, Post;

        public PostViewHolder(View item) {
            AdSoyad = (TextView) item.findViewById(R.id.tvAdSoyad);
            Post = (TextView) item.findViewById(R.id.tvPost);
        }
    }
}
