package com.example.mahe.publictransport;
//ca-app-pub-1568752486126597~7900936066

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    EditText startLocation;
    EditText endLocation;
    Button navigate;
    Button Station;
    Button Transport;
    LatLng source=null;
    LatLng destination=null;
    LocationManager locationManager;
    LocationListener locationListener;

     ArrayList<String> steps;
    StaggeredGridLayoutManager layoutManager;
    private int mPosition = RecyclerView.NO_POSITION;
    RecyclerView mRecyclerView;
    stepsadapter adapter;
    AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(!isNetworkAvailable())
        {
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(this);
            }
            builder.setTitle("No Internet Connection")
                    .setMessage("You need internet connection to run this app")


                    .setNegativeButton("close app", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                finishAndRemoveTask();
                            }else
                            {
                                finishAffinity();
                            }

                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }else{



       /* if(Build.VERSION.SDK_INT<23) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,  locationListener);
        }else {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }*/
       Station=(Button)findViewById(R.id.stations);
       startLocation=(EditText)findViewById(R.id.FromPlace);
        endLocation=(EditText)findViewById(R.id.ToPlace);
        navigate=(Button)findViewById(R.id.Navigate);
        Transport=(Button)findViewById(R.id.Transport);

        mRecyclerView=(RecyclerView)findViewById(R.id.steps);
        layoutManager =
                new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL);


        /* setLayoutManager associates the LayoutManager we created above with our RecyclerView */
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        adapter=new stepsadapter(this);
        mRecyclerView.setAdapter(adapter);



    }}

    public void getTransport(View view)
    {
        mRecyclerView.setVisibility(View.VISIBLE);




        if(steps!=null)
        {
            steps.clear();
        }
        if(startLocation.getText().toString().equals("") || endLocation.getText().toString().equals(""))
        {
            Toast.makeText(this,"Start and end location need to be specified",Toast.LENGTH_LONG).show();
            return;
        }
        Station.setVisibility(View.VISIBLE);
        navigate.setVisibility(View.VISIBLE);
        if(startLocation.getText().toString().equals("My location"))
        {
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
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                } else {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                    Location lastknownlocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    source=new LatLng(lastknownlocation.getLatitude(),lastknownlocation.getLongitude());
                }

            }}else
                {
                    String startaddress=startLocation.getText().toString();
                    source=Utilities.getLatLng(startaddress,this);

                }

        String endaddress=endLocation.getText().toString();

        destination=Utilities.getLatLng(endaddress,this);
        if(source==null )
        {
            Toast.makeText(this,"Please specify more specific source location",Toast.LENGTH_LONG).show();
            Station.setVisibility(View.INVISIBLE);
            navigate.setVisibility(View.INVISIBLE);
            return;
        }if(destination==null)
    {
        Toast.makeText(this,"Please specify more specific destination location",Toast.LENGTH_LONG).show();
        Station.setVisibility(View.INVISIBLE);
        navigate.setVisibility(View.INVISIBLE);
        return;

    }
        steps=Steps.getRoute(source,destination);
        if(steps==null)
        {
            Toast.makeText(this,"please be more specific",Toast.LENGTH_LONG).show();
             Station.setVisibility(View.INVISIBLE);
            navigate.setVisibility(View.INVISIBLE);
            return;
        }

        adapter.swaplist(steps);
        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
        layoutManager.scrollToPositionWithOffset(mPosition,0);
        mRecyclerView.setVisibility(View.VISIBLE);


    }
public void startNavigation(View view)
{

    if(startLocation.getText().toString().equalsIgnoreCase("My location"))
    {
        if(Build.VERSION.SDK_INT<23) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,  locationListener);
        }else {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                Location lastknownlocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                source=new LatLng(lastknownlocation.getLatitude(),lastknownlocation.getLongitude());
            }

        }
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
  }else
    {
        if(Build.VERSION.SDK_INT<23) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,  locationListener);
        }else {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 2);
            } else {
                String startaddress=startLocation.getText().toString();
                source=Utilities.getLatLng(startaddress,this);

            }

        }

    }

    String endaddress=endLocation.getText().toString();

    destination=Utilities.getLatLng(endaddress,this);
    if(source==null )
    {
        Toast.makeText(this,"Please specify more specific source location",Toast.LENGTH_LONG).show();
        return;
    }if(destination==null)
{
    Toast.makeText(this,"Please specify more specific destination location",Toast.LENGTH_LONG).show();
    return;

}
    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
            Uri.parse("http://maps.google.com/maps?saddr="+source.getLatitude()+","+source.getLongitude()+"&daddr="+destination.getLatitude()+","+destination.getLongitude()));
    startActivity(intent);

}
public void getStations(View view)
{
    Intent intent=new Intent(this,StationsActivity.class);
    if(startLocation.getText()==null || endLocation.getText()==null)
    {
        Toast.makeText(this,"Please enter transport locations",Toast.LENGTH_LONG).show();
        return;
    }
    intent.putExtra("source latitude",source.getLatitude());
    intent.putExtra("source longitude",source.getLongitude());
    intent.putExtra("dest latitude",destination.getLatitude());
    intent.putExtra("dest longitude",destination.getLongitude());
    startActivity(intent);
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
                    source=new LatLng(lastknownlocation.getLatitude(),lastknownlocation.getLongitude());

                }
            }
        }else
        {
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
                {
                    String startaddress=startLocation.getText().toString();
                    source=Utilities.getLatLng(startaddress,this);


                }

        }
    }

}
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }



}
