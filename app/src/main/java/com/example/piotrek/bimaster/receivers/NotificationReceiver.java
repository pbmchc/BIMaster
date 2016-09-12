package com.example.piotrek.bimaster.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.example.piotrek.bimaster.services.NotificationService;

/**
 * Created by Piotrek on 2016-08-27.
 */
public class NotificationReceiver extends WakefulBroadcastReceiver {

    public static void setupAlarm(Context context)
    {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = startIntent(context);
        long interval = 1 * 60 * 1000;
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + interval, interval, pendingIntent);
    }


    @Override
    public void onReceive (Context ctx, Intent intent)
    {
        String action = intent.getAction();
        Intent srcIntent = null;

        if (action.equals("ACTION_START_NOTIFICATION_SERVICE"))
        {
            srcIntent = NotificationService.startIntent(ctx);
        }
        else if (action.equals("ACTION_DELETE_NOTIFICATION"))
        {
            srcIntent = NotificationService.deleteIntent(ctx);
        }

        if (srcIntent != null)
        {
            startWakefulService(ctx, srcIntent);
        }
    }

    private  static PendingIntent startIntent(Context ctx)
    {
        Intent intent = new Intent(ctx, NotificationReceiver.class);
        intent.setAction("ACTION_START_NOTIFICATION_SERVICE");
        return PendingIntent.getBroadcast(ctx,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private  static PendingIntent deleteIntent(Context ctx)
    {
        Intent intent = new Intent(ctx, NotificationReceiver.class);
        intent.setAction("ACTION_DELETE_NOTIFICATION");
        return PendingIntent.getBroadcast(ctx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

    }
}
