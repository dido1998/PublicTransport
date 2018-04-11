package com.example.mahe.publictransport;

import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

/**
 * Created by Mahe on 6/23/2017.
 */

public class Utilities {
   static LocationManager locationManager;
    static LocationListener locationListener;
    static Context mContext;


public static LatLng getLatLng(String address, Context context) {
    mContext=context;


        final String url = "http://maps.google.com/maps/api/geocode/json?address=" + address + "&sensor=false";
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {

                try {
                    URL latlngurl = new URL(url);
                    HttpURLConnection urlConnection = (HttpURLConnection) latlngurl.openConnection();
                    try {
                        InputStream in = urlConnection.getInputStream();

                        Scanner scanner = new Scanner(in);
                        scanner.useDelimiter("\\A");

                        boolean hasInput = scanner.hasNext();
                        String response = null;
                        if (hasInput) {
                            response = scanner.next();
                        }
                        scanner.close();
                        return response;
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;


                    } finally {
                        urlConnection.disconnect();

                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    return null;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }


            }
        };

        try {
            String json_response = task.execute().get();
            LatLng loc = getCoordinates(json_response);
            return loc;

        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        }


    }

public static LatLng getCoordinates(String json)
{
    try {
        JSONObject object=new JSONObject(json);
        JSONArray result=object.getJSONArray("results");
        JSONObject firstelement=result.getJSONObject(0);
        JSONObject geometry=firstelement.getJSONObject("geometry");
        JSONObject location=geometry.getJSONObject("location");
        Double latitude=location.getDouble("lat");
        Double longitude=location.getDouble("lng");
        Log.i("latitude",String.valueOf(latitude));
        Log.i("longitude",String.valueOf(longitude));

        LatLng coordinates=new LatLng(latitude,longitude);
        return coordinates;

    } catch (JSONException e) {
        e.printStackTrace();
        return null;

    }

}




}
