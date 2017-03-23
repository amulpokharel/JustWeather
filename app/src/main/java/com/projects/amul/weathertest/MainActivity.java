package com.projects.amul.weathertest;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

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

                
                String line;
                while((line = reader.readLine()) != null){
                    buffer.append(line + "\n");
                }

                if(buffer == null){
                    return null;
                }

                result = buffer.toString();

                return result;
            }
            catch (Exception e){

                e.printStackTrace();

            }
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

            return result;

        }
    }
}
