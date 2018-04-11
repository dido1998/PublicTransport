package com.example.mahe.publictransport;

/**
 * Created by Mahe on 6/23/2017.
 */

public class LatLng {
    Double Latitude;
    Double Longitude;
    public LatLng(Double lat,Double lng)
    {
        Latitude=lat;
        Longitude=lng;
    }
    public Double getLatitude()
    {
        return Latitude;
    }
    public Double getLongitude()
    {
        return Longitude;
    }


}
