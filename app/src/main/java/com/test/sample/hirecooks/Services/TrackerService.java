package com.test.sample.hirecooks.Services;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.Models.Chat.Example;
import com.test.sample.hirecooks.Models.Chat.Message;
import com.test.sample.hirecooks.Models.MapLocationResponse.Map;
import com.test.sample.hirecooks.Models.MapLocationResponse.MapResponse;
import com.test.sample.hirecooks.Models.Users.User;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.Constants;
import com.test.sample.hirecooks.Utils.SharedPrefManager;
import com.test.sample.hirecooks.WebApis.MapApi;
import com.test.sample.hirecooks.WebApis.UserApi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrackerService extends Service {

    private static final String TAG = TrackerService.class.getSimpleName();
    private User user;
    private int pinCode;
    private String subAddress;

    @Override
    public IBinder onBind(Intent intent) {return null;}

    @Override
    public void onCreate() {
        super.onCreate();
        user = SharedPrefManager.getInstance(this).getUser();
        requestLocationUpdates();
    }

    private void buildNotification() {
        String stop = "stop";
        registerReceiver(stopReceiver, new IntentFilter(stop));
        PendingIntent broadcastIntent = PendingIntent.getBroadcast(
                this, 0, new Intent(stop), PendingIntent.FLAG_UPDATE_CURRENT);
        // Create the persistent notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.app_name))
                .setOngoing(true)
                .setContentIntent(broadcastIntent)
                .setSmallIcon(R.drawable.ic_launcher);
        startForeground(1, builder.build());
    }

    protected BroadcastReceiver stopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "received stop broadcast");
            // Stop the service when the notification is tapped
            unregisterReceiver(stopReceiver);
            stopSelf();
        }
    };

    private void requestLocationUpdates() {
        LocationRequest request = new LocationRequest();
        request.setInterval(10000);
        request.setFastestInterval(5000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        final String path = getString(R.string.app_name) + "/" + getString(R.string.project_id);
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            // Request location updates and when an update is
            // received, store the location in Firebase
            client.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference(path);
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        Log.d(TAG, "location update " + location);
                        ref.setValue(String.valueOf(location));
                        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                        try{
                            if(user!=null){
                                setMapDetails(latLng);
                                getMessages(user.getId());
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        Constants.USER_CURRENT_LOCATION = latLng;
                    }
                }
            }, null);
        }
    }

    private void setMapDetails(LatLng latLng) {
        Map map = new Map();
        map.setUserId(user.getId());
        map.setLatitude(String.valueOf(latLng.latitude));
        map.setLongitude(String.valueOf(latLng.longitude));
        map.setAddress(getAddress(latLng));
        map.setSubAddress(subAddress);
        map.setPincode(pinCode);
        map.setFirm_id(user.getFirmId());

        if(user.getUserType()!=null){
            if(user.getUserType().equalsIgnoreCase("Employee")||user.getUserType().equalsIgnoreCase("Rider")){
                updateMapDetails(map);
            }
        }
    }

    private String getAddress(LatLng latLng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String result = null;
        try {
            List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 10);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                try {
                    pinCode = Integer.parseInt(address.getPostalCode());
                }catch (Exception e){
                    e.printStackTrace();
                }
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    sb.append(address.getAddressLine(i)).append(",");
                }
                result = address.getAddressLine(0);
                subAddress = address.getSubLocality()+", "+address.getLocality();
                return result;
            }
            return result;
        } catch (IOException e) {
            Log.e("MapLocation", "Unable connect to Geocoder", e);
            return result;
        }
    }

    private void updateMapDetails(final Map updateMaps) {
        MapApi mapApi = ApiClient.getClient().create(MapApi.class);
        Call<MapResponse> postMapDetailsResponse = mapApi.TrackUser(updateMaps.getUserId(),updateMaps.getLatitude(),updateMaps.getLongitude(),updateMaps.getAddress(),updateMaps.getSubAddress(),String.valueOf(updateMaps.getPincode()),updateMaps.getFirm_id());
        postMapDetailsResponse.enqueue(new Callback<MapResponse>() {
            @Override
            public void onResponse(@NonNull Call<MapResponse> call, @NonNull Response<MapResponse> response) {
                if(response.code() == 200 && response.body() != null) {
                    try{
                        System.out.println("Suree : update location ");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                else {
                    System.out.println("Suree : failed to update location ");
                }
            }

            @Override
            public void onFailure(@NonNull Call<MapResponse> call, @NonNull Throwable t) {
                System.out.println("suree: "+t.getMessage());
            }
        });
    }

    private void getMessages(Integer id) {
        UserApi service = ApiClient.getClient().create(UserApi.class);
        Call<List<Example>> call = service.getMessages(id);
        call.enqueue(new Callback<List<Example>>() {
            @SuppressLint("ShowToast")
            @Override
            public void onResponse(@NonNull Call<List<Example>> call, @NonNull Response<List<Example>> response) {
                int statusCode = response.code();
                if (statusCode == 200) {
                    Log.d(TAG,"getMessages");
                    List<Message> messageList = new ArrayList<>(  );
                    List<Message> filterMessageList = new ArrayList<>(  );
                    for (Example messages:response.body()) {

                    }
                }
            }

            @SuppressLint("ShowToast")
            @Override
            public void onFailure(@NonNull Call<List<Example>> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), R.string.error + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
