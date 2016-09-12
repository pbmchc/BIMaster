package com.example.piotrek.bimaster;


import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.piotrek.bimaster.data.Report;
import com.example.piotrek.bimaster.helpers.ReportsAdapter;
import com.example.piotrek.bimaster.services.ReportsTask;
import com.example.piotrek.bimaster.utils.Utils;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReportListFragment extends Fragment {

    OnReportSelectedListener mCallback;
    ArrayList<Report> reports = new ArrayList();
    String category;
    GridView gv;
    EditText edtSearch;
    ReportsAdapter adapter;
    Toolbar toolbar;
    int code;
    TextView tv;
    TextView noReports;

    public ReportListFragment() {
        // Required empty public constructor
    }

    public interface OnReportSelectedListener
    {
        void onReportSelected(Report report);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_report_list, container, false);
        setHasOptionsMenu(true);
        if (getArguments() != null)
        category = getArguments().getString("category");
        String server = Utils.readSP(getActivity(), "server", "");
        String instance = Utils.readSP(getActivity(), "instance", "");
        String query = "?$filter=Type%20eq%20Model.CatalogItemType'MobileReport'";
        if(isOnline())
        new ReportsTask(this, category).execute("http://" + server + "/" + instance + "/api/v1.0/CatalogItems" + query);
        if (!isOnline())
        if (!isOnline()) Toast.makeText(getActivity(), "Brak dostępu do Internetu", Toast.LENGTH_LONG).show();
        tv = (TextView) view.findViewById(R.id.Title);
        noReports = (TextView)view.findViewById(R.id.noReports);
        if(getArguments() != null)
        {
         if (getArguments().getString("category").equals(""))
             tv.setText("Niesklasyfikowane");
         else
             tv.setText(getArguments().getString("category"));
        }
        else
        tv.setText("Obserwowane");
        gv = (GridView) view.findViewById(R.id.grid);

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Report report = (Report) adapterView.getItemAtPosition(i);
                mCallback.onReportSelected(report);
            }
        });


        toolbar = (Toolbar) view.findViewById(R.id.search_toolbar);
        edtSearch = (EditText)view.findViewById(R.id.edtSearch);
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
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try {
            mCallback = (OnReportSelectedListener) activity;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString() + "must implement interface");
        }
    }

    public void setReports(ArrayList<Report> reports)
    {
        this.reports = reports;
        adapter = new ReportsAdapter(reports);
        gv.setAdapter(adapter);
        if (reports.size() == 0)
        {
            noReports.setVisibility(View.VISIBLE);
            noReports.setText("Brak raportów do wyświetlenia");
        }

    }

    public void setCode(int code)
    {
        this.code = code;
        if (String.valueOf(code).startsWith("40"))
            Toast.makeText(getActivity(), "Odmowa dostępu", Toast.LENGTH_LONG).show();
        else if (String.valueOf(code).startsWith("50"))
            Toast.makeText(getActivity(), "Błąd serwera. Spróbuj pożniej", Toast.LENGTH_LONG).show();

    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater)
    {
        menuInflater.inflate(R.menu.search_menu, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
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
