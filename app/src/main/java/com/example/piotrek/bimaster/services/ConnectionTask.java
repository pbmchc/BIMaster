package com.example.piotrek.bimaster.services;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.example.piotrek.bimaster.CredentialsActivity;
import com.example.piotrek.bimaster.utils.Utils;

import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Piotrek on 2016-08-27.
 */
public class ConnectionTask extends AsyncTask<String, Void, Integer> {

    public ConnectionTask(CredentialsActivity ca, String username, String password) {
        this.credentialsActivity = ca;
        this.username =username;
        this.password = password;
    }

    private CredentialsActivity credentialsActivity;
    String username;
    String password;
    int code;
    ProgressDialog pd;

    @Override
    protected  void onPreExecute(){
         pd = ProgressDialog.show(credentialsActivity, "", "Sprawdzam połączenie...", true);
    }
    @Override
    protected Integer doInBackground(String ... params)
    {
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(params[0]);
            urlConnection = (HttpURLConnection) url.openConnection();
            String userCredentials = username+":"+password;
            String basicAuth = "Basic " + new String(new Base64().encode(userCredentials.getBytes()));
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty ("Authorization", basicAuth);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.connect();
            code =  urlConnection.getResponseCode();
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
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return code;
    }

    @Override
    protected void onPostExecute(Integer code)
    {
        pd.dismiss();
        super.onPostExecute(code);
        credentialsActivity.getCode(code);

    }
}
