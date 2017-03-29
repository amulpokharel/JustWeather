package com.projects.amul.weathertest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import com.google.gson.Gson;
import com.projects.amul.weathertest.data.WeatherObj;
import com.projects.amul.weathertest.modules.DownloadTask;
import com.projects.amul.weathertest.modules.LocationProvider;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity {

    private WeatherObj weather;
    private String icon = "";
    private final int LOCATION_GRANTED = 2;

    @BindView(R.id.weatherIcon) TextView iconView;
    @BindView(R.id.weatherText) TextView weatherText;
    @BindView(R.id.locationName) TextView locationName;
    @BindView(R.id.maxTemp) TextView maxTemp;
    @BindView(R.id.minTemp) TextView minTemp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //Remove notification bar
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        //butterknife bind
        ButterKnife.bind(this);

        //set typeface to the weather icon
        iconView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/weather.ttf"));

        //update function
        updateLocation();
    }


    @OnClick(R.id.locationIcon)
    public void updateLocation(){

        double lng = 0;
        double lat = 0;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.



            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_GRANTED);

            return;
        }
        else{
            LocationProvider locationProvider = new LocationProvider(getApplicationContext());

            //get long/lat
            lng = locationProvider.getLongitude();
            lat = locationProvider.getLatitude();
        }


        downloadWeather(lat, lng);
    }

    private void downloadWeather(double lat, double lng){
        //set up the request URL based on long/lat
        String urlText = "http://api.openweathermap.org/data/2.5/weather?lat=" + Double.toString(lat) + "&lon=" + Double.toString(lng) + "&appid=50aaa0b9c38198d17df8b2140f09879e&units=metric";

        Log.i("URL", urlText);

        //set up download task
        DownloadTask dt = new DownloadTask();
        String result ="";

        //get data, parse, get important data
        try {

            //resulting json from URL
            result = dt.execute(urlText).get();
            parseAndupdateUI(result);

        }
        catch (Exception e)
        {
            Log.e("stuff happened", "stuff!");
            e.printStackTrace();
        }
    }

    private void parseAndupdateUI(String jsonResult){
        //parse json with Gson
        Gson gson = new Gson();
        weather = gson.fromJson(jsonResult, WeatherObj.class);

        //set text to textview
        weatherText.setText(Integer.toString((weather.getMain().getTemp().intValue())) + "°C");
        maxTemp.setText(" ▴" + Integer.toString((weather.getMain().getTempMax().intValue())) + "°C");
        minTemp.setText(" ▾" + Integer.toString((weather.getMain().getTempMin().intValue())) + "°C");
        locationName.setText(weather.getName());

        //get resource based on weather
        icon = "w" + weather.getWeather().get(0).getIcon();
        int resourceID = getApplicationContext().getResources().getIdentifier(icon, "string", getPackageName());

        // set the correct icon from resourceid, otherwise set to ?
        if(resourceID != 0) {
            iconView.setText(getString(resourceID));
        }
        else{
            iconView.setText("?");
            //Log.i("Info", icon);
            //Log.i("Info", Integer.toString(resourceID));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_GRANTED: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    LocationProvider locationProvider = new LocationProvider(getApplicationContext());

                    //get long/lat
                    downloadWeather(locationProvider.getLatitude(), locationProvider.getLongitude());

                } else {
                    downloadWeather(27.717245, 85.323960);
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
