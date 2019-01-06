package com.example.stas.weather.Activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stas.weather.Objects.Data;
import com.example.stas.weather.R;
import com.example.stas.weather.Service.CityUpdater;

import static com.example.stas.weather.Enums.ErrorType.StrangeError;

public class Weather extends AppCompatActivity {

    private TextView tvCity;
    private TextView tvTemp;
    private TextView tvWindSpeed;
    private TextView tvPressure;
    private TextView tvHumidity;

    private Button btnBack;
    private Button btnSetService;

    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_weather);
        tvCity = findViewById(R.id.tvCity);
        tvTemp = findViewById(R.id.tvTemp);
        tvWindSpeed = findViewById(R.id.tvWindSpeed);
        tvPressure = findViewById(R.id.tvPressure);
        tvHumidity = findViewById(R.id.tvHumidity);

        btnBack = findViewById(R.id.btnBack);
        btnSetService = findViewById(R.id.btnSetServise);

        Intent result = getIntent();
        final Data weather = result.getParcelableExtra("fact");
        if (weather == null) {
            Data err = new Data();
            Intent intent = new Intent(context, Error.class);
            err.err = "Произошла непредвиденная ошибка";
            err.errorType = StrangeError;
            intent.putExtra("err", err);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        tvCity.setText("Город: " + weather.cityName);
        if (MainActivity.isCelsius) //температура приходит в Кельвинах, переводим в Цельсии или Фаренгейты
            tvTemp.setText("Температура: " + String.valueOf(weather.temp - 273) + " °C");
        else
            tvTemp.setText("Температура: "+ String.valueOf((weather.temp - 273) * (9 / 5) + 32) + " °F");
        tvWindSpeed.setText("Скорость ветра: " + weather.wind_speed + " м/с");
        tvPressure.setText("Атм. давление: " + String.valueOf ((weather.pressure_mm * 3 / 4)) + " мм рт.ст.");
        tvHumidity.setText("Влажность воздуха: " + weather.humidity + " %");
        btnBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnSetService.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMyServiceRunning(CityUpdater.class)) {
                    Toast.makeText(context, "Отслеживание уже запущено", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(context, CityUpdater.class);
                    intent.putExtra("CityName", weather.cityName);
                    startService(intent);
                    btnSetService.setEnabled(false);
                }
            }
        });

        if (result.getBooleanExtra("fromBD", false)) {
            btnSetService.setVisibility(View.GONE);
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null)
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        return false;
    }
}
