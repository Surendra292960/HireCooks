package com.test.sample.hirecooks.Services;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import com.test.sample.hirecooks.Activity.Home.MainActivity;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.CheckConnection;
import com.test.sample.hirecooks.Utils.NetworkUtil;

@SuppressLint("Registered")
public class ForegroundService extends Service {
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    private CheckConnection checkConnection;
    @Override
    public void onCreate() {
        super.onCreate();
    }
 
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String input = intent.getStringExtra("inputExtra");
        checkInternetConnection();
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
 
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Foreground Service")
                .setContentText(input)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(pendingIntent)
                .build();
 
        startForeground(1, notification);
 
        //do heavy work on a background thread

        //stopSelf();
 
        return START_NOT_STICKY;
    }

    private void checkInternetConnection() {
        if(NetworkUtil.checkInternetConnection(this)) {

        }
        else {
            Toast.makeText(getApplicationContext(),"Please Check your internet Connection",Toast.LENGTH_LONG).show();
            checkConnection = new CheckConnection();
            checkConnection.showAlert(getResources().getString(R.string.no_internet));
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
 
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
 
}