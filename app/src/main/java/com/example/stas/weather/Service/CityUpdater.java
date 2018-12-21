package com.example.stas.weather.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.stas.weather.Activity.MainActivity;
import com.example.stas.weather.Objects.Data;
import com.example.stas.weather.R;
import com.example.stas.weather.SQL.DBHelper;
import com.example.stas.weather.Tasks.GetJson;
import com.example.stas.weather.Threads.MyRun;

import static com.example.stas.weather.Enums.ErrorType.NoInternet;

public class CityUpdater extends Service implements GetJson.Callback {

    public static final String LOG_TAG = "CityUpdater";
    private String CityName;

    private MyRun thread;

    private int notifyCounter = 1;
    private String CHANNEL_ID = "com.expl.stas.weather.CHANALID";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        CityName = intent.getStringExtra("CityName");

        thread = new MyRun(CityName, this);
        thread.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        this.thread.Stop();
        Log.d(LOG_TAG, "onDestroy service: ");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onComplete(Data result) {
        Log.d(LOG_TAG, "onComplete: " + result.errorType);
        result.cityName = CityName;
        if (result.err.equals("")) {
            DBHelper dbHelper = new DBHelper(this, "Weather", null, DBHelper.DBVersion);
            long id = dbHelper.setWeatherToBD(dbHelper.getReadableDatabase(), result);
            dbHelper.close();

            Log.d(LOG_TAG, "onComplete: " + "скачивание завершено успешно id = " + id);

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationCompat.Builder builder;

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "My channel",
                        NotificationManager.IMPORTANCE_HIGH);
                channel.setDescription("My channel description");
                channel.enableLights(true);
                channel.enableVibration(false);
                notificationManager.createNotificationChannel(channel);

                builder = new NotificationCompat.Builder(this, CHANNEL_ID);
            } else {
                builder = new NotificationCompat.Builder(this);
            }
            builder.setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Текущая погода в городе " + result.cityName);
            if (MainActivity.isCelsius)
                builder.setContentText("Сейчас " + String.valueOf(result.temp - 273)  + " °C");
            else
                builder.setContentText("Сейчас " + String.valueOf((result.temp - 273) * (9 / 5) + 32) + " °F");

            Notification notification = builder.build();
            notificationManager.notify(notifyCounter, notification);
            notifyCounter++;
        } else {
            if (result.errorType == NoInternet) {
                Log.d(LOG_TAG, "onComplete: " + "скачивание завершено с ошибкой noInternet");
            } else {
                Log.d(LOG_TAG, "onComplete: " + "скачивание завершено с ошибкой " + result.err + " errType " + result.errorType);
            }
        }
    }


}
