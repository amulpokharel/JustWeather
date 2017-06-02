/**
 * Main Activity to hold the fragments.
 * Created by Amul on 5/31/17.
 */
package com.projects.amul.weathertest;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.View;


public class MainActivity extends FragmentActivity {
    FragmentManager fm = getSupportFragmentManager();
    WeatherFragment weather_frag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState == null){
            weather_frag = new WeatherFragment();
        }
        else{
            weather_frag = (WeatherFragment)fm.getFragment(savedInstanceState, "weather fragment");
        }
        fm.beginTransaction().replace(R.id.mainFrame, weather_frag).commit();
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    public void onSaveInstanceState(Bundle outState){
        fm.putFragment(outState,"weather fragment",weather_frag);
        super.onSaveInstanceState(outState);
    }

}
