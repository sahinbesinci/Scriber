package com.example.proje.scriber.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.proje.scriber.Objects.Mesaj;
import com.example.proje.scriber.R;

import java.util.ArrayList;

/**
 * Created by sahin on 9.05.2016.
 */
public class MesajBaseAdapter extends BaseAdapter {

    private ArrayList<Mesaj> mesajlar = new ArrayList<>();
    private Context context;
    private LayoutInflater layoutInflater;
    public MesajBaseAdapter(Context context, ArrayList<Mesaj> arrayList)
    {
        this.mesajlar = arrayList;
        this.context = context;
        layoutInflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return mesajlar.size();
    }

    @Override
    public Object getItem(int position) {
        return mesajlar.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MesajViewHolder mViewHolder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.mesaj_layout, parent, false);
            mViewHolder = new MesajViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MesajViewHolder) convertView.getTag();
        }
        Mesaj mesaj = mesajlar.get(position);

        mViewHolder.tvKonusmaAdi.setText(mesaj.getKonusmaAdi());

        return convertView;
    }

    private class MesajViewHolder {
        TextView tvKonusmaAdi;

        public MesajViewHolder(View item) {
            tvKonusmaAdi = (TextView) item.findViewById(R.id.tvKonusmaAdi);
        }
    }
}
