package com.example.piotrek.bimaster.services;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.example.piotrek.bimaster.CategoryFragment;
import com.example.piotrek.bimaster.ReportListFragment;
import com.example.piotrek.bimaster.data.Report;
import com.example.piotrek.bimaster.utils.Utils;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by Piotrek on 2016-08-26.
 */
public class ReportsTask extends AsyncTask<String, Void, ArrayList<Report>> {

    public ReportsTask(ReportListFragment reportListFrag, String category)  {
        this.reportListFragment = reportListFrag;
        this.category = category;
    }

    String category;
    private ReportListFragment reportListFragment;
    ArrayList<Report> reports = new ArrayList<>();
    int code;
    ProgressDialog pd;

    @Override
    protected  void onPreExecute(){
        pd = ProgressDialog.show(reportListFragment.getActivity(), "", "Aktualizuję dane...", true);

    }
    @Override
    protected ArrayList<Report> doInBackground(String ... params)
    {
        HttpURLConnection urlConnection = null;
        BufferedReader breader = null;

        try {
            URL url = new URL(params[0]);
            urlConnection = (HttpURLConnection) url.openConnection();
            String username = Utils.readSP(reportListFragment.getActivity(), "username", "");
            String password = Utils.readSP(reportListFragment.getActivity(), "password", "");
            String userCredentials = username+":"+password;
            String basicAuth = "Basic " + new String(new Base64().encode(userCredentials.getBytes()));
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty ("Authorization", basicAuth);
            urlConnection.connect();

            code =  urlConnection.getResponseCode();

            InputStream stream = urlConnection.getInputStream();
            breader = new BufferedReader(new InputStreamReader(stream));
            StringBuffer buffer = new StringBuffer();

            String line = "";
            while ((line = breader.readLine()) != null) {
                buffer.append(line);
            }
            String finalJson = buffer.toString();
            try {
                JSONObject parentObject = new JSONObject(finalJson);
                JSONArray childObject = parentObject.getJSONArray("value");
                for (int i = 0; i < childObject.length(); i++)
                {
                    JSONObject object = childObject.getJSONObject(i);
                    String path = object.getString("Path");
                    String []words =  path.split("/");
                    if (category != null && words[words.length-2].equals(category))
                    {
                        Report report = new Report();
                        report.reportName = object.getString("Name");
                        report.category = words[words.length-2];
                        report.description = object.getString("Description");
                        String modified = object.getString("ModifiedDate");
                        modified = modified.substring(0, 16).replace("T", " ");
                        report.modified = modified;
                        reports.add(report);
                    }
                    else
                    {
                        String favs = Utils.readSP(reportListFragment.getActivity(), "favs", "");
                        if (!favs.equals(""))
                        {
                            String[] favourites = favs.split(";");
                            for (String s : favourites)
                            {
                                if (s.equals(object.getString("Name")))
                                {
                                    Report report = new Report();
                                    report.reportName = object.getString("Name");
                                    report.category = words[words.length-2];
                                    report.description = object.getString("Description");
                                    String modified = object.getString("ModifiedDate");
                                    modified = modified.substring(0, 16).replace("T", " ");
                                    report.modified = modified;
                                    reports.add(report);
                                    }
                                }
                            }

                        }
                    }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }


        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally {
            if (urlConnection != null)
            {
                urlConnection.disconnect();
            }
            try {
                if(breader != null) {
                    breader.close();
                }
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
        return reports;
    }

    @Override
    protected void onPostExecute(ArrayList<Report> result)
    {
        pd.dismiss();
        super.onPostExecute(result);
        reportListFragment.setReports(result);
        reportListFragment.setCode(code);

    }


}
