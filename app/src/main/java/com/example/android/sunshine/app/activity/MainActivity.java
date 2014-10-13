package com.example.android.sunshine.app.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.android.sunshine.app.R;
import com.example.android.sunshine.app.fragment.ForecastFragment;
import com.example.android.sunshine.app.utils.AppUtils;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {

            getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.container, new ForecastFragment())
                        .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            AppUtils.launchSettingsActivity(MainActivity.this);
            return true;
        } else if(id == R.id.action_location){
            String location = AppUtils.getPreferredLocation(MainActivity.this);
            Uri uri = Uri.parse("geo:0,0?q="+location);
            boolean result = AppUtils.launchMapsWithLocation(MainActivity.this, uri);
            if(!result){
                Toast.makeText(MainActivity.this, "Maps can't be launched", Toast.LENGTH_LONG).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

}
