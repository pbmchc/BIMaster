package com.example.piotrek.bimaster;



import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.piotrek.bimaster.utils.Utils;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class SettingsFragment extends Fragment{

    EditText edtUser;
    EditText edtPass;
    EditText edtServer;
    EditText edtInstance;
    Button btnSave;
    Button btnEdit;
    TextView tv;
    TextView info;
    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_settings, container, false);
        edtUser = (EditText)v.findViewById(R.id.edtName);
        edtPass = (EditText)v.findViewById(R.id.edtPass);
        edtServer = (EditText) v.findViewById(R.id.edtServer);
        edtInstance = (EditText) v.findViewById(R.id.edtInstance);
        btnSave = (Button)v.findViewById(R.id.btnSave);
        btnEdit = (Button)v.findViewById(R.id.btnEdit);
        tv = (TextView) getActivity().findViewById(R.id.tvUser);
        info = (TextView) getActivity().findViewById(R.id.info);
        String uname = Utils.readSP(getActivity(), "username", "");
        String pass = Utils.readSP(getActivity(), "password", "");
        final String server = Utils.readSP(getActivity(), "server", "");
        final String instance = Utils.readSP(getActivity(), "instance", "");

        if (uname.isEmpty() && pass.isEmpty())
        {
            edtUser.setEnabled(true);
            edtPass.setEnabled(true);
            edtServer.setEnabled(true);
            edtInstance.setEnabled(true);
            btnSave.setVisibility(View.VISIBLE);
            btnEdit.setVisibility(View.GONE);
        }
        else
        {
            edtUser.setText(uname);
            edtPass.setText(pass);
            edtServer.setText(server);
            edtInstance.setText(instance);
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = edtUser.getText().toString();
                String password = edtPass.getText().toString();
                String srv = edtServer.getText().toString();
                String inst = edtInstance.getText().toString();

                if (username.equals("") || password.equals("") || srv.equals("") || inst.equals(""))
                    Toast.makeText(getActivity(), "Wypełnij wszystkie pola", Toast.LENGTH_LONG).show();
                else
                {
                    Toast.makeText(getActivity(), "Zmiany zapisano", Toast.LENGTH_SHORT).show();
                    Utils.saveSP(getActivity(), "username", username);
                    Utils.saveSP(getActivity(), "password", password);
                    Utils.saveSP(getActivity(), "server", srv);
                    Utils.saveSP(getActivity(), "instance", inst);
                    tv.setText(username);
                    info.setText("Server: " + srv);

                    edtUser.setEnabled(false);
                    edtPass.setEnabled(false);
                    edtInstance.setEnabled(false);
                    edtServer.setEnabled(false);
                    btnSave.setVisibility(View.GONE);
                    btnEdit.setVisibility(View.VISIBLE);
                }
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtUser.setEnabled(true);
                edtPass.setEnabled(true);
                edtServer.setEnabled(true);
                edtInstance.setEnabled(true);
                btnSave.setVisibility(View.VISIBLE);
                btnEdit.setVisibility(View.GONE);
            }
        });

        return v;
    }


}
