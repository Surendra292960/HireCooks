package com.test.sample.hirecooks.ApiServiceCall;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static com.test.sample.hirecooks.Utils.APIUrl.BASE_URL;

public class ApiClient {
    private static final int PERMISSION_RESULT = 1;
    private static Retrofit retrofit = null;
    public static Retrofit getClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor)
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS).build();

        Gson gson = new GsonBuilder().setLenient().create();
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addCallAdapterFactory(RetryCallAdapterFactory.create())
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static Bitmap getResizedBitmap (Bitmap image , int maxSize ) {
        int width = image.getWidth ( );
        int height = image.getHeight ( );
        float bitmapRatio = ( float ) width / ( float ) height;
        if ( bitmapRatio > 1 ) {
            width = maxSize;
            height = ( int ) ( width / bitmapRatio );
        } else {
            height = maxSize;
            width = ( int ) ( height * bitmapRatio );
        }
        return Bitmap.createScaledBitmap ( image , width , height , true );
    }
    public static boolean checkPermissionOfCamera ( final Context context , final String permission , String msg ) {
        if ( ContextCompat.checkSelfPermission ( context , permission ) != PackageManager.PERMISSION_GRANTED ) {
            if ( ActivityCompat.shouldShowRequestPermissionRationale ( ( Activity ) context , permission ) ) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder ( context );
                alertBuilder.setCancelable ( true );
                alertBuilder.setTitle ( "Permission necessary" );
                alertBuilder.setMessage ( msg );
                alertBuilder.setPositiveButton ( "Yes" , new DialogInterface.OnClickListener ( ) {
                    @Override
                    public void onClick ( DialogInterface dialog , int which ) {
                        ActivityCompat.requestPermissions ( ( Activity ) context , new String[] { permission } , PERMISSION_RESULT );

                    }
                } );
                alertBuilder.setNegativeButton ( "No" , new DialogInterface.OnClickListener ( ) {
                    @Override
                    public void onClick ( DialogInterface dialog , int which ) {
                    }
                } );
                AlertDialog alert = alertBuilder.create ( );
                alert.show ( );
                return false;

            } else {
                ActivityCompat.requestPermissions ( ( Activity ) context , new String[] { permission } , PERMISSION_RESULT );
                return true;
            }
        } else {
            return true;
        }

    }

}
