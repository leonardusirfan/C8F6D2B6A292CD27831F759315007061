package com.leonardus.locationmanager;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class AppLocationManager {
    public final static int ACTIVATE_LOCATION = 16;
    public final static int PERMISSION_LOCATION = 17;

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME = 1000; // 1 second
    private boolean permission_gained = false;

    private AppCompatActivity activity;
    private AppLocationListener listener;
    private Location current_location;

    public AppLocationManager(AppCompatActivity activity, AppLocationListener listener) {
        this.activity = activity;
        this.listener = listener;
    }

    public void startLocationService(){
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        if(!permission_gained && Build.VERSION.SDK_INT >= 23){
            //System.out.println("cek permission");
            checkPermission();
        }
        else{
            //System.out.println("cek enable");
            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_FINE);
                criteria.setAltitudeRequired(false);
                criteria.setBearingRequired(false);
                criteria.setCostAllowed(true);
                criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
                String provider = locationManager.getBestProvider(criteria, true);

                locationManager.requestLocationUpdates(provider, MIN_TIME, MIN_DISTANCE, new android.location.LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        //System.out.println("update");
                        listener.onLocationChange(location);
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                        Log.v("LocationListener", provider + " : " + status);
                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                        startLocationService();
                    }
                });
            }
            else{
                showActivateGPS();
            }
        }
    }

    private void showActivateGPS(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Akses Lokasi Tidak Menyala");
        builder.setMessage("Aplikasi membutuhkan akses lokasi untuk dapat berjalan dengan benar. Nyalakan sekarang?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), ACTIVATE_LOCATION);
            }
        });
       /* builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });*/
        builder.setCancelable(false);
        AlertDialog dialog_lokasi = builder.create();
        if(!activity.isFinishing()){
            dialog_lokasi.show();
        }
    }

    private void checkPermission(){
        if(ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)){
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("Izin Lokasi");
                builder.setMessage("Aplikasi membutuhkan izin lokasi untuk dapat berjalan dengan benar.");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_LOCATION);
                    }
                });
                builder.setCancelable(false);
                builder.create().show();
            }
            else{
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_LOCATION);
            }
        }
        else{
            permission_gained = true;
            startLocationService();
        }
    }

    public interface AppLocationListener {
        void onLocationChange(Location location);
    }
}
