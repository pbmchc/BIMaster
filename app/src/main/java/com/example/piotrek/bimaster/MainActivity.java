package com.example.piotrek.bimaster;

import android.app.ActionBar;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.piotrek.bimaster.data.Indicator;
import com.example.piotrek.bimaster.data.Report;
import com.example.piotrek.bimaster.helpers.DatabaseAdapter;
import com.example.piotrek.bimaster.receivers.NotificationReceiver;
import com.example.piotrek.bimaster.services.CategoriesTask;
import com.example.piotrek.bimaster.utils.Utils;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        CategoryFragment.OnCategorySelectedListener, ReportListFragment.OnReportSelectedListener,
        PerformanceFragment.OnIndicatorSelectedListener, IndicatorFragment.SendMessageListener {

    String username;
    String password;
    String server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username = Utils.readSP(this,"username", "");
        password = Utils.readSP(this,"password", "");
        server = Utils.readSP(this, "server", "");
        Intent intent = new Intent(MainActivity.this, CredentialsActivity.class);
        if (username.isEmpty() && password.isEmpty())
        {
            startActivity(intent);
        }
        else {
            MainFragment fragment = new MainFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragContainer, fragment);
            fragmentTransaction.commit();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        boolean firstTime = Boolean.valueOf(Utils.readSP(this, "firstTime", "true"));
        Intent introIntent = new Intent(MainActivity.this, WelcomeActivity.class);
        if (firstTime)
        {
            Utils.saveSP(this, "notify", "1");
            Utils.saveSP(this, "hint", "1");
            Utils.saveSP(this,"alerts", "1");
            setDialogSharedPrefs();
            startActivity(introIntent);
        }

    }

    private void setDialogSharedPrefs()
    {
        Utils.saveSP(this, "search_dialog", "1");
        Utils.saveSP(this, "fav_dialog", "1");
        Utils.saveSP(this,"send_dialog", "1");
        Utils.saveSP(this,"alert_dialog", "1");
        Utils.saveSP(this,"main_dialog", "1");
    }


    @Override
    public void onStop()
    {
      super.onStop();
        setDialogSharedPrefs();
        String allow = Utils.readSP(this, "notify", "0");
        String alerts = Utils.readSP(this, "alerts", "0");
        if (allow.equals("1") || alerts.equals("1"))
            NotificationReceiver.setupAlarm(getApplicationContext());
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        TextView tvUser = (TextView) findViewById(R.id.tvUser);
        tvUser.setText(username);
        TextView info = (TextView) findViewById(R.id.info);
        info.setText("Server: " + server);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            CategoryFragment categoryFragment = new CategoryFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragContainer, categoryFragment);
            fragmentTransaction.addToBackStack("reports");
            fragmentTransaction.commit();
        } else if (id == R.id.nav_gallery) {
            PerformanceFragment performanceFragment = new PerformanceFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragContainer, performanceFragment);
            fragmentTransaction.addToBackStack("indicators");
            fragmentTransaction.commit();
        } else if (id == R.id.nav_slideshow) {
            SettingsFragment settingsFragment = new SettingsFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragContainer, settingsFragment);
            fragmentTransaction.addToBackStack("account");
            fragmentTransaction.commit();
        } else if (id == R.id.nav_manage) {
            ReportListFragment reportListFragment = new ReportListFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragContainer,reportListFragment);
            fragmentTransaction.addToBackStack("favs");
            fragmentTransaction.commit();
        }
        else if (id == R.id.nav_about)
        {
            AboutFragment aboutFragment = new AboutFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragContainer, aboutFragment);
            fragmentTransaction.addToBackStack("about");
            fragmentTransaction.commit();
        }
        else if (id == R.id.nav_settings)
        {
            ManageFragment manageFragment = new ManageFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragContainer, manageFragment);
            fragmentTransaction.addToBackStack("settings");
            fragmentTransaction.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onCategorySelected(String category)
    {
        ReportListFragment rlf = new ReportListFragment();
        Bundle b = new Bundle();
        b.putString("category", category);
        rlf.setArguments(b);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragContainer, rlf);
        ft.addToBackStack("elo");
        ft.commit();

    }

    public void onReportSelected(Report report)
    {
        ReportFragment rf = new ReportFragment();
        Bundle b = new Bundle();
        b.putString("category", report.category);
        b.putString("report", report.reportName);
        rf.setArguments(b);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragContainer, rf);
        ft.addToBackStack("lister");
        ft.commit();
    }


    public void onIndicatorSelected(Indicator indicator)
    {
        IndicatorFragment rf = new IndicatorFragment();
        Bundle b = new Bundle();
        String name = indicator.indiName;
        String desc = indicator.description;
        String value = indicator.value;
        String currency  =indicator.currency;
        double status = indicator.status;
        double[]trendSet = new double[indicator.trendSet.size()];
        for (int i = 0; i < indicator.trendSet.size(); i++)
        {
            trendSet[i] = indicator.trendSet.get(i);
        }
        b.putString("name", name);
        b.putString("description", desc);
        b.putString("value", value);
        b.putString("currency", currency);
        b.putDoubleArray("trendSet", trendSet);
        b.putDouble("status", status);
        rf.setArguments(b);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragContainer, rf);
        ft.addToBackStack("kpi");
        ft.commit();
    }

    public void onSendMessage(String name, String value, String currency)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("sms:"));
        if (currency == "null") currency = "";
        String message = name + " : " + value + " " + currency;
        intent.putExtra("sms_body", message);
        this.startActivity(intent);
    }


}
