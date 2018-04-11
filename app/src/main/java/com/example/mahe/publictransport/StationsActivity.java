package com.example.mahe.publictransport;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class StationsActivity extends AppCompatActivity {
    LocationManager locationManager;
    LocationListener locationListener;
    LatLng src;
    LatLng destination;
    ArrayList<String> stations;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stations);
        setTitle("Navigate to Stations");
        ListView stationsview=(ListView)findViewById(R.id.StationsList);
        double srclat=getIntent().getDoubleExtra("source latitude",0.0);
        double srclon=getIntent().getDoubleExtra("source longitude",0.0);
        double destlat=getIntent().getDoubleExtra("dest latitude",0.0);
        double destlon=getIntent().getDoubleExtra("dest longitude",0.0);
        LatLng source=new LatLng(srclat,srclon);
        final LatLng dest=new LatLng(destlat,destlon);

        /*if(Build.VERSION.SDK_INT<23) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,  locationListener);
        }else {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }*/
        Steps.getRoute(source,dest);
       stations=new ArrayList<>();
        stations=Steps.getStations();
        if(stations.size()==0 || stations==null)
        {
            stations.add("NO MEANS OF TRANSPORT FOUND");
            ArrayAdapter adapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,stations);
            stationsview.setAdapter(adapter);
            return;
        }
        ArrayAdapter adapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,stations);
        stationsview.setAdapter(adapter);
        stationsview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                locationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
                locationListener = new android.location.LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {

                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                };
                if(Build.VERSION.SDK_INT<23) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,  locationListener);
                }else {
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(StationsActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    } else {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                        Location lastknownlocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        src=new LatLng(lastknownlocation.getLatitude(),lastknownlocation.getLongitude());
                    }

                }

                destination=Utilities.getLatLng(stations.get(position),StationsActivity.this);
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?saddr="+src.getLatitude()+","+src.getLongitude()+"&daddr="+destination.getLatitude()+","+destination.getLongitude()));
                startActivity(intent);
            }
        });


    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==1)
        {
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
                {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0, locationListener);
                    Location lastknownlocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    src=new LatLng(lastknownlocation.getLatitude(),lastknownlocation.getLongitude());

                }
            }
        }

    }
}
