package com.example.piotrek.bimaster.services;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.example.piotrek.bimaster.PerformanceFragment;
import com.example.piotrek.bimaster.data.Indicator;
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

/**
 * Created by Piotrek on 2016-08-26.
 */
public class IndicatorsTask extends AsyncTask<String, Void, ArrayList<Indicator>> {


    public IndicatorsTask(PerformanceFragment pf)  {
        this.performanceFragment = pf;
    }

    private PerformanceFragment performanceFragment;
    ArrayList<Indicator> indicators = new ArrayList<>();
    int code;
    ProgressDialog pd;

    @Override
    protected  void onPreExecute(){
        pd = ProgressDialog.show(performanceFragment.getActivity(), "", "Aktualizuję dane...", true);

    }

    HttpURLConnection urlConnection = null;
    BufferedReader breader = null;
    @Override
    protected ArrayList<Indicator> doInBackground(String ... params)
    {
    try {
        URL url = new URL(params[0]);
        urlConnection = (HttpURLConnection) url.openConnection();
        String username = Utils.readSP(performanceFragment.getActivity(), "username", "");
        String password = Utils.readSP(performanceFragment.getActivity(), "password", "");
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
                Indicator indicator = new Indicator();
                indicator.indiName = object.getString("Name");
                indicator.description = object.getString("Description");
                indicator.value= object.getJSONObject("Values").getString("Value");
                indicator.currency = object.getString("Currency");
                JSONArray trends = object.getJSONObject("Values").getJSONArray("TrendSet");
                if (trends != null)
                {
                    ArrayList<Double> trendValues = new ArrayList<>();
                    for (int k=0; k<trends.length(); k++)
                    {
                        trendValues.add(Double.parseDouble(trends.get(k).toString()));
                    }
                    indicator.trendSet = trendValues;
                }
                indicator.status =  Double.parseDouble(object.getJSONObject("Values").getString("Status"));
                indicators.add(indicator);
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
    return indicators;
}

    @Override
    protected void onPostExecute(ArrayList<Indicator> result)
    {
        pd.dismiss();
        super.onPostExecute(result);
        performanceFragment.setIndicators(result);
        performanceFragment.setCode(code);

    }

}
