package com.example.piotrek.bimaster.helpers;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.piotrek.bimaster.R;
import com.example.piotrek.bimaster.data.Indicator;

import java.util.ArrayList;

/**
 * Created by Piotrek on 2016-08-26.
 */
public class IndicatorAdapter extends BaseAdapter {

    private ArrayList mData;
    private ArrayList<Indicator> sData;

    public IndicatorAdapter(ArrayList<Indicator> indicators){
        mData  = indicators;
        sData = new ArrayList<>();
        sData.addAll(mData);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Indicator getItem(int position) {
        return (Indicator) mData.get(position);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {

        final View view;
        if (convertView == null)
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.indicator_item_layout, null);
        else
            view = convertView;

        Indicator indicator = getItem(pos);

        TextView text = (TextView) view.findViewById(R.id.name);
        TextView amount = (TextView) view.findViewById(R.id.value);
        TextView currency = (TextView) view.findViewById(R.id.currency);
        ImageView status = (ImageView) view.findViewById(R.id.status);

        text.setText(indicator.indiName);
        amount.setText(indicator.value);
        if (!indicator.currency.equals("null"))
        {
            currency.setText(indicator.currency);
            StringBuilder stringBuilder = new StringBuilder(indicator.value);
            int index = stringBuilder.length() - 3;
            while (index > 0)
            {
                stringBuilder.insert(index, " ");
                index -= 3;
            }
            amount.setText(stringBuilder.toString());
        }

        switch (String.valueOf(indicator.status))
        {
            case "1.0":
               status.setBackgroundResource(R.drawable.arrow_up);
                break;
            case "0.0":
                status.setBackgroundResource(R.drawable.arrow_straight);
                break;
            case "-1.0":
                status.setBackgroundResource(R.drawable.arrow_down);
                break;
            default:
                status.setBackgroundResource(R.drawable.arrow_straight);
                break;
        }
        return view;
    }

    public void filter(String text)
    {
        mData.clear();
        if (text.length() == 0)
        {
            mData.addAll(sData);
        }
        else {
            for (Indicator indicator : sData) {
                if (indicator.indiName.toLowerCase().contains(text.toLowerCase())) {
                    mData.add(indicator);
                }
            }
            notifyDataSetChanged();
        }
    }
}
