package com.example.piotrek.bimaster;


import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.webkit.HttpAuthHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.piotrek.bimaster.utils.Utils;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReportFragment extends Fragment {

    String password;
    String username;
    String category;
    String report;
    FloatingActionButton fab;
    Menu menu;
    MenuItem like;
    String favs;
    WebView wv;
    Animation flowAnim;

    public ReportFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_report, container, false);
        TextView tv = (TextView) view.findViewById(R.id.Title);
        category = getArguments().getString("category");
        report = getArguments().getString("report");
        setHasOptionsMenu(true);
        tv.setText(report);
        wv = (WebView) view.findViewById(R.id.chartViewer);
        WebSettings ws = wv.getSettings();
        String server = Utils.readSP(getActivity(), "server", "");
        String instance = Utils.readSP(getActivity(), "instance", "");
        wv.setHttpAuthUsernamePassword("http://" +server+ ":80", "myrealm", username, password);
        ws.setJavaScriptEnabled(true);
        ws.setDomStorageEnabled(true);
        wv.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // TODO Auto-generated method stub
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {

                // TODO Auto-generated method stub

                super.onPageFinished(view, url);

            }
            @Override
            public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm)
            {
                username = Utils.readSP(getActivity(), "username", "");
                password = Utils.readSP(getActivity(), "password", "");
                handler.proceed(username, password);
            }

            @Override
            public void onReceivedError(WebView webView, int errorCode, String desc, String failingUrl)
            {
                Toast.makeText(getActivity(), "Raport usunięty lub przeniesiony", Toast.LENGTH_LONG).show();
            }

        });
        wv.setWebChromeClient(new WebChromeClient());
        wv.loadUrl("http://" + server + ":80/" + instance + "/api/v1.0/SafeGetSystemResourceContent(type='mobilereportruntime',key='web')?v=3.9.93&baseuri=http%3A%2F%2F" + server + "%2F" + instance + "&report=%2F" + category +"%2F" + report +"&loader=1");

        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        favs = Utils.readSP(getActivity(), "favs", "");
        if (favs.contains(report))
            fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorRed)));
        else
            fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGray)));


        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {

                flowAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.flow_anim);
                flowAnim.setRepeatCount(1);
                flowAnim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        fab.setVisibility(View.GONE);
                        like = menu.findItem(R.id.like);
                        if (fab.getBackgroundTintList() == ColorStateList.valueOf(getResources().getColor(R.color.colorRed)))
                        like.setIcon(R.drawable.nav_fav);
                        else
                            like.setIcon(R.drawable.not_fav);
                        like.setVisible(true);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                fab.startAnimation(flowAnim);
                return true;
            }
        });
        Animation test = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_anim);
        fab.startAnimation(test);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!favs.contains(report))
                {
                    favs += report + ";";
                    Utils.saveSP(getActivity(), "favs", favs);
                    fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorRed)));
                    Toast.makeText(getContext(), "Dodano do obserwowanych", Toast.LENGTH_LONG).show();
                }
                else
                {
                    favs = favs.replace(report + ";", "");
                    Utils.saveSP(getActivity(), "favs", favs);
                    Toast.makeText(getContext(), "Usunięto z obserwowanych", Toast.LENGTH_LONG).show();
                    fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGray)));
                }
            }
        });

        String hint = Utils.readSP(getActivity(), "hint", "");
        String fav_dialog = Utils.readSP(getActivity(), "fav_dialog", "0");
        if (hint.equals("1") && fav_dialog.equals("1"))
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Obserwowane");
            builder.setIcon(R.drawable.dialog_fav);
            builder.setMessage("Używając przycisku widocznego w lewej dolnej części ekranu możesz dodać " +
                    "raport do obserwowany. Przytrzymanie przycisku spowoduje przeniesienie go na pasek narzędzi ");
            builder.setPositiveButton("OK, ROZUMIEM", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Utils.saveSP(getActivity(), "fav_dialog", "0");
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }

        return view;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.refresh_menu, menu);
        this.menu = menu;
        super.onCreateOptionsMenu(menu, inflater);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.refresh) {
            wv.reload();

        }
        else if (id == R.id.like)
        {
            if (!favs.contains(report))
            {
                favs += report + ";";
                Utils.saveSP(getActivity(), "favs", favs);
                like.setIcon(R.drawable.nav_fav);
                Toast.makeText(getContext(), "Dodano do obserwowanych", Toast.LENGTH_LONG).show();
            }
            else
            {
                favs = favs.replace(report + ";", "");
                Utils.saveSP(getActivity(), "favs", favs);
                like.setIcon(R.drawable.not_fav);
                Toast.makeText(getContext(), "Usunięto z obserwowanych", Toast.LENGTH_LONG).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

}
