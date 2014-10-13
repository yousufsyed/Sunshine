package com.example.android.sunshine.app.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.example.android.sunshine.app.R;
import com.example.android.sunshine.app.activity.DetailActivity;
import com.example.android.sunshine.app.activity.SettingsActivity;

/**
 * Created by A704968 on 10/13/14.
 */
public class AppUtils {

    public static String getPreferredLocation(Context ctx){

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
        String location = sharedPref.getString(ctx.getResources().getString(R.string.pref_location_key),
                ctx.getResources().getString(R.string.pref_location_default));
        return location;
    }


    public static void launchSettingsActivity(Context ctx){
        ctx.startActivity(new Intent(ctx, SettingsActivity.class));
    }

    public static void launchForecastDetailsActivity(Context ctx,String weatherInfo){
        Intent intent = new Intent(ctx,DetailActivity.class).putExtra(Intent.EXTRA_TEXT, weatherInfo);
        ctx.startActivity(intent);
    }

    public static boolean launchMapsWithLocation(Context ctx, Uri geoLocation){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);
        if(intent.resolveActivity(ctx.getPackageManager()) != null){
            ctx.startActivity(intent);
            return true;
        } else {
            return false;
        }
    }

    public static Intent createShareIntent(String text){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT,text);
        return intent;
    }
}
