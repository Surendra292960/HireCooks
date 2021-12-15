package com.test.sample.hirecooks.Notifications;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class NotificationReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String tag = "NotificationReciever";
        Log.d("TestReciever","intent="+intent);
        String message = intent.getStringExtra("message");
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
        Log.d(tag,message);
    }
}
