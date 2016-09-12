package com.example.piotrek.bimaster;


import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.piotrek.bimaster.helpers.CategoriesAdapter;
import com.example.piotrek.bimaster.services.CategoriesTask;
import com.example.piotrek.bimaster.utils.Utils;

import java.util.Collection;
import java.util.Hashtable;


/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryFragment extends ListFragment {

    private Hashtable<String, Integer> categories = new Hashtable<>();
    int code;
    TextView noReports;

    OnCategorySelectedListener mCallback;
    public interface OnCategorySelectedListener{
        void onCategorySelected(String category);
    }
    public CategoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        String server = Utils.readSP(getActivity(), "server", "");
        String instance = Utils.readSP(getActivity(), "instance", "");
        String query = "?$filter=Type%20eq%20Model.CatalogItemType'MobileReport'";
        if (isOnline())
        new CategoriesTask(this).execute("http://" + server +"/" + instance + "/api/v1.0/CatalogItems" + query);
        noReports = (TextView)view.findViewById(R.id.noReports);
        if (!isOnline())
            Toast.makeText(getActivity(), "Brak dostępu do Internetu", Toast.LENGTH_LONG).show();
        return view;
    }

    @Override
    public void onStart(){
        super.onStart();
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selected = ((TextView) view.findViewById(R.id.name)).getText().toString();
                if (selected.equals("Niesklasyfikowane"))
                    selected = "";
                mCallback.onCategorySelected(selected);
            }
        });
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try {
            mCallback = (OnCategorySelectedListener) activity;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString() + "must implement interface");
        }
    }


    public void setCategories(Hashtable<String, Integer> cats)
    {
        this.categories = cats;
        Collection<Integer> values = cats.values();
        int reportNumber = 0;
        for (int value : values)
        {
            reportNumber+=value;
        }
        Utils.saveSP(getActivity(), "reportNumber", String.valueOf(reportNumber));
        CategoriesAdapter adapter = new CategoriesAdapter(categories);
        setListAdapter(adapter);
        if (categories.size() == 0)
        {
            noReports.setVisibility(View.VISIBLE);
            noReports.setText("Brak raportów do wyświetlenia");
        }
    }

    public void setCode(int code)
    {
        this.code = code;
        if (code==200)
        {
        }
        else if (String.valueOf(code).startsWith("40"))
            Toast.makeText(getActivity(), "Odmowa dostępu", Toast.LENGTH_LONG).show();
        else if (String.valueOf(code).startsWith("50"))
            Toast.makeText(getActivity(), "Błąd serwera. Spróbuj pożniej", Toast.LENGTH_LONG).show();
    }

    public boolean isOnline()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}
