package com.example.piotrek.bimaster.helpers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.piotrek.bimaster.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * Created by Piotrek on 2016-08-26.
 */
public class CategoriesAdapter extends BaseAdapter {

    private ArrayList mData;

    public CategoriesAdapter(Hashtable<String, Integer> data){
        mData  = new ArrayList();
        mData.addAll(data.entrySet());
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Hashtable.Entry<String, Integer> getItem(int position) {
        return (Hashtable.Entry) mData.get(position);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {

        final View view;
        if (convertView == null)
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item_layout, null);
        else
            view = convertView;

        Hashtable.Entry<String, Integer> category = getItem(pos);

        TextView text = (TextView) view.findViewById(R.id.text);
        TextView name = (TextView) view.findViewById(R.id.name);

        text.setText("[" + category.getValue().toString() + "]");
        name.setText(category.getKey());


        return view;
    }

}
