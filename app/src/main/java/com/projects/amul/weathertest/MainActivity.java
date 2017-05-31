/**
 * Main Activity to hold the fragments.
 * Created by Amul on 5/31/17.
 */
package com.projects.amul.weathertest;


import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


public class MainActivity extends AppCompatActivity {
    FragmentManager fm = getFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fm.beginTransaction().replace(R.id.mainFrame, new WeatherFragment()).commit();
    }

}
