package com.projects.amul.weathertest;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Typeface;
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

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.BindView;

import static com.projects.amul.weathertest.R.id.weatherText;
import static com.projects.amul.weathertest.R.string.w01d;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private double lng = 0;
    private double lat = 0;
    private double temp = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize textview and imageview
        TextView iconView = (TextView) findViewById(R.id.weatherIcon);
        TextView weatherText = (TextView) findViewById(R.id.weatherText);

        iconView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/weather.ttf"));


        //set up location manager + check if enabled
        LocationManager location = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        //check permissions to sate locationmanager
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

        //set location source, and get initial long/lat
        location.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        try {
            Location loc = location.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            lng = loc.getLongitude();
            lat = loc.getLatitude();
        }
        catch(SecurityException e)
        {

        }
        catch(Exception e){

        }

        //set up the request URL based on long/lat
        String urlText = "http://api.openweathermap.org/data/2.5/weather?lat=" + Double.toString(lat) + "&lon=" + Double.toString(lng) + "&appid=50aaa0b9c38198d17df8b2140f09879e";

//        Log.i("Log", urlText);

        //initialize vars for incoming data
        String result ="";
        JSONObject json, weatherObj, mainObj;
        String icon = "";
        JSONArray arr;

        //set up download task
        DownloadTask dt = new DownloadTask();

        //get data, parse, get important data
        try {
            result = dt.execute(urlText).get();
            json = new JSONObject(result);

            arr = json.getJSONArray("weather");
            weatherObj = arr.getJSONObject(0);
            mainObj = json.getJSONObject("main");

            //set text to textview
            temp = Double.parseDouble(mainObj.getString("temp"));
            temp = Math.round((temp -273.15)*100.0)/100.0;
            setTitle(json.getString("name"));

            // add the latter bit
            weatherText.setText(Double.toString(temp) + "°C");


            //get resource based on weather
            icon = "w" + weatherObj.getString("icon");
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
