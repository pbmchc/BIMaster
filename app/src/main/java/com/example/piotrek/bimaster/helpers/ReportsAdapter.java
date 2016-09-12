package com.example.piotrek.bimaster.helpers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.piotrek.bimaster.R;
import com.example.piotrek.bimaster.data.Report;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by Piotrek on 2016-08-26.
 */
public class ReportsAdapter extends BaseAdapter {

    private ArrayList mData;
    private ArrayList <Report> sData;

    public ReportsAdapter(ArrayList<Report> reports){
        mData  = reports;
        sData = new ArrayList();
        sData.addAll(mData);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Report getItem(int position) {
        return (Report) mData.get(position);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {

        final View view;
        if (convertView == null)
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_item_layout, null);
        else
            view = convertView;

        Report report = getItem(pos);

        TextView text = (TextView) view.findViewById(R.id.reportName);
        TextView date = (TextView) view.findViewById(R.id.reportDate);

        text.setText(report.reportName);
        date.setText(report.modified);


        return view;
    }

    public void filter(String text)
    {
        text = text.toLowerCase();
        mData.clear();
        if (text.length() == 0)
            mData.addAll(sData);
        else
        {
            for (Report r: sData)
            {
                if (r.reportName.toLowerCase().contains(text))
                {
                    mData.add(r);
                }
            }
        }
        notifyDataSetChanged();
    }
}
