package com.example.piotrek.bimaster.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.example.piotrek.bimaster.MainActivity;
import com.example.piotrek.bimaster.R;
import com.example.piotrek.bimaster.data.Indicator;
import com.example.piotrek.bimaster.helpers.DatabaseAdapter;
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
import java.util.List;
import java.util.Random;

/**
 * Created by Piotrek on 2016-08-27.
 */
public class NotificationService extends IntentService {

    int actualNumber;
    int response=0;
    int code;
    ArrayList<Indicator> indicators = new ArrayList<>();

    public NotificationService(){

        super(NotificationService.class.getSimpleName());
    }

    public static Intent startIntent(Context ctx)
    {
        Intent intent = new Intent(ctx, NotificationService.class);
        intent.setAction("ACTION_START");
        return intent;
    }

    public static Intent deleteIntent(Context ctx)
    {
        Intent intent = new Intent(ctx, NotificationService.class);
        intent.setAction("ACTION_DELETE");
        return intent;
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        try{
            String action = intent.getAction();
            if (action.equals("ACTION_START")){

                checkForChanges();

                if (code == 200) {
                    String reportNumber = Utils.readSP(this, "reportNumber", "0");
                    if (reportNumber.equals("0") || response < Integer.parseInt(Utils.readSP(this, "reportNumber", ""))) {
                        actualNumber = response;
                        Utils.saveSP(this, "reportNumber", String.valueOf(actualNumber));
                    }
                    else
                        actualNumber = Integer.parseInt(Utils.readSP(this, "reportNumber", ""));

                    if (actualNumber < response) {
                        createNotification();
                        actualNumber = response;
                        Utils.saveSP(this, "reportNumber", String.valueOf(actualNumber));
                    }
                    if (indicators != null)
                    {
                        int index = 1;
                        for(Indicator ind : indicators)
                        {
                            createAlert(ind.indiName, ind.alertValue, index);
                            index++;
                        }
                    }
                }
            }
            else if (action.equals("ACTION_DELETE")){

            }
        }
        finally {
            WakefulBroadcastReceiver.completeWakefulIntent(intent);
        }
    }

    private void createNotification()
    {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle("Powiadomienie").setAutoCancel(true).setColor(Color.WHITE).setContentText("Dostępny jest nowy raport").setSmallIcon(R.drawable.in_app_logo).setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.notify));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT );
        builder.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
    }

    private void createAlert(String indicatorName, int value, int index)
    {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle("Alert!").setAutoCancel(true).setColor(Color.WHITE).setContentText("Alert! " + indicatorName +  " spadł poniżej " + String.valueOf(value)).setSmallIcon(R.drawable.in_app_logo).setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.alert));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT );
        builder.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(index, builder.build());
    }

    private void checkForChanges()
    {
        HttpURLConnection urlConnection = null;
        BufferedReader breader = null;

        try {
            URL url = new URL("http://192.168.0.13/Reports_MSSQLSERVER16/api/v1.0/CatalogItems");
            urlConnection = (HttpURLConnection) url.openConnection();
            String username = Utils.readSP(this, "username", "");
            String password = Utils.readSP(this, "password", "");
            String notify = Utils.readSP(this, "notify", "");
            String indicatorAlert = Utils.readSP(this, "alerts", "");
            String userCredentials = username+":"+password;
            String basicAuth = "Basic " + new String(new Base64().encode(userCredentials.getBytes()));
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty ("Authorization", basicAuth);
            urlConnection.setRequestProperty("Content-Type", "application/json");
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
                    if (object.getString("@odata.type").equals("#Model.MobileReport") && notify.equals("1"))
                    {
                        response++;
                    }
                    DatabaseAdapter db = new DatabaseAdapter(this);
                    if (db.getCount() != 0 && indicatorAlert.equals("1"))
                    {
                        List<Indicator> indicatorList = db.getAllIndicators();
                        if (object.getString("@odata.type").equals("#Model.Kpi"))
                        {
                            for (Indicator indicator : indicatorList)
                            {
                                if (object.getString("Name").equals(indicator.indiName))
                                {
                                    String value = object.getJSONObject("Values").getString("Value");
                                    if (Integer.parseInt(value) < indicator.alertValue)
                                    {
                                        indicators.add(indicator);
                                    }
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
    }

}
