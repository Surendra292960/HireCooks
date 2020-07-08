package com.test.sample.hirecooks.GoogleMapLocation;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.Models.MapLocationResponse.Map;
import com.test.sample.hirecooks.Models.MapLocationResponse.Result;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.SharedPrefManager;
import com.test.sample.hirecooks.WebApis.MapApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetUserMapLocation extends FragmentActivity implements OnMapReadyCallback {
    private String userId;
    private String lat, lng = null;
    private Map maps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getuser_map_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        maps = new Map();
        userId = String.valueOf(SharedPrefManager.getInstance(this).getUser().getId());
        getMapDetails();
    }

    /*
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    private void getMapDetails() {
        MapApi mService = ApiClient.getClient().create(MapApi.class);
        Call<Result> call = mService.getMapDetails(Integer.parseInt(userId));
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (response.code() == 200 && response.body() != null && response.body().getMaps() != null) {
                    try{
                        maps = response.body().getMaps();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(GetUserMapLocation.this,"Failed due to: "+response.errorBody(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(GetUserMapLocation.this,R.string.error +t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap mMap) {

        //work with different map types
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.clear(); //clear old markers

        CameraPosition googlePlex = CameraPosition.builder()
                .target(new LatLng(25.1413,55.1853))
                .zoom(2)
                .bearing(0)
                .tilt(45)
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 10000, null);

        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(27.1750, 78.0422))
                .title("Taj Mahal")
                .snippet("It is located in India")
                .rotation((float) 3.5)
                .icon(bitmapDescriptorFromVector(getApplicationContext(),R.drawable.ic_account_circle_black_24dp)));

        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(48.8584, 2.2945))
                .title("Eiffel Tower")
                .snippet("It is located in France")
                .rotation((float) 33.5)
                .icon(bitmapDescriptorFromVector(getApplicationContext(),R.drawable.ic_account_circle_black_24dp)));

        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(25.1413, 55.1853))
                .title("burj al arab")
                .snippet("It is located in Dubai")
                .rotation((float) 93.5)
                .icon(bitmapDescriptorFromVector(getApplicationContext(),R.drawable.ic_account_circle_black_24dp)));

        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(38.9637, 35.2433))
                .title("Turkey")
                .snippet("It is located in Turkey")
                .rotation((float) 33.5)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}