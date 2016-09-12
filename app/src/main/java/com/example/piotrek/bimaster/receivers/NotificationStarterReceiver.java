package com.example.piotrek.bimaster.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Piotrek on 2016-08-27.
 */
public class NotificationStarterReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context ctx, Intent intent)
    {
        NotificationReceiver.setupAlarm(ctx);
    }
}
