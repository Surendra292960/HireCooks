package com.test.sample.hirecooks.Services.AlarmSoundService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class GPSReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
     /*   if(ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())){
            boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY,false);
            if(noConnectivity){
                Toast.makeText(context,"Disconnected",Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(context,"Connected",Toast.LENGTH_LONG).show();
                Intent pushIntent = new Intent(context, LocationListenerService.class);
                context.startService(pushIntent);
            }
        }*/

        if (intent.getAction().matches("android.location.GPS_ENABLED_CHANGE")) {
            boolean enabled = intent.getBooleanExtra("enabled",false);
            if(enabled){
                System.out.println( "Suree : "+ " Enable" );
             /*   Intent pushIntent = new Intent(context, LocationListenerService.class);
                context.startService(pushIntent);*/
            }else{
                System.out.println( "Suree : "+ " Disable" );
            }
        }

 /*       if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
            boolean changeLocation = intent.getBooleanExtra("changeLocation",false);
            if(changeLocation){
                Toast.makeText(context, "Location Changed", Toast.LENGTH_SHORT).show();
                LocationListenerService locationListenerService = new LocationListenerService();
                locationListenerService.fn_getlocation(context);
            }else{
                Toast.makeText(context, "Location Not Changed", Toast.LENGTH_SHORT).show();
                LocationListenerService locationListenerService = new LocationListenerService();
                locationListenerService.fn_getlocation(context);
            }*/


    }
}