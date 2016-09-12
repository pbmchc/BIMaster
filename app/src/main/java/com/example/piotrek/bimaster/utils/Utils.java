package com.example.piotrek.bimaster.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Set;

/**
 * Created by Piotrek on 2016-08-25.
 */
public class Utils {

    public static String readSP(Context ctx, String settingName, String value)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return  sharedPreferences.getString(settingName, value);
    }

    public static void saveSP(Context ctx, String settingName, String value)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(settingName, value);
        editor.apply();
    }
    //test purposes only
    public static void deleteSP(Context ctx)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }

    public static void deleteOneSP(Context ctx, String settingName)
    {
        SharedPreferences mySPrefs =PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = mySPrefs.edit();
        editor.remove(settingName);
        editor.apply();
    }
}
