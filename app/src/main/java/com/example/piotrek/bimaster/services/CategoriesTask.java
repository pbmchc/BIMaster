package com.example.piotrek.bimaster.services;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.piotrek.bimaster.CategoryFragment;
import com.example.piotrek.bimaster.MainActivity;
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
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Objects;

/**
 * Created by Piotrek on 2016-08-26.
 */
public class CategoriesTask extends AsyncTask<String, Void, Hashtable<String, Integer>> {

    public CategoriesTask(CategoryFragment cf) {
        this.categoryFragment = cf;
    }

    private CategoryFragment categoryFragment;
    Hashtable<String, Integer> categories = new Hashtable<>();
    ProgressDialog pd;
    int code;

    @Override
    protected  void onPreExecute(){
        pd = ProgressDialog.show(categoryFragment.getActivity(), "", "Aktualizuję dane...", true);
    }
    @Override
    protected Hashtable<String, Integer> doInBackground(String ... params)
    {
        HttpURLConnection urlConnection = null;
        BufferedReader breader = null;

        try {
            URL url = new URL(params[0]);
            urlConnection = (HttpURLConnection) url.openConnection();
            String username = Utils.readSP(categoryFragment.getActivity(), "username", "");
            String password = Utils.readSP(categoryFragment.getActivity(), "password", "");
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
                    if (words[words.length-2].equals(""))
                        words[words.length-2] = "Niesklasyfikowane";
                    Object obj = categories.get(words[words.length-2]);
                    if (obj == null)
                    {
                        categories.put(words[words.length-2], 1);
                    }
                    else
                    {
                        int occur = ((Integer) obj).intValue() + 1;
                        categories.put(words[words.length-2], new Integer(occur));
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
        return categories;
    }

    @Override
    protected void onPostExecute(Hashtable<String, Integer> result)
    {
        pd.dismiss();
        super.onPostExecute(result);
        categoryFragment.setCategories(result);
        categoryFragment.setCode(code);

    }
}
