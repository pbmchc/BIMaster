package com.example.piotrek.bimaster;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.piotrek.bimaster.data.Indicator;
import com.example.piotrek.bimaster.helpers.IndicatorAdapter;
import com.example.piotrek.bimaster.services.IndicatorsTask;
import com.example.piotrek.bimaster.utils.Utils;

import java.util.ArrayList;


public class PerformanceFragment extends ListFragment {

    OnIndicatorSelectedListener mCallback;
    private ArrayList<Indicator> indicators = new ArrayList<>();
    int code;
    Toolbar toolbar;
    EditText edtSearch;
    IndicatorAdapter adapter;
    TextView noReports;

    public interface OnIndicatorSelectedListener{
        void onIndicatorSelected(Indicator indicator);
    }

    public PerformanceFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_performance, container, false);
        String server = Utils.readSP(getActivity(), "server", "");
        String instance = Utils.readSP(getActivity(), "instance", "");
        String query = "?$filter=Type%20eq%20Model.CatalogItemType'Kpi'";
        setHasOptionsMenu(true);
        if(isOnline())
        new IndicatorsTask(this).execute("http://" + server + "/" + instance + "/api/v1.0/CatalogItems" + query);
        if (!isOnline())
            Toast.makeText(getActivity(), "Brak dostępu do Internetu", Toast.LENGTH_LONG).show();
        toolbar = (Toolbar) view.findViewById(R.id.search_toolbar);
        edtSearch = (EditText) view.findViewById(R.id.edtSearch);
        noReports = (TextView)view.findViewById(R.id.noReports);
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = edtSearch.getText().toString().toLowerCase();
                adapter.filter(text);
            }
        });

        String hint = Utils.readSP(getActivity(), "hint", "");
        String search_dialog = Utils.readSP(getActivity(), "search_dialog", "0");
        if (hint.equals("1") && search_dialog.equals("1"))
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Wyszukiwanie");
            builder.setIcon(R.drawable.dialog_search);
            builder.setMessage("Używając przycisku widocznego w prawej górnej części ekranu możesz wyszukać " +
                    "konkretne, interesujące Cię elementy");
            builder.setPositiveButton("OK, ROZUMIEM", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Utils.saveSP(getActivity(), "search_dialog", "0");
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }

        return view;
    }
    @Override
    public void onStart(){
        super.onStart();
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Indicator selectedIndicator = (Indicator) adapterView.getItemAtPosition(i);
                mCallback.onIndicatorSelected(selectedIndicator);
            }
        });
    }
    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try {
            mCallback = (OnIndicatorSelectedListener) activity;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString() + "must implement interface");
        }
    }

    public void setIndicators(ArrayList<Indicator> indics)
    {
        this.indicators = indics;
        adapter = new IndicatorAdapter(indicators);
        setListAdapter(adapter);
        if (indicators.size() == 0)
        {
            noReports.setVisibility(View.VISIBLE);
            noReports.setText("Brak wskaźników do wyświetlenia");
        }
    }

    public void setCode(int code)
    {
        this.code = code;
        if (String.valueOf(code).startsWith("40"))
            Toast.makeText(getActivity(), "Odmowa fdostępu", Toast.LENGTH_LONG).show();
        else if (String.valueOf(code).startsWith("50"))
            Toast.makeText(getActivity(), "Błąd serwera. Spróbuj pożniej", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.search) {
            int state = toolbar.getVisibility();
            if (state == View.GONE)
            {
                edtSearch.requestFocus();
            }
            toolbar.setVisibility(state == View.GONE ? View.VISIBLE : View.GONE);
            edtSearch.setText("");
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean isOnline()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

}
