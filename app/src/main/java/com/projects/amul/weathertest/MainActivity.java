package com.projects.amul.weathertest;

import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.provider.Settings;
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

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.weatherText) TextView weather;

    @BindView(R.id.weatherIcon) ImageView weatherIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set up location manager + check if enabled
        LocationManager location = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = location.isProviderEnabled(LocationManager.GPS_PROVIDER);
        int lng = 0, lat = 0;

        //prompt user to enable
        if(!enabled){
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
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
