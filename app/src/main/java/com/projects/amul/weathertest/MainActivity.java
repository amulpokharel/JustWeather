package com.projects.amul.weathertest;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import com.google.gson.Gson;
import com.projects.amul.weathertest.data.WeatherObj;
import com.projects.amul.weathertest.modules.DownloadTask;
import com.projects.amul.weathertest.modules.LocationProvider;

import butterknife.BindView;




public class MainActivity extends AppCompatActivity {

    private WeatherObj weather;
    private double lng = 0;
    private double lat = 0;
    private String result ="";
    private String icon = "";

    @BindView(R.id.weatherIcon) TextView iconView;
    @BindView(R.id.weatherText) TextView weatherText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iconView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/weather.ttf"));

        //set up locationprovider

        LocationProvider locationProvider = new LocationProvider(getApplicationContext());

        //get long/lat
        lng = locationProvider.getLongitude();
        lat = locationProvider.getLatitude();

        //set up the request URL based on long/lat
        String urlText = "http://api.openweathermap.org/data/2.5/weather?lat=" + Double.toString(lat) + "&lon=" + Double.toString(lng) + "&appid=50aaa0b9c38198d17df8b2140f09879e&units=metric";

        //Log.i("Log", urlText);

        //set up download task
        DownloadTask dt = new DownloadTask();

        //get data, parse, get important data
        try {

            //resulting json from URL
            result = dt.execute(urlText).get();

            //parse json with Gson
            Gson gson = new Gson();
            weather = gson.fromJson(result, WeatherObj.class);

            //set text to textview
            setTitle(weather.getName());
            weatherText.setText(Double.toString(weather.getMain().getTemp()) + "°C");

            //get resource based on weather
            icon = "w" + weather.getWeather().get(0).getIcon();
            int resourceID = getApplicationContext().getResources().getIdentifier(icon, "string", getPackageName());

            // set the correct icon from resourceid, otherwise set to ?
            if(resourceID != 0) {
                iconView.setText(getString(resourceID));
            }
            else{
                iconView.setText("?");
            }
        }
        catch (Exception e)
        {
            Log.e("stuff happened", "stuff!");
        }
    }
}
