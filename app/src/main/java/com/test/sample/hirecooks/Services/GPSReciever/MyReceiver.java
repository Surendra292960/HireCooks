package com.test.sample.hirecooks.Services.GPSReciever;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import com.test.sample.hirecooks.Utils.NetworkUtil;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        String status = NetworkUtil.getConnectivityStatusString(context);
        if(status.isEmpty()||status.equals("No internet is available")||status.equals("No Internet Connection")) {
            status="No Internet Connection";
        }
     //   Toast.makeText(context, status, Toast.LENGTH_LONG).show();
    }
}