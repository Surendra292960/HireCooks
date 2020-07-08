package com.test.sample.hirecooks.Services.BackGroundService;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.model.LatLng;
import com.test.sample.hirecooks.Models.LiveTracking.LiveTracking;
import com.test.sample.hirecooks.Utils.SharedPrefManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

public class LocationListenerService extends Service implements LocationListener {
    private static final String TAG = "BookingTrackingService";
    private Context context;
    boolean isGPSEnable = false;
    boolean isNetworkEnable = false;
    double latitude, longitude;
    LocationManager locationManager;
    Location location;
    private Handler mHandler = new Handler();
    public double track_lat = 0.0;
    public double track_lng = 0.0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public LocationListenerService(Context applicationContext) {
        super();
        Log.i("HERE", "here I am!");
    }

    public LocationListenerService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.context = this;
        try{
            fn_getlocation(context);
        }catch (Exception e){
            e.printStackTrace();
        }
        //startTimer();
        Intent restartService = new Intent(getApplicationContext(), this.getClass());
        restartService.setPackage(getPackageName());
        PendingIntent restartServicePI = PendingIntent.getService(getApplicationContext(), 1, restartService, PendingIntent.FLAG_ONE_SHOT);
        //Restart the service once it has been killed android
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 30000, restartServicePI);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i("EXIT", "ondestroy!");
        if(SharedPrefManager.getInstance(context).getUser().getId()!=0&& SharedPrefManager.getInstance(context).getUser().getUserType().equalsIgnoreCase("User")){
            Intent restartService = new Intent(getApplicationContext(), this.getClass());
            restartService.setPackage(getPackageName());
            PendingIntent restartServicePI = PendingIntent.getService(getApplicationContext(), 1, restartService, PendingIntent.FLAG_ONE_SHOT);
            //Restart the service once it has been killed android
            AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 30000, restartServicePI);
        }else {
            super.onDestroy();
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        // TODO Auto-generated method stub

        if(SharedPrefManager.getInstance(context).getUser().getId()!=0){
            Intent restartService = new Intent(getApplicationContext(), this.getClass());
            restartService.setPackage(getPackageName());
            PendingIntent restartServicePI = PendingIntent.getService(getApplicationContext(), 1, restartService, PendingIntent.FLAG_ONE_SHOT);
            //Restart the service once it has been killed android
            AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 30000, restartServicePI);
        }
    }

    private void trackLocation() {
        Log.e(TAG, "trackLocation");
        String TAG_TRACK_LOCATION = "trackLocation";
        Map<String, String> params = new HashMap<>();
        params.put("latitude", "" + track_lat);
        params.put("longitude", "" + track_lng);

        Log.e(TAG, "param_track_location >> " + params.toString());

        stopSelf();
//        mTimer.cancel();

    }

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

    public void fn_getlocation(Context context) {
        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnable && !isNetworkEnable) {
            Log.e(TAG, "CAN'T GET LOCATION");
            stopSelf();
        } else {

            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                }, 10);
            }
            if (isGPSEnable) {
                location = null;
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null) {
                        Log.e(TAG, "isGPSEnable latitude" + location.getLatitude() + "\nlongitude" + location.getLongitude() + "");
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();


                        if(latitude!=0&&longitude!=0){
                            LiveTracking liveTracking = new LiveTracking();
                            liveTracking.setEmployeeId(SharedPrefManager.getInstance(LocationListenerService.this).getUser().getId());
                            liveTracking.setLatitude(""+latitude);
                            liveTracking.setLongitude(""+longitude);
                            liveTracking.setTrackingDate(new SimpleDateFormat("MM/dd/yyyy").format(new Date()));
                            liveTracking.setTrackingTime(new SimpleDateFormat("HH:mm:ss").format(new Date()));
                            //liveTracking.setTrackingDate("01/02/2019");
                            try {
                              //  addLiveTracking(liveTracking);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    //  fn_update(location);
                    }
                }else {
                    System.out.println("Location null");
                }
            }else if (isNetworkEnable) {
                location = null;
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this);
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {

                        Log.e(TAG, "isNetworkEnable latitude" + location.getLatitude() + "\nlongitude" + location.getLongitude() + "");
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();

                        if(latitude!=0&&longitude!=0){
                            LiveTracking liveTracking = new LiveTracking();
                            liveTracking.setEmployeeId(SharedPrefManager.getInstance(LocationListenerService.this).getUser().getId());
                            liveTracking.setLatitude(""+latitude);
                            liveTracking.setLongitude(""+longitude);
                            liveTracking.setTrackingDate(new SimpleDateFormat("MM/dd/yyyy").format(new Date()));
                            liveTracking.setTrackingTime(new SimpleDateFormat("HH:mm:ss").format(new Date()));
                            LatLng latLng = new LatLng(longitude, longitude);
                            //liveTracking.setTrackingDate("01/02/2019");
                            try {
                                //addLiveTracking(liveTracking);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
//                        fn_update(location);
                    }
                }
            }


            Log.e(TAG, "START SERVICE");
            //   trackLocation();

        }
    }



    /*public void addLiveTracking(final LiveTracking liveTracking) {
        LiveTrackingAPI apiService = Util.getClient().create(LiveTrackingAPI.class);
        Call<LiveTracking> call = apiService.addLiveTracking(liveTracking);
        call.enqueue(new Callback<LiveTracking>() {
            @Override
            public void onResponse(Call<LiveTracking> call, Response<LiveTracking> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try {
                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {
                        LiveTracking s = response.body();
                        if(s!=null){

                            Log.e("TAG", "Success");
                        }
                    }else {

                    }
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
//                callGetStartEnd();
            }

            @Override
            public void onFailure(Call<LiveTracking> call, Throwable t) {
                Log.e("TAG", t.toString());
            }
        });
    }*/

    private class TimerTaskToGetLocation extends TimerTask {
        @Override
        public void run() {

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    //fn_getlocation();
                }
            });

        }
    }

//    private void fn_update(Location location) {
//
//        intent.putExtra("latutide", location.getLatitude() + "");
//        intent.putExtra("longitude", location.getLongitude() + "");
//        sendBroadcast(intent);
//    }
}