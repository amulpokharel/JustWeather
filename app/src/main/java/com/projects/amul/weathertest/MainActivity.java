package com.projects.amul.weathertest;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.BindView;

public class MainActivity extends AppCompatActivity implements LocationListener {

    @BindView(R.id.weatherText)
    TextView weather;

    @BindView(R.id.weatherIcon)
    ImageView weatherIcon;

    private int lng = 0;
    private int lat = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set up location manager + check if enabled
        LocationManager location = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        location.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        try {
            Location loc = location.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            lng = (int)loc.getLongitude();
            lat = (int)loc.getLatitude();
        }
        catch(SecurityException e)
        {

        }
        catch(Exception e){

        }

        String urlText = "http://api.openweathermap.org/data/2.5/weather?lat=" + Integer.toString(lat) + "&lon=" + Integer.toString(lng) + "&appid=50aaa0b9c38198d17df8b2140f09879e";

        Log.i("Log", urlText);
        String result ="";
        JSONObject json;

        DownloadTask dt = new DownloadTask();
        try {
            result = dt.execute(urlText).get();
            json = new JSONObject(result);

            Log.i("Log", result);
        }
        catch (Exception e)
        {
            Log.e("stuff happened", "stuff!");
        }


    }

    @Override
    public void onLocationChanged(Location location) {
        lng = (int)location.getLongitude();
        lat = (int)location.getLatitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        //prompt user to enable GPS
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }

    public class DownloadTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... urls) {

            //declare variables
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try{

                //open the url, set up inputstream
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();

                // set up a buffer
                StringBuffer buffer = new StringBuffer();

                //return null if inputstream is broke
                if(in == null)
                    return null;

                //set up a bufferedreader with the proper inputstream
                reader = new BufferedReader(new InputStreamReader(in));

                //read the json
                String line;
                while((line = reader.readLine()) != null){
                    buffer.append(line + "\n");
                }

                //if buffer is empty, just return null instead of processing
                if(buffer == null){
                    return null;
                }

                //set the result from buffer
                result = buffer.toString();

                //return the result;
                return result;
            }
            catch (Exception e){

                e.printStackTrace();

            }

            //close stuff
            finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }

            //return blank result
            return result;

        }
    }
}
