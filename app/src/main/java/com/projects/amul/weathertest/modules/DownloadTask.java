package com.projects.amul.weathertest.modules;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by amulpok on 3/27/17.
 */

public class DownloadTask extends AsyncTask<String, Void, String> {

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