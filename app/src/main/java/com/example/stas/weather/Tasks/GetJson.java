package com.example.stas.weather.Tasks;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.stas.weather.Objects.Data;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

import static com.example.stas.weather.Enums.ErrorType.JSONParse;
import static com.example.stas.weather.Enums.ErrorType.NoInternet;
import static com.example.stas.weather.Enums.ErrorType.WrongRequest;

public class GetJson extends AsyncTask<String, Void, Data> {

    public interface Callback {
        void onComplete(Data result);
    }

    private static final String LOG_TAG = "GetWeather";
    private Callback callback;
    private ProgressBar pbLoading;
    private Button btnGetWeather;
    private static final String mUrl = "http://api.openweathermap.org/data/2.5/weather?q=";
    private static final String key = "&appid=f86628d4407790e972163f4c9a8d3513";

    public GetJson(ProgressBar pbLoading, Button btnGetWeather, Callback callback) {
        this.callback = callback;
        this.btnGetWeather = btnGetWeather;
        this.pbLoading = pbLoading;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d(LOG_TAG,"onPre");
        if (pbLoading != null)
            pbLoading.setVisibility(View.VISIBLE);  //To show ProgressBar
        if (btnGetWeather != null)
            btnGetWeather.setEnabled(false);
    }

    @Override
    protected Data doInBackground(String... params) {
        Log.d(LOG_TAG, "doInBackground: ");
        Data result = new Data();
        result.err = "";
        result.cityName = params[0];
        StringBuilder json = new StringBuilder();
        BufferedReader reader = null;
        try {
            URL url = new URL(mUrl + params[0] + key);
            Log.d(LOG_TAG, "doInBackground: ya " + url.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setReadTimeout(2000);
            conn.connect();
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String s;
                while ((s = reader.readLine()) != null) {
                    json.append(s);
                }
            } else {
                Log.e(LOG_TAG, "responseCode  = " + responseCode);
                result.err = "Запрос вернул код ошибки " + responseCode + ".\n\nСкорее всего, данный город не существует на стороне API.\n";
                result.errorType = WrongRequest;
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Ошибка " + e.getMessage());
            result.err = "С вашим Интернет соединением не всё так просто...\n\nПроверьте Интернет соединение.\n";
            result.errorType = NoInternet;
        }
        //Log.d(LOG_TAG, "json: " + json);
        if (!json.toString().equals("") & result.err.equals("")) {
            try {
                JSONObject jsonObject = new JSONObject(json.toString());
                JSONObject main = jsonObject.getJSONObject("main");
                JSONObject wind = jsonObject.getJSONObject("wind");
                result.temp = main.getInt("temp");
                result.pressure_mm = main.getInt("pressure");
                //result.pressure_mm = String.valueOf(main.getInt("pressure"));
                //result.pressure_mm = main.getString("pressure_mm");
                result.humidity = String.valueOf(main.getInt("humidity"));
                result.wind_speed = String.valueOf(wind.getDouble("speed"));
            } catch (JSONException e) {
                Log.w(LOG_TAG, "JSONException " + Arrays.toString(e.getStackTrace()));
                result.err = "Ошибка обработки ответа";
                result.errorType = JSONParse;
            }
        }
        return result;
    }

    @Override
    protected void onPostExecute(Data result) {
        super.onPostExecute(result);
        if (pbLoading != null)
            pbLoading.setVisibility(View.GONE);     // To Hide ProgressBar
        if (btnGetWeather != null)
            btnGetWeather.setEnabled(true);
        callback.onComplete(result);
    }
}
