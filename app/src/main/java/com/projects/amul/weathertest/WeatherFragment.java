package com.projects.amul.weathertest;

import android.Manifest;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.projects.amul.weathertest.data.WeatherObj;
import com.projects.amul.weathertest.modules.DownloadTask;
import com.projects.amul.weathertest.modules.LocationProvider;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class WeatherFragment extends Fragment {

    private WeatherObj weather;
    private String icon = "";
    private static Handler handler = new Handler();
    private final int LOCATION_GRANTED = 2;

    @BindView(R.id.weatherIcon) TextView iconView;
    @BindView(R.id.weatherText) TextView weatherText;
    @BindView(R.id.locationName) TextView locationName;
    @BindView(R.id.maxTemp) TextView maxTemp;
    @BindView(R.id.minTemp) TextView minTemp;

    public static WeatherFragment newInstance() {

        Bundle args = new Bundle();
        WeatherFragment fragment = new WeatherFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.weather_fragment, container, false);
        ButterKnife.bind(this, view);
        iconView.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/weather.ttf"));
        handler.postDelayed(weatherUpdateThread, 0L);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    private Runnable weatherUpdateThread = new Runnable() {
        @Override
        public void run() {
            updateLocation();
            handler.removeCallbacks(weatherUpdateThread);
        }
    };


    @OnClick(R.id.locationIcon)
    public void updateLocation(){

        double lng = 0;
        double lat = 0;

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.



            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_GRANTED);

            return;
        }
        else{
            LocationProvider locationProvider = new LocationProvider(getActivity().getApplicationContext());

            //get long/lat
            lng = locationProvider.getLongitude();
            lat = locationProvider.getLatitude();
        }


        downloadWeather(lat, lng);
    }

    private void downloadWeather(double lat, double lng){
        String urlText = "http://api.openweathermap.org/data/2.5/weather?lat=" + Double.toString(lat) + "&lon=" + Double.toString(lng) + "&appid=50aaa0b9c38198d17df8b2140f09879e&units=metric";

        Log.i("URL", urlText);

        DownloadTask dt = new DownloadTask();
        String result ="";

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
        int resourceID = getActivity().getApplicationContext().getResources().getIdentifier(icon, "string", getActivity().getPackageName());

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

                    LocationProvider locationProvider = new LocationProvider(getActivity().getApplicationContext());

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
