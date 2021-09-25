package com.test.sample.hirecooks;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.test.sample.hirecooks.Activity.Users.PhoneVerification;

import java.util.Objects;

public class EmailVerification extends AppCompatActivity implements View.OnClickListener {
    private TextInputEditText editTextEmail;
    private AppCompatButton refreshBtn, verifyBtn,buttonSignIn;
    private TextView status, verifyEmail, userId;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_and_phone_varification);
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextEmail = findViewById(R.id.editTextEmail);
        verifyEmail = findViewById(R.id.verifyEmail);
        userId = findViewById(R.id.userId);
        status = findViewById(R.id.status);
        buttonSignIn = findViewById(R.id.buttonSignIn);
        buttonSignIn.setOnClickListener(this);
        buttonSignIn.setVisibility(View.GONE);
        refreshBtn = findViewById(R.id.refreshBtn);
        verifyBtn = findViewById(R.id.verifyBtn);
        verifyBtn.setOnClickListener(this);

        // editTextEmail.setText( firebaseUser.getEmail() );
        setInfo();

    }


    @Override
    public void onClick(View view) {
        if (view == verifyBtn) {
            SignInValidation();
        }else if(view== buttonSignIn){
            SignInValidation();
        }
    }

    private void SignInValidation() {
        String email = Objects.requireNonNull(editTextEmail.getText()).toString().trim();

        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Please enter email");
            editTextEmail.requestFocus();
            return;
        }

        if(firebaseUser!=null&&firebaseUser.isEmailVerified()){
         //   ResultSignIn(email,password);
        }else{
            emailPasswordValidation(email,"123");
        }
    }

    private void emailPasswordValidation(final String email, final String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Objects.requireNonNull(mAuth.getCurrentUser()).sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Please Check your Email Address", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseUser.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        setInfo();
                    }
                });
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void setInfo() {

        if(firebaseUser!=null&& firebaseUser.isEmailVerified()){
            verifyEmail.setText(new StringBuilder("EMAIL: ").append(firebaseUser.getEmail()));
            userId.setText(new StringBuilder("UID: ").append(firebaseUser.getUid()));
            status.setText(new StringBuilder("Status: ").append(firebaseUser.isEmailVerified()));
            buttonSignIn.setVisibility(View.VISIBLE);
            startActivity(new Intent(this, PhoneVerification.class));
        }else{
            Toast.makeText(getApplicationContext(), "Please try after some time", Toast.LENGTH_SHORT).show();
            verifyEmail.setText("EMAIL: "+"Non");
            userId.setText("UID: "+" Non");
            status.setText("Status: " +"false");
        }
    }

  /*  private void ResultSignIn(String email, String password) {
        progressBarUtil.showProgress();
        UserApi service = ApiClient.getClient().create(UserApi.class);
        Call<Results> call = service.userLogin(email, password);
        call.enqueue(new Callback<Results>() {
            @Override
            public void onResponse(Call<Results> call, Response<Results> response) {
                int statusCode = response.code();
                if(statusCode==200){
                    progressBarUtil.hideProgress();
                    assert response.body() != null;
                    if (!response.body().getError()) {
                      //  SharedPrefManager.getInstance(getApplicationContext()).userLogin(response.body().getUser());
                        startActivity(new Intent(getApplicationContext(), VideoChat.class));
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Invalid email or password", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Results> call, Throwable t) {
                progressBarUtil.hideProgress();
                Toast.makeText(getApplicationContext(), "Error ", Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }*/
}

/*

package com.test.sample.hirecooks.Activity.TrackUser;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.test.sample.hirecooks.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class RiderMapLocation extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mGoogleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_map_location);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.mapNearBy);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        List<LatLng> sourcePoints = new ArrayList<>();
        sourcePoints.add(new LatLng(-35.27801,149.12958));
        sourcePoints.add(new LatLng(-35.28032,149.12907));
        sourcePoints.add(new LatLng(-35.28099,149.12929));
        sourcePoints.add(new LatLng(-35.28144,149.12984));
        sourcePoints.add(new LatLng(-35.28194,149.13003));
        sourcePoints.add(new LatLng(-35.28282,149.12956));
        sourcePoints.add(new LatLng(-35.28302,149.12881));
        sourcePoints.add(new LatLng(-35.28473,149.12836));

        PolylineOptions polyLineOptions = new PolylineOptions();
        polyLineOptions.addAll(sourcePoints);
        polyLineOptions.width(5);
        polyLineOptions.color(Color.BLUE);
        mGoogleMap.addPolyline(polyLineOptions);

        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sourcePoints.get(0), 15));

        List<LatLng> snappedPoints = new ArrayList<>();
        new GetSnappedPointsAsyncTask().execute(sourcePoints, null, snappedPoints);
    }


    private String buildRequestUrl(List<LatLng> trackPoints) {
        StringBuilder url = new StringBuilder();
        url.append("https://roads.googleapis.com/v1/snapToRoads?path=");

        for (LatLng trackPoint : trackPoints) {
            url.append(String.format("%8.5f", trackPoint.latitude));
            url.append(",");
            url.append(String.format("%8.5f", trackPoint.longitude));
            url.append("|");
        }
        url.delete(url.length() - 1, url.length());
        url.append("&interpolate=true");
        url.append(String.format("&key=%s", "AIzaSyBm_OQWOR7nRG7uPjRgtkeXwHSjWIbjmz4"));

        return url.toString();
    }


    private class GetSnappedPointsAsyncTask extends AsyncTask<List<LatLng>, Void, List<LatLng>> {

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected List<LatLng> doInBackground(List<LatLng>... params) {

            List<LatLng> snappedPoints = new ArrayList<>();

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(buildRequestUrl(params[0]));
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder jsonStringBuilder = new StringBuilder();

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                    jsonStringBuilder.append(line);
                    jsonStringBuilder.append("\n");
                }

                JSONObject jsonObject = new JSONObject(jsonStringBuilder.toString());
                JSONArray snappedPointsArr = jsonObject.getJSONArray("snappedPoints");

                for (int i = 0; i < snappedPointsArr.length(); i++) {
                    JSONObject snappedPointLocation = ((JSONObject) (snappedPointsArr.get(i))).getJSONObject("location");
                    double lattitude = snappedPointLocation.getDouble("latitude");
                    double longitude = snappedPointLocation.getDouble("longitude");
                    snappedPoints.add(new LatLng(lattitude, longitude));
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return snappedPoints;
        }

        @Override
        protected void onPostExecute(List<LatLng> result) {
            super.onPostExecute(result);

            PolylineOptions polyLineOptions = new PolylineOptions();
            polyLineOptions.addAll(result);
            polyLineOptions.width(5);
            polyLineOptions.color( Color.RED);
            mGoogleMap.addPolyline(polyLineOptions);

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(result.get(0));
            builder.include(result.get(result.size()-1));
            LatLngBounds bounds = builder.build();
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10));

        }
    }
*/
