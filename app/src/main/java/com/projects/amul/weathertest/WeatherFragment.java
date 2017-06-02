package com.projects.amul.weathertest;

import android.Manifest;
import android.support.v4.app.Fragment;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
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

import static android.support.v4.content.PermissionChecker.checkSelfPermission;


/**
 * Fragment that handles the weather operations, along with getting location info from the GPS to
 * access the relevant information
 *
 * @author Amul Pokharel
 */

public class WeatherFragment extends Fragment {

    private WeatherObj weather;
    private String icon = "";
    private static Handler handler = new Handler();
    private final int LOCATION_GRANTED = 2;
    private static boolean PERMISSION_GRANTED = false;

    @BindView(R.id.weatherIcon) TextView iconView;
    @BindView(R.id.weatherText) TextView weatherText;
    @BindView(R.id.locationName) TextView locationName;
    @BindView(R.id.humidity) TextView humidity;
    @BindView(R.id.pressure) TextView pressure;

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
        setRetainInstance(true);
    }


    /**
     * Runnable thread for updating the weather + acquiring location
     */
    private Runnable weatherUpdateThread = new Runnable() {
        @Override
        public void run() {
            updateLocation();
            handler.removeCallbacks(weatherUpdateThread);
        }
    };


    /**
     * Updates the location after getting the location information.
     */
    public void updateLocation(){

        double lng = 0;
        double lat = 0;

        if (checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_GRANTED);

            Log.d("permission", "checked");

            return;
        }
        else{
            LocationProvider locationProvider = new LocationProvider(getActivity().getApplicationContext());

            lng = locationProvider.getLongitude();
            lat = locationProvider.getLatitude();
        }


        downloadWeather(lat, lng);
    }

    /**
     * Method that downloads the weather based on the passed parameters
     * @param lat Latitude for acquiring weather
     * @param lng Longitude for acquiring weather
     */
    private void downloadWeather(double lat, double lng){
        String urlText = "http://api.openweathermap.org/data/2.5/weather?lat=" + Double.toString(lat) + "&lon=" + Double.toString(lng) + "&appid=50aaa0b9c38198d17df8b2140f09879e&units=metric";

        Log.i("URL", urlText);

        DownloadTask dt = new DownloadTask();
        String result ="";

        try {
            result = dt.execute(urlText).get();
            parseAndupdateUI(result);

        }
        catch (Exception e)
        {
            Log.e("stuff happened", "stuff!");
            e.printStackTrace();
        }
    }

    /**
     * Parses the result from downloadWeather into revelant fields and updates the UI
     * @param jsonResult The json result returend by the query from downloadWeather
     * @see public void downloadWeather(double late, double lng)
     */
    private void parseAndupdateUI(String jsonResult){
        Gson gson = new Gson();
        weather = gson.fromJson(jsonResult, WeatherObj.class);

        weatherText.setText(Integer.toString((weather.getMain().getTemp().intValue())) + " °C");
        //maxTemp.setText(" ▴" + Integer.toString((weather.getMain().getTempMax().intValue())) + "°C");
        //minTemp.setText(" ▾" + Integer.toString((weather.getMain().getTempMin().intValue())) + "°C");
        humidity.setText("Humidity: " + (Double.toString((weather.getMain().getHumidity().doubleValue())))+" %");
        pressure.setText("Pressure: " + (Double.toString((weather.getMain().getPressure().doubleValue()))) + " hpa");
        locationName.setText(weather.getName());

        icon = "w" + weather.getWeather().get(0).getIcon();
        int resourceID = getActivity().getApplicationContext().getResources().getIdentifier(icon, "string", getActivity().getPackageName());

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
    /**
     * Function that runs when permission is requested to re-run the needed operations
     * instead of aborting
     */
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_GRANTED: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("yay", "permission granted");

                    LocationProvider locationProvider = new LocationProvider(getActivity().getApplicationContext());

                    downloadWeather(locationProvider.getLatitude(), locationProvider.getLongitude());

                } else {
                    downloadWeather(27.717245, 85.323960);
                }
                return;
            }
        }
    }

}
