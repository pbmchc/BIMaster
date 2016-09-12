package com.example.piotrek.bimaster;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.Toast;

import com.example.piotrek.bimaster.data.Indicator;
import com.example.piotrek.bimaster.helpers.DatabaseAdapter;
import com.example.piotrek.bimaster.utils.Utils;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ManageFragment extends Fragment {


    Switch aSwitch;
    Switch hintSwitch;
    Switch alertSwitch;
    String allow;
    String hint;
    String alerts;
    public ManageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_manage, container, false);
        allow = Utils.readSP(getActivity(), "notify", "");
        hint = Utils.readSP(getActivity(), "hint", "");
        alerts = Utils.readSP(getActivity(), "alerts", "0");

        aSwitch = (Switch) view.findViewById(R.id.notifySwitch);
        if (allow.equals("0"))
            aSwitch.setChecked(false);
        else
            aSwitch.setChecked(true);

        aSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (allow.equals("0"))
                    Utils.saveSP(getActivity(), "notify", "1");
                else
                    Utils.saveSP(getActivity(), "notify", "0");
            }
        });

        alertSwitch = (Switch) view.findViewById(R.id.alertSwitch);
        if (alerts.equals("0"))
           alertSwitch.setChecked(false);
        else
            alertSwitch.setChecked(true);

        alertSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (alerts.equals("0"))
                {
                    Utils.saveSP(getActivity(), "alerts", "1");
                }
                else
                {
                    DatabaseAdapter db = new DatabaseAdapter(getActivity());
                    List<Indicator> indicatorList;
                    indicatorList = db.getAllIndicators();
                    for(Indicator i : indicatorList)
                    {
                        db.deleteIndicator(i.indiName);
                    }
                    Utils.saveSP(getActivity(), "alerts", "0");
                }

            }
        });


        hintSwitch = (Switch) view.findViewById(R.id.hintSwitch);
        if (hint.equals("0"))
            hintSwitch.setChecked(false);
        else
            hintSwitch.setChecked(true);

        hintSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hint.equals("0"))
                {
                    Utils.saveSP(getActivity(), "hint", "1");
                    Utils.saveSP(getActivity(), "search_dialog", "1");
                    Utils.saveSP(getActivity(), "fav_dialog", "1");
                    Utils.saveSP(getActivity(),"send_dialog", "1");
                    Utils.saveSP(getActivity(),"alert_dialog", "1");
                    Utils.saveSP(getActivity(),"main_dialog", "1");
                }
                else
                    Utils.saveSP(getActivity(), "hint", "0");
            }
        });
        return view;
    }

}
