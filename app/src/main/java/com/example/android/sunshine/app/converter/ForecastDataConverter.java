package com.example.android.sunshine.app.converter;

import com.example.android.sunshine.app.data.ForecastData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by A704968 on 10/9/14.
 */
public class ForecastDataConverter {

    public static String[] getWeatherForecastFromJson(String jsonString){
        ArrayList<String> forecastList = new ArrayList<String>();
        ForecastData dayForecast = null;
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray list = (JSONArray) jsonObject.get("list");

            for(int i=0; i < list.length();i++){
                JSONObject forecast = (JSONObject) list.get(i);
                dayForecast = ForecastData.getDayForecast(forecast);
                forecastList.add(dayForecast.formattedForecastInfo());
            }
        } catch (JSONException jsonException){
            // do nothing...
        }

        return forecastList.toArray(new String[forecastList.size()]);
    }
}
