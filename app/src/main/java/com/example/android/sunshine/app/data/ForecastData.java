package com.example.android.sunshine.app.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by A704968 on 10/9/14.
 */
public class ForecastData {

    private double min;

    private double max;

    private long timestamp;

    private String desc = "";

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public String getDesc() {
        return desc;
    }

    public String getReadableDate() {
        Date date = new Date(timestamp * 1000);
        SimpleDateFormat formatter = new SimpleDateFormat("E, MMM d");
        return formatter.format(date);
    }

    private String formattedMaxMin(double high, double low, boolean isImperial) {
        if (isImperial){
            high = (high * 1.8) + 32;
            low = (low * 1.8) + 32;
        }
        return Math.round(high) + "/"+ Math.round(low);
    }

    public String formattedForecastInfo(boolean isImperial){
        return getReadableDate() + " - " + getDesc() + " - " + formattedMaxMin(max, min, isImperial);
    }

    public static ForecastData getDayForecast(JSONObject jsonObject){
        ForecastData forecastData = new ForecastData();

        try {
            forecastData.timestamp = Long.parseLong(jsonObject.optString("dt"));

            JSONObject temperatureObject = (JSONObject)jsonObject.get("temp");
            if(temperatureObject != null) {
                forecastData.max = Double.parseDouble(temperatureObject.optString("max"));
                forecastData.min = Double.parseDouble(temperatureObject.optString("min"));
            }

            JSONArray weatherArray = (JSONArray)jsonObject.get("weather");
            if(weatherArray != null && weatherArray.length() > 0) {
                 JSONObject weatherObject = (JSONObject)weatherArray.get(0);
                forecastData.desc = weatherObject.optString("main");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return forecastData;
    }

}
