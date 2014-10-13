package com.example.android.sunshine.app.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.sunshine.app.R;
import com.example.android.sunshine.app.activity.DetailActivity;
import com.example.android.sunshine.app.converter.ForecastDataConverter;
import com.example.android.sunshine.app.utils.AppUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by A704968 on 10/9/14.
 */
public class ForecastFragment  extends Fragment {

    public static String TAG = ForecastFragment.class.getSimpleName();

    private ArrayAdapter<String> mForecastAdapter;
    private ListView mList;


    public ForecastFragment() {
    }

    // LESSON Learnt:
    // To render fragment specific menu options using menus.xml and handle them within the fragment.
    // Override OnCreateOptionMenus & onOptionsItemSelected.
    // Also notify the activity about the fragment having its own Option menus in OnCreate --> setHasOptionsMenu(true);

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // to handle menu events.
        setHasOptionsMenu(true);

    }

    @Override
    public void onStart(){
        super.onStart();
        updateWeatherForecast();

    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == R.id.action_refresh){
            updateWeatherForecast();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mForecastAdapter = new ArrayAdapter<String>(getActivity(),
                                                    R.layout.list_item_forecast,
                                                    R.id.list_item_forecast_textview,
                                                    new ArrayList<String>());

        mList = (ListView) rootView.findViewById(R.id.listview_forecast);
        mList.setAdapter(mForecastAdapter);
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppUtils.launchForecastDetailsActivity(ForecastFragment.this.getActivity(),
                        mForecastAdapter.getItem(position));
            }
        });
        return rootView;
    }

    // To initiate an Async task to fetch the weather forecast for the current week.
    private void updateWeatherForecast(){
        String location = AppUtils.getPreferredLocation(this.getActivity());
        FetchWeatherTask forecastTask = new FetchWeatherTask();
        forecastTask.execute(location);
    }

    /*
        LESSON Learnt:
        - Only good for Small operations where the UI components is available,
          if UI component goes away the task also goes away as its tied to UI component.

        - Services can handle long running background operations independent of UI components.
    */
     class FetchWeatherTask extends AsyncTask<String, Void, String[]> {


        @Override
        protected String[] doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String forecastJsonStr = null;

            try {
                //Building the query String....
                String urlString = buildQueryString(params[0]);

                //Making the network call to get the json...
                URL url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if (inputStream != null) {
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line + "\n");
                    }

                    forecastJsonStr = buffer.toString();
                }
            } catch (IOException e) {
                Log.e(TAG, "Error ", e);
                forecastJsonStr = null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(TAG, "Error closing stream", e);
                    }
                }
            }
            return ForecastDataConverter.getWeatherForecastFromJson(forecastJsonStr,isPreferredUnitsImperial(getActivity()));
        }

        @Override
        protected void onPostExecute(String[] forecastData) {
            if(forecastData != null) {
                mForecastAdapter.clear();
                // LESSON Learnt:
                // Arrays.asList(forecastData) will produce fixed-size list,
                // hence the list can't be modified structurally (No remove or add operations can be performed).
                List<String> list = new LinkedList<String>(Arrays.asList(forecastData));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    mForecastAdapter.addAll(list);
                } else {
                    for (String item : forecastData) {
                        mForecastAdapter.add(item);
                    }
                }
            }
        }

        public String buildQueryString(String location) {
            final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";

            String format = "json";
            String units = "metric";
            int days = 7;

            String urlString = "";
            try {
                final String LOCATION_PARAM = "q";
                final String MODE_PARAM = "mode";
                final String UNIT_PARAM = "units";
                final String DAYS_PARAM = "cnt";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(LOCATION_PARAM, location)
                        .appendQueryParameter(MODE_PARAM, format)
                        .appendQueryParameter(UNIT_PARAM, units)
                        .appendQueryParameter(DAYS_PARAM, Integer.toString(days))
                        .build();

                urlString =  builtUri.toString();

            }catch(Exception e){
                e.printStackTrace();
            }
            return urlString;
        }

        private boolean isPreferredUnitsImperial(Context ctx) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
            String units = prefs.getString(ctx.getString(R.string.pref_metrics_key), getString(R.string.pref_metrics_default));
            if (units.equals(getString(R.string.pref_unit_imperial)))
                return true;
            else if (!units.equals(getString(R.string.pref_unit_metric))){
                Log.e(TAG, "Invalid units" + units);
                return true;
            }
            else
                return false;
        }
    }

}