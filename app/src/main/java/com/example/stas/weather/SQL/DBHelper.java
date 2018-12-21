package com.example.stas.weather.SQL;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.stas.weather.Objects.Data;

public class DBHelper extends SQLiteOpenHelper {

    private String LOG_TAG = "DBHelper";
    public static final int DBVersion = 4;

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE city (" +
                "id integer PRIMARY KEY AUTOINCREMENT," +
                "name text," +
                "temperature integer," + //1 DOUBLE
                "wind_speed text," +
                "pressure_mm integer," +
                "humidity text" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS city");
        onCreate(db);
    }

    public Data getWeatherFromBD(SQLiteDatabase db, String City) {
        Log.d(LOG_TAG, "getWeatherFromBD: " + City);
        Data result = new Data();
        Cursor city = db.rawQuery("SELECT * FROM city c WHERE c.name=?", new String[]{City});
        city.moveToLast();
        if (city.getCount() != 0) {
            result.cityName = city.getString(city.getColumnIndex("name"));
            result.temp = city.getInt(city.getColumnIndex("temperature"));
            result.wind_speed = city.getString(city.getColumnIndex("wind_speed"));
            result.pressure_mm = city.getInt(city.getColumnIndex("pressure_mm"));
            result.humidity = city.getString(city.getColumnIndex("humidity"));
            city.close();
            return result;
        }
        city.close();
        return null;
    }

    public long setWeatherToBD(SQLiteDatabase db, Data City) {
        ContentValues newValues = new ContentValues();
        newValues.put("name", City.cityName);
        newValues.put("temperature", City.temp);
        newValues.put("wind_speed", City.wind_speed);
        newValues.put("pressure_mm", City.pressure_mm);
        newValues.put("humidity", City.humidity);
        return db.insert("city", null, newValues);
    }

}
