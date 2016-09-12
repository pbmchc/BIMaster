package com.example.piotrek.bimaster;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SnapHelper;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.piotrek.bimaster.data.Indicator;
import com.example.piotrek.bimaster.helpers.DatabaseAdapter;
import com.example.piotrek.bimaster.utils.Utils;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class IndicatorFragment extends Fragment {

    View view;
    Animation flowAnim;
    FloatingActionButton fab;
    TextView seekValue;
    Menu menu;
    MenuItem menuItem;
    LinearLayout alertLayout;
    SeekBar seekBar;
    DatabaseAdapter db;
    Indicator indicator, newIndicator;
    String hint;

    public IndicatorFragment() {
        // Required empty public constructor
    }

    SendMessageListener mCallback;
    public interface SendMessageListener
    {
        void onSendMessage(String name, String value, String currency);
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_indicator, container, false);
        setHasOptionsMenu(true);
        TextView txTitle = (TextView) view.findViewById(R.id.title);
        TextView txValue = (TextView) view.findViewById(R.id.value);
        TextView txDesc = (TextView) view.findViewById(R.id.desc);
        TextView txCurr = (TextView) view.findViewById(R.id.currency);
        seekBar = (SeekBar) view.findViewById(R.id.alertSeek);
        seekValue = (TextView) view.findViewById(R.id.seekValue);
        txTitle.setText(getArguments().getString("name"));
        if (getArguments().getString("currency").equals(""))
        txValue.setText(getArguments().getString("value"));
        else
        {
            StringBuilder stringBuilder = new StringBuilder(getArguments().getString("value"));
            int index = stringBuilder.length() - 3;
            while (index > 0)
            {
                stringBuilder.insert(index, " ");
                index -= 3;
            }
            txValue.setText(stringBuilder.toString());
        }
        txDesc.setText(getArguments().getString("description") != "null" ? getArguments().getString("description") : "brak opisu" );
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mCallback.onSendMessage(getArguments().getString("name"), getArguments().getString("value"), getArguments().getString("currency"));
            }
        });

        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                flowAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.flow_alt_anim);
                flowAnim.setRepeatCount(1);
                flowAnim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        fab.setVisibility(View.INVISIBLE);
                        menuItem = menu.findItem(R.id.send);
                        menuItem.setVisible(true);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                fab.startAnimation(flowAnim);
                return true;
            }
        });

        hint = Utils.readSP(getActivity(), "hint", "");
        String send_dialog = Utils.readSP(getActivity(), "send_dialog", "0");

        if (hint.equals("1") && send_dialog.equals("1"))
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Wiadomości");
            builder.setIcon(R.drawable.dialog_send);
            builder.setMessage("Używając przycisku widocznego w lewej dolnej części ekranu możesz wysłać " +
                    "wiadomość sms dotyczącą wskaźnika. Przytrzymanie przycisku spowoduje przeniesienie go na pasek narzędzi ");
            builder.setPositiveButton("OK, ROZUMIEM", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Utils.saveSP(getActivity(), "send_dialog", "0");
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }

        Animation fabAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_anim);
        fab.startAnimation(fabAnim);

        //LINECHART
        LineChart chart = (LineChart) view.findViewById(R.id.chart);
        DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
        int height = displayMetrics.heightPixels;
        chart.getLayoutParams().height = height/4;
        double[] trendSet = getArguments().getDoubleArray("trendSet");
        List<Entry>entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        int counter=0;
        for (double d : trendSet)
        {
            float f = (float)d;
            entries.add(new Entry(f, counter));
            labels.add("");
            counter++;
        }
        LineDataSet dataSet = new LineDataSet(entries, "trend");
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet.setColor(R.color.colorPrimary);
        dataSet.setCircleColor(R.color.colorPrimary);
        dataSet.setCircleColorHole(R.color.colorPrimary);
        dataSet.setCircleRadius(4.0f);
        dataSet.setLineWidth(3.0f);
        dataSet.setDrawValues(false);
        LineData lineData = new LineData(labels, dataSet);
        chart.setBackgroundColor(Color.TRANSPARENT);
        chart.animateX(1000, Easing.EasingOption.EaseOutCirc);
        chart.setDescription("");
        chart.setData(lineData);
        chart.invalidate();
        chart.setDoubleTapToZoomEnabled(false);
        chart.setTouchEnabled(false);
        chart.setNoDataTextDescription("Brak danych");
        Legend legend = chart.getLegend();
        legend.setEnabled(false);

        XAxis xAxis = chart.getXAxis();
        YAxis yAxisL = chart.getAxisLeft();
        YAxis yAxisR = chart.getAxisRight();

        xAxis.setDrawGridLines(false);
        yAxisL.setDrawGridLines(false);
        yAxisR.setDrawGridLines(false);

        yAxisL.setEnabled(false);
        yAxisR.setEnabled(false);
        xAxis.setEnabled(false);

        if (!getArguments().getString("currency").equals("null"))
            txCurr.setText(getArguments().getString("currency"));

        db = new DatabaseAdapter(getActivity());
        indicator = db.getIndicator(getArguments().getString("name"));
        Switch alertSwitch = (Switch) view.findViewById(R.id.alertSwitch);
        alertLayout = (LinearLayout)view.findViewById(R.id.alertLayout);

        if (Utils.readSP(getActivity(), "alerts", "").equals("0"))
            alertSwitch.setEnabled(false);

        if(indicator.indiName!=null)
        {
            alertLayout.setVisibility(View.VISIBLE);
            alertSwitch.setChecked(true);
            seekBar.setMax(Integer.parseInt(getArguments().getString("value")));
            if (indicator.alertValue > Integer.parseInt(getArguments().getString("value")))
            {
                seekValue.setText(String.valueOf(getArguments().getString("value")));
                seekBar.setProgress(Integer.parseInt(getArguments().getString("value")));
                indicator.alertValue = Integer.parseInt(getArguments().getString("value"));
                Toast.makeText(getActivity(),"Alert! Ustal nową wartość krytyczną", Toast.LENGTH_LONG).show();
                db.updateIndicator(indicator);
        }
            else
            {
                seekValue.setText(String.valueOf(indicator.alertValue));
                seekBar.setProgress(indicator.alertValue);
            }
        }
        alertSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (alertLayout.getVisibility() == View.INVISIBLE) {
                    String alert_dialog = Utils.readSP(getActivity(), "alert_dialog", "0");
                    if (hint.equals("1") && alert_dialog.equals("1"))
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Alerty KPI");
                        builder.setIcon(R.drawable.kpi);
                        builder.setMessage("Włączono Alert KPI. Używając suwaka możesz ustalić wartość krytyczną " +
                                "wskaźnika, poniżej której alert zostanie uruchiomiony");
                        builder.setPositiveButton("OK, ROZUMIEM", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Utils.saveSP(getActivity(), "alert_dialog", "0");
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.show();
                    }
                    alertLayout.setVisibility(View.VISIBLE);
                    newIndicator = new Indicator();
                    newIndicator.indiName = getArguments().getString("name");
                    newIndicator.alertValue = 0;
                    seekBar.setProgress(0);
                    db.insertIndicator(newIndicator);
                }
                else
                {
                    db.deleteIndicator(getArguments().getString("name"));
                    seekBar.setProgress(0);
                    seekValue.setText(String.valueOf(0));
                    alertLayout.setVisibility(View.INVISIBLE);
                }
            }
        });
        seekBar.setMax(Integer.parseInt(getArguments().getString("value")));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            int progress=0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progress = i;
                seekValue.setText(String.valueOf(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                indicator.indiName = getArguments().getString("name");
                indicator.alertValue = progress;
                db.updateIndicator(indicator);
                Toast.makeText(getActivity(), "Alert włączony", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try {
            mCallback = (SendMessageListener) activity;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString() + "must implement interface");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.send_menu, menu);
        this.menu = menu;
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.send)
        {
            mCallback.onSendMessage(getArguments().getString("name"), getArguments().getString("value"), getArguments().getString("currency"));

        }
        return super.onOptionsItemSelected(item);
    }

}
