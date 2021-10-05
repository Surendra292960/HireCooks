package com.test.sample.hirecooks.Activity.ProductDatails;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.test.sample.hirecooks.Activity.Home.MainActivity;
import com.test.sample.hirecooks.Activity.Users.SignupAddressActivity;
import com.test.sample.hirecooks.Models.MapLocationResponse.Map;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.Constants;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class BottomSheetDialog extends BottomSheetDialogFragment {
	public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
	private FusedLocationProviderClient client;
	private GoogleSignInAccount account;
	private DetailsActivity mCtx;
	private String pinCode,subAddress;
	private LatLng latLng;
	private TextInputEditText editTextPinCode;
	private ImageView cancel;

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.enter_pincode_layout, container, false);
		v.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.slide_up));
		client = LocationServices.getFusedLocationProviderClient(mCtx);
		account = GoogleSignIn.getLastSignedInAccount(mCtx);
		cancel = v.findViewById(R.id.cancel);
		editTextPinCode = v.findViewById(R.id.editTextPinCode);
		editTextPinCode.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getCurrentLocation();
			}
		});

		cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dismiss();
			}
		});
		return v;
	}

	public void getCurrentLocation() {
		if ( ActivityCompat.checkSelfPermission ( mCtx , android.Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED
				&& ActivityCompat.checkSelfPermission ( mCtx , android.Manifest.permission.ACCESS_COARSE_LOCATION ) == PackageManager.PERMISSION_GRANTED ) {
			//request the last location and add a listener to get the response. then update the UI.
			client.getLastLocation ( ).addOnSuccessListener ( mCtx , location -> {
				// Got last known location.
				if ( location != null ) {
					setLocation (location);
				} else {
					Toast.makeText ( mCtx , "location: IS NULL" , Toast.LENGTH_SHORT ).show ( );
				}
			} ).addOnFailureListener ( mCtx , new OnFailureListener( ) {
				@Override
				public void onFailure ( @NonNull Exception e ) {
					Toast.makeText ( mCtx , "getCurrentLocation FAILED" , Toast.LENGTH_LONG ).show ( );
				}
			} );
		} else {//client
			Toast.makeText ( mCtx , "getCurrentLocation ERROR" , Toast.LENGTH_LONG ).show ( );
		}
	}


	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		switch (requestCode) {
			case MY_PERMISSIONS_REQUEST_LOCATION:
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					// permission was granted
					getCurrentLocation();
				} else {
					// permission denied
                    /*TextView locationText = findViewById(R.id.locationText);
                    locationText.setText("location: permission denied");*/

					new AlertDialog.Builder(mCtx)
							.setMessage("Cannot get the location!")
							.setPositiveButton("OK", null)
							.setNegativeButton("Cancel", null)
							.create()
							.show();
				}
		}
	}

	private void setLocation(Location location) {
		latLng = new LatLng( location.getLatitude(), location.getLongitude() );
		getAddress( latLng );
	}

	private String getAddress( LatLng latLng) {
		Geocoder geocoder = new Geocoder(mCtx, Locale.getDefault());
		String result = null;
		try {
			List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 10);
			if (addressList != null && addressList.size() > 0) {
				Address address = addressList.get(0);
				pinCode = address.getPostalCode();
				editTextPinCode.setText(""+pinCode);
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
					sb.append(address.getAddressLine(i)).append(",");
				}
				result = address.getAddressLine(0);
				subAddress = address.getSubLocality()+", "+address.getLocality();
				return result;
			}
			return result;
		} catch ( IOException e) {
			Log.e("MapLocation", "Unable connect to Geocoder", e);
			return result;
		}
	}

	@Override
	public void onAttach(@NonNull Context context) {
		super.onAttach(context);
		mCtx = (DetailsActivity)context;
	}
}
