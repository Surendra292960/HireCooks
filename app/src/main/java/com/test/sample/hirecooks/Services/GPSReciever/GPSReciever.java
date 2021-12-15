package com.test.sample.hirecooks.Services.GPSReciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class GPSReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().matches("android.location.GPS_ENABLED_CHANGE")) {
            boolean enabled = intent.getBooleanExtra("enabled",false);
            if(enabled){
                System.out.println( "Suree : "+ " Enable" );
            }else{
                System.out.println( "Suree : "+ " Disable" );
            }
        }
    }
}