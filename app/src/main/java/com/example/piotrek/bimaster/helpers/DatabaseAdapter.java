package com.example.piotrek.bimaster.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.piotrek.bimaster.data.Indicator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Piotrek on 2016-08-29.
 */
public class DatabaseAdapter extends SQLiteOpenHelper {

    private static final String DEBUG_TAG = "BiMaster";
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "bimaster";
    private static final String DB_TABLE = "indicator";
    public static final String KEY_ID = "_id";
    public static final String ID_OPTIONS = "INTEGER PRIMARY KEY AUTOINCREMENT";
    public static final int ID_COLUMN = 0;
    public static final String KEY_NAME = "name";
    public static final String NAME_OPTIONS = "TEXT NOT NULL";
    public static final int NAME_COLUMN = 1;
    public static final String KEY_VALUE = "value";
    public static final String VALUE_OPTIONS = "INTEGER DEFAULT 0";
    public static final int VALUE_COLUMN = 2;


    private static final String DB_CREATE_BIMASTER_TABLE =
            "CREATE TABLE " + DB_TABLE + "( " +
                    KEY_ID + " " + ID_OPTIONS + ", " +
                    KEY_NAME + " " + NAME_OPTIONS + ", " +
                    KEY_VALUE + " " + VALUE_OPTIONS +
                    ");";
    private static final String DROP_BIMASTER_TABLE =
            "DROP TABLE IF EXISTS " + DB_TABLE;


    public DatabaseAdapter(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE_BIMASTER_TABLE);

        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(DROP_BIMASTER_TABLE);
            onCreate(db);
        }


    public void insertIndicator(Indicator indicator) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues newTodoValues = new ContentValues();
        newTodoValues.put(KEY_NAME, indicator.indiName);
        newTodoValues.put(KEY_VALUE, indicator.value);
        db.insert(DB_TABLE, null, newTodoValues);
        db.close();
    }

    public void deleteIndicator(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String where = KEY_NAME + "='" + name +"'";
        db.delete(DB_TABLE, where, null);
        db.close();
    }


    public void updateIndicator(Indicator indicator) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = KEY_NAME + "=" + "'" + indicator.indiName + "'";
        int newValue = indicator.alertValue;
        ContentValues updateTodoValues = new ContentValues();
        updateTodoValues.put(KEY_NAME, indicator.indiName);
        updateTodoValues.put(KEY_VALUE, newValue);
        db.update(DB_TABLE, updateTodoValues, where, null);
        db.close();
    }

    public List<Indicator> getAllIndicators() {
        List<Indicator> indicatorList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + DB_TABLE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Indicator indicator = new Indicator();
                indicator.indiName = (cursor.getString(1));
                indicator.alertValue = (cursor.getInt(2));
                indicatorList.add(indicator);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return indicatorList;

    }


    public Indicator getIndicator(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {KEY_ID, KEY_NAME, KEY_VALUE};
        String where = KEY_NAME + "='" + name +"'";
        Cursor cursor = db.query(DB_TABLE, columns, where, null, null, null, null);
        Indicator indicator = new Indicator();
        if(cursor != null && cursor.moveToFirst()) {
            String indiName = cursor.getString(NAME_COLUMN);
            int value = cursor.getInt(VALUE_COLUMN);
            indicator.indiName = indiName;
            indicator.alertValue = value;
        }
        cursor.close();
        db.close();
        return indicator;
    }


    public int getCount() {
        String countQuery = "SELECT  * FROM " + DB_TABLE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }
}
