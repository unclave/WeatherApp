package com.example.stas.weather.Activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;

import com.example.stas.weather.Objects.Data;
import com.example.stas.weather.R;
import com.example.stas.weather.SQL.DBHelper;
import com.example.stas.weather.Service.CityUpdater;
import com.example.stas.weather.Tasks.GetJson;

import static com.example.stas.weather.Enums.ErrorType.NoInternet;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MainAct";
    public static boolean isCelsius = true;

    private EditText etCity;

    private Button btnSearch;
    private Button btnStopService;
    private RadioGroup radioGroup;

    private ProgressBar progressBar;

    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etCity = findViewById(R.id.etCity);

        btnSearch = findViewById(R.id.btnSearch);
        btnStopService = findViewById(R.id.btnStopService);

        progressBar = findViewById(R.id.progressBar);

        radioGroup = findViewById(R.id.rg);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                isCelsius = group.getCheckedRadioButtonId() == R.id.rbCel;
            }
        });

        btnSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "onClick: ");
                if (!etCity.getText().toString().trim().equals("")) {
                    Log.d(LOG_TAG, "onClick:  after");
                    GetJson getJson = new GetJson(progressBar, btnSearch, new GetJson.Callback() {
                        @Override
                        public void onComplete(Data result) {
                            Log.d(LOG_TAG, "onComplete: " + result.errorType);
                            if (result.err.equals("")) {
                                Intent i = new Intent(context, Weather.class);
                                //передача фактических данных о погоде
                                i.putExtra("fact", result);
                                startActivity(i);
                            } else {
                                //подключение к БД
                                DBHelper dbHelper = new DBHelper(context, "Weather", null, DBHelper.DBVersion);
                                Data dbFact = dbHelper.getWeatherFromBD(dbHelper.getReadableDatabase(), result.cityName);
                                dbHelper.close();
                                if (dbFact != null && result.errorType == NoInternet) {
                                    Intent i = new Intent(context, Weather.class);
                                    i.putExtra("fact", dbFact);
                                    i.putExtra("fromBD", true);
                                    startActivity(i);
                                } else {
                                    Intent i = new Intent(context, Error.class);
                                    result.err = result.err + "\nЗаписей по данному городу в БД приложения не обнаружено.";
                                    i.putExtra("err", result);
                                    startActivity(i);
                                }
                            }
                        }
                    });
                    getJson.execute(etCity.getText().toString().trim());
                }
            }
        });

        btnStopService.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(context, CityUpdater.class));
                if (!isMyServiceRunning(CityUpdater.class)) {
                    v.setEnabled(false);
                } else {
                    v.setEnabled(true);
                }
            }
        });

        if (!isMyServiceRunning(CityUpdater.class)) {
            btnStopService.setEnabled(false);
        } else {
            btnStopService.setEnabled(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isMyServiceRunning(CityUpdater.class)) {
            btnStopService.setEnabled(false);
        } else {
            btnStopService.setEnabled(true);
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
