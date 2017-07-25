/**
 * Main Activity to hold the fragments.
 * Created by Amul on 5/31/17.
 */
package com.amulp.justweather;


import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.View;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;


public class MainActivity extends FragmentActivity {

    private static final String SAVED_WEATHER = "WeatherFragment";
    private WeatherFragment weather_frag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);

        FragmentManager fm = getSupportFragmentManager();
        weather_frag = (WeatherFragment) fm.findFragmentByTag(SAVED_WEATHER);

        if(weather_frag == null){
            weather_frag = new WeatherFragment();
            fm.beginTransaction().replace(R.id.mainFrame, weather_frag, SAVED_WEATHER).commit();
        }

        hideUI();
    }

    public void onResume(){
        super.onResume();

        hideUI();
    }

    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
    }

    private void hideUI(){
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

}
