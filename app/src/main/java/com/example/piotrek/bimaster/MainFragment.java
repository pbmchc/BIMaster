package com.example.piotrek.bimaster;


import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.piotrek.bimaster.utils.Utils;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {

    public MainFragment() {
        // Required empty public constructor
    }

    TextView txtView;
    TextView welcomeMsg;
    ImageView iv;
    Animation fade;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        txtView = (TextView) v.findViewById(R.id.txtView);
        iv = (ImageView) v.findViewById(R.id.cube);
        welcomeMsg = (TextView) v.findViewById(R.id.welcomeMsg);
        welcomeMsg.setText("Witaj " + Utils.readSP(getActivity(), "username", ""));
        checkStatus();

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.deleteSP(getActivity());
            }
        });

        String hint = Utils.readSP(getActivity(), "hint", "");
        String main_dialog = Utils.readSP(getActivity(), "main_dialog", "0");
        if (hint.equals("1") && main_dialog.equals("1"))
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Podpowiedzi");
            builder.setIcon(R.drawable.dialog_hint);
            builder.setMessage("Aktualnie podpowiedzi są włączone. Aby je wyłączyć naciśnij przycisk " +
                    "WYŁĄCZ. Możesz to zrobić również w Ustawieniach aplikacji");
            builder.setNegativeButton("WYŁĄCZ", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Utils.saveSP(getActivity(), "hint", "0");
                }
            });
            builder.setPositiveButton("DALEJ", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Utils.saveSP(getActivity(), "main_dialog", "0");
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }

        return v;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        checkStatus();
    }

    public boolean isOnline()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    private void checkStatus()
    {
        Animation rotate = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_anim);
        fade = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_anim);
        rotate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (!isOnline())
                {
                    txtView.setText("Brak połączenia z Internetem");
                }
                else
                {
                    txtView.setText("Połączenie z Internetem aktywne");
                }
                txtView.startAnimation(fade);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        rotate.setFillEnabled(true);
        rotate.setFillAfter(true);
        iv.startAnimation(rotate);

    }

}
