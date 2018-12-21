package com.example.stas.weather.Objects;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.stas.weather.Enums.ErrorType;

import java.util.ArrayList;

public class Data implements Parcelable {
    public String err;
    public ErrorType errorType;
    public String cityName;
    public Integer temp;
    public String wind_speed;
    public int pressure_mm;
    public String humidity;


    public Data() {
        err = cityName = wind_speed = humidity = "";
        temp = 0;
        pressure_mm = 0;
    }

    @Override
    public String toString() {
        return "err " + err + " City " + cityName + " temp " + temp +
                " wind speed " + wind_speed + " pressure_mm " + pressure_mm + " humidity " + humidity;
    }

    private Data(Parcel in) {
        err = in.readString();
        cityName = in.readString();
        temp = in.readInt();
        wind_speed = in.readString();
        pressure_mm = in.readInt();
        humidity = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(err);
        dest.writeString(cityName);
        dest.writeInt(temp);
        dest.writeString(wind_speed);
        dest.writeInt(pressure_mm);
        dest.writeString(humidity);
    }

    public static final Creator<Data> CREATOR = new Creator<Data>() {

        @Override
        public Data createFromParcel(Parcel source) {
            return new Data(source);
        }

        @Override
        public Data[] newArray(int size) {
            return new Data[size];
        }
    };
}
