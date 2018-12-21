package com.example.stas.weather.Threads;

import android.os.AsyncTask;
import android.util.Log;

import com.example.stas.weather.Tasks.GetJson;
import com.example.stas.weather.Tasks.GetJson.Callback;

import java.util.concurrent.TimeUnit;

public class MyRun extends Thread {

    private String cityName;

    private Callback callback;
    private GetJson weather;

    private boolean work = true;
    private String LOG_TAG = "MYRUN";

    public MyRun(String cityName, Callback callback) {
        this.cityName = cityName;
        this.callback = callback;
    }

    public void run() {
        while (work) {
            weather = new GetJson(null, null, callback);
            weather.execute(cityName);
            try {
                TimeUnit.SECONDS.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.d(LOG_TAG, "run: ");
        }

    }

    public void Stop() {
        if (weather.getStatus() == AsyncTask.Status.RUNNING)
            weather.cancel(false);
        work = false;
        Log.d(LOG_TAG, "Stop thread: ");
    }
}
