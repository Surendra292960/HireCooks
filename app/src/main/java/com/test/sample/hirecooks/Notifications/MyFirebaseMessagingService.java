package com.test.sample.hirecooks.Notifications;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.test.sample.hirecooks.Activity.Orders.RecievedOrderActivity;
import com.test.sample.hirecooks.BuildConfig;
import com.test.sample.hirecooks.Models.TokenResponse.Token;
import com.test.sample.hirecooks.Models.users.User;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.SharedPrefManager;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String CHANNEL_ID = "1212121";
    private static final String CHANNEL_NAME = "HireCooks";

    User user = SharedPrefManager.getInstance(getBaseContext()).getUser();
    Token token = SharedPrefManager.getInstance(getBaseContext()).getTokens();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            sendNotification1(remoteMessage);
        }else{
            sendNotification(remoteMessage);
        }
    }

    @SuppressLint("LongLogTag")
    private void sendNotification(RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();
        String title = data.get("title");
        String body = data.get("body");
        String firm_id = data.get("firm_id");
        String device_token = data.get("device_token");
        String click_action = data.get("click_action");
        String imageUri = data.get("image");
        String largeIconUri = data.get("icon");


        if(user!=null&&token!=null) {
            if (user != null && token != null && user.getFirmId().equalsIgnoreCase(firm_id) && token.getToken().equalsIgnoreCase(device_token)) {
                if (!isAppIsInBackground(getApplicationContext())) {
                    //foreground app
                    Log.e("remoteMessage foreground", remoteMessage.getData().toString());
                    Bitmap image = getBitmapfromUrl(imageUri);
                    Bitmap largeIcon = getBitmapfromUrl(largeIconUri);
                    final Uri NOTIFICATION_SOUND_URI = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + BuildConfig.APPLICATION_ID + "/" + R.raw.cheerful);
                    Intent resultIntent = new Intent(getApplicationContext(), RecievedOrderActivity.class);
                    resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0 /* Request code */, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
                    notificationBuilder.setAutoCancel(true)
                            .setContentIntent(pendingIntent)
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setLargeIcon(largeIcon)
                            //.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(image))
                            .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                            .setContentTitle(title)
                            //.setContentText(body)
                            .setSound(NOTIFICATION_SOUND_URI/*Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.cheerful)*/)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setFullScreenIntent(pendingIntent, true)
                            .setCategory(NotificationCompat.CATEGORY_CALL)
                            .setContentInfo("Info");
                    notificationManager.notify(1, notificationBuilder.build());

                } else {
                    Log.e("remoteMessage background", remoteMessage.getData().toString());
                    Bitmap image = getBitmapfromUrl(imageUri);
                    Bitmap largeIcon = getBitmapfromUrl(largeIconUri);
                    final Uri NOTIFICATION_SOUND_URI = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + BuildConfig.APPLICATION_ID + "/" + R.raw.cheerful);
                    Intent resultIntent = new Intent(getApplicationContext(), RecievedOrderActivity.class);
                    resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0 /* Request code */, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
                    notificationBuilder.setAutoCancel(true)
                            .setContentIntent(pendingIntent)
                            //.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(image))
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setLargeIcon(largeIcon)
                            .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                            .setContentTitle(title)
                            //.setContentText(body)
                            .setSound(NOTIFICATION_SOUND_URI/*Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.cheerful)*/)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setFullScreenIntent(pendingIntent, true)
                            .setCategory(NotificationCompat.CATEGORY_CALL)
                            .setContentInfo("Info");
                    notificationManager.notify(1, notificationBuilder.build());
                }
            } else {
                if (!isAppIsInBackground(getApplicationContext())) {
                    //foreground app
                    Log.e("remoteMessage foreground", remoteMessage.getData().toString());
                    Bitmap image = getBitmapfromUrl(imageUri);
                    Bitmap largeIcon = getBitmapfromUrl(largeIconUri);
                    Intent resultIntent = new Intent(getApplicationContext(), RecievedOrderActivity.class);
                    resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0  /*Request code */, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
                    notificationBuilder.setAutoCancel(true)
                            .setContentIntent(pendingIntent)
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setLargeIcon(largeIcon)
                            .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(image).bigLargeIcon(null))
                            .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                            .setContentTitle(title)
                            .setContentText(body)
                            .setSound(Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.cheerful))
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setFullScreenIntent(pendingIntent, true)
                            .setCategory(NotificationCompat.CATEGORY_CALL)
                            .setContentInfo("Info");
                    notificationManager.notify(1, notificationBuilder.build());

                } else {
                    Log.e("remoteMessage background", remoteMessage.getData().toString());
                    Bitmap image = getBitmapfromUrl(imageUri);
                    Bitmap largeIcon = getBitmapfromUrl(largeIconUri);
                    Intent resultIntent = new Intent(getApplicationContext(), RecievedOrderActivity.class);
                    resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0 /* Request code*/, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
                    notificationBuilder.setAutoCancel(true)
                            .setContentIntent(pendingIntent)
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setLargeIcon(largeIcon)
                            .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(image).bigLargeIcon(null))
                            .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                            .setContentTitle(title)
                            .setContentText(body)
                            .setSound(Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.cheerful))
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setFullScreenIntent(pendingIntent, true)
                            .setCategory(NotificationCompat.CATEGORY_CALL)
                            .setContentInfo("Info");
                    notificationManager.notify(1, notificationBuilder.build());
                }
            }
        }
    }

    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        }
        return isInBackground;
    }

    @SuppressLint("NewApi")
    private void sendNotification1(RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();
        String title = data.get("title");
        String body = data.get("body");
        String firm_id = data.get("firm_id");
        String device_token = data.get("device_token");
        String click_action = data.get("click_action");
        String imageUri = data.get("image");
        String largeIconUri = data.get("icon");

        if(user!=null&&token!=null) {
            if (user.getFirmId().equalsIgnoreCase(firm_id) && token.getToken().equalsIgnoreCase(device_token)) {
                if (!isAppIsInBackground(getApplicationContext())) {
                    //foreground app
                    Log.e("remoteMessage", remoteMessage.getData().toString());
                    Bitmap image = getBitmapfromUrl(imageUri);
                    Intent resultIntent = new Intent(getApplicationContext(), RecievedOrderActivity.class);
                    resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0 /* Request code */, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    //Uri defaultsound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Uri defaultsound = (Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.cheerful));
                    OreoNotification oreoNotification = new OreoNotification(this);
                    Notification.Builder builder = oreoNotification.getOreoNotification(title/*, body*/, pendingIntent, defaultsound, String.valueOf(R.drawable.ic_launcher_background));
                    int i = 0;
                    oreoNotification.getManager().notify(i, builder.build());
                } else {
                    Log.e("remoteMessage", remoteMessage.getData().toString());
                    Bitmap image = getBitmapfromUrl(imageUri);
                    Intent resultIntent = new Intent(getApplicationContext(), RecievedOrderActivity.class);
                    resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0 /* Request code */, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    //Uri defaultsound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Uri defaultsound = (Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.cheerful));
                    OreoNotification oreoNotification = new OreoNotification(this);
                    Notification.Builder builder = oreoNotification.getOreoNotification(title/*, body*/, pendingIntent, defaultsound, String.valueOf(R.drawable.ic_launcher_background));
                    int i = 0;
                    oreoNotification.getManager().notify(i, builder.build());
                }
            } else {
                if (!isAppIsInBackground(getApplicationContext())) {
                    //foreground app
                    Log.e("remoteMessage", remoteMessage.getData().toString());
                    Bitmap image = getBitmapfromUrl(imageUri);
                    Intent resultIntent = new Intent(getApplicationContext(), RecievedOrderActivity.class);
                    resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0  /*Request code*/, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    //Uri defaultsound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Uri defaultsound = (Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.cheerful));
                    OreoNotification oreoNotification = new OreoNotification(this);
                    Notification.Builder builder = oreoNotification.getOreoNotification(title, /*body,*/ pendingIntent, defaultsound, String.valueOf(R.drawable.ic_launcher_background));
                    int i = 0;
                    oreoNotification.getManager().notify(i, builder.build());
                } else {
                    Log.e("remoteMessage", remoteMessage.getData().toString());
                    Bitmap image = getBitmapfromUrl(imageUri);
                    Intent resultIntent = new Intent(getApplicationContext(), RecievedOrderActivity.class);
                    resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0  /*Request code */, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    //Uri defaultsound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Uri defaultsound = (Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.cheerful));
                    OreoNotification oreoNotification = new OreoNotification(this);
                    Notification.Builder builder = oreoNotification.getOreoNotification(title, /*body,*/ pendingIntent, defaultsound, String.valueOf(R.drawable.ic_launcher_background));
                    int i = 0;
                    oreoNotification.getManager().notify(i, builder.build());
                }
            }
        }
    }

    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.e("NEW_TOKEN = = == = = =",s);
    }
}