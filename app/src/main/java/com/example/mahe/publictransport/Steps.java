package com.example.mahe.publictransport;

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
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

/**
 * Created by Mahe on 6/23/2017.
 */

public class Steps {

    static ArrayList<String> srcdest=new ArrayList<>();

    public static ArrayList<String> getRoute(LatLng source,LatLng destination)
    {
        ArrayList<String> steps=new ArrayList<>();
        srcdest.clear();

        final String url="https://route.cit.api.here.com/routing/7.2/calculateroute.json" +
                "?app_id=CLc4OGX16hDwhOKUch0j" +
                "&app_code=0T8DSPGptIByqFN3M5rsfg" +
                "&waypoint0=geo!"+source.getLatitude()+","+source.getLongitude()+
                "&waypoint1=geo!" +destination.getLatitude()+","+destination.getLongitude()+
                "&departure=now" +
                "&mode=fastest;publicTransport" +
                "&combineChange=true";
        Log.i("url",url);
        AsyncTask<Void,Void,String> task=new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {

                try {
                    URL latlngurl=new URL(url);
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
                        Log.i("this","5");

                        e.printStackTrace();
                        return null;



                    } finally {
                        urlConnection.disconnect();

                    }

                } catch (MalformedURLException e) {
                    Log.i("this","4");
                    e.printStackTrace();

                    return null;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }




            }
        };

        try {
            String response=task.execute().get();
            if(response==null)
            {
                return null;
            }
            steps=getSteps(response);


           return steps;


        } catch (InterruptedException e) {
            Log.i("this","6");

            e.printStackTrace();
             return null;
        } catch (ExecutionException e) {
            Log.i("this","7");

            e.printStackTrace();
             return null;
        }


    }
    public static ArrayList<String> getSteps(String json)
    {

        int ci=0;
        ArrayList<String> steps=new ArrayList<>();
        try {

            JSONObject object=new JSONObject(json);
            try{
            if(object.getString("subtype").equals("NoRouteFound"))
            {
                return null;
            }}catch (JSONException r)
            {
                Log.i("thdi","1");
                r.printStackTrace();
            }
            JSONObject response=object.getJSONObject("response");
            JSONArray route=response.getJSONArray("route");
            JSONObject firstelement=route.getJSONObject(0);
            JSONObject summary=firstelement.getJSONObject("summary");
            String text=summary.getString("text");
            String s2="";
            if(text==null)
            {
                return null;
            }


            for(int j=0;j<text.length();j++)
            {

                char ch=text.charAt(j);
                if(ch=='<')
                {
                    s2=s2+" ";

                    int c=0;
                    for(int k=j;k<text.length();k++)
                    {
                        char ch1=text.charAt(k);
                        if(ch1=='>')
                        {
                            j=k;
                            c++;
                        }
                        if(c>0)
                            break;
                    }
                }else
                {
                    s2=s2+ch;
                }
            }
            steps.add(s2);
            JSONArray leg=firstelement.getJSONArray("leg");
            JSONObject firselementofleg=leg.getJSONObject(0);
            JSONArray maneuver=firselementofleg.getJSONArray("maneuver");
            if(maneuver==null)
            {
                return null;
            }
            for(int i=0;i<maneuver.length();i++)
            {
                JSONObject eachelementofmaneuver=maneuver.getJSONObject(i);
                String instruction=eachelementofmaneuver.getString("instruction");
                try{
                if(eachelementofmaneuver.getString("stopName")!=null)
                {
                    String stop=eachelementofmaneuver.getString("stopName");
                    srcdest.add(stop);
                    Log.i("stop",stop);
                }}catch (JSONException e)
                {
                    Log.i("this","2");
                    e.printStackTrace();
                }
                String s1="";
                for(int j=0;j<instruction.length();j++)
                {

                    char ch=instruction.charAt(j);
                    if(ch=='<')
                    {
                        s1=s1+" ";

                        int c=0;
                        for(int k=j;k<instruction.length();k++)
                        {
                            char ch1=instruction.charAt(k);
                            if(ch1=='>')
                            {
                                j=k;
                                c++;
                            }
                            if(c>0)
                                break;
                        }
                    }else
                    {
                        s1=s1+ch;
                    }
                }

                steps.add(s1);
            }
            return steps;




        } catch (JSONException e) {
            e.printStackTrace();
            Log.i("this","3");
            return null;
        }

    }
public static ArrayList<String> getStations()
{
    return srcdest;
}

}
