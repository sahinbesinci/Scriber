package com.example.proje.scriber.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.proje.scriber.Objects.Kisi;
import com.example.proje.scriber.R;

import java.util.ArrayList;

/**
 * Created by sahin on 9.05.2016.
 */
public class KisiBaseAdapter extends BaseAdapter implements Filterable {

    private ArrayList<Kisi> kisiler = new ArrayList<>();
    private ArrayList<Kisi> filtreliKisiler = new ArrayList<>();
    private Context context;
    private LayoutInflater layoutInflater;
    public KisiBaseAdapter(Context context, ArrayList<Kisi> arrayList)
    {
        this.kisiler = arrayList;
        this.filtreliKisiler = arrayList;
        this.context = context;
        layoutInflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return filtreliKisiler.size();
    }

    @Override
    public Object getItem(int position) {
        return filtreliKisiler.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PostViewHolder pViewHolder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.kisi_arama_layout, parent, false);
            pViewHolder = new PostViewHolder(convertView);
            convertView.setTag(pViewHolder);
        } else {
            pViewHolder = (PostViewHolder) convertView.getTag();
        }
        Kisi kisi = filtreliKisiler.get(position);

        pViewHolder.AdSoyad.setText(kisi.getAdSoyad());
        pViewHolder.Email.setText(kisi.getEmail());

        return convertView;
    }


    private class PostViewHolder {
        TextView AdSoyad, Email;

        public PostViewHolder(View item) {
            AdSoyad = (TextView) item.findViewById(R.id.tvAdSoyad);
            Email = (TextView) item.findViewById(R.id.tvEmail);
        }
    }
    @Override
    public Filter getFilter() {
        return new Filter()
        {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence)
            {
                FilterResults results = new FilterResults();

                if(charSequence == null || charSequence.length() == 0)
                {
                    results.values = kisiler;
                    results.count = kisiler.size();
                }
                else
                {
                    ArrayList<Kisi> filterResultsData = new ArrayList<>();

                    for(Kisi data : kisiler)
                    {
                        if(data.getAdSoyad().substring(0,charSequence.length()).equals(charSequence.toString()) ||
                                data.getEmail().substring(0,charSequence.length()).equals(charSequence.toString()))
                        {
                            filterResultsData.add(data);
                        }
                    }
                    results.values = filterResultsData;
                    results.count = filterResultsData.size();
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults)
            {
                filtreliKisiler = (ArrayList<Kisi>)filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
