package com.test.sample.hirecooks.Activity.ManageAddress;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.RoomDatabase.SecondryAddress_DB.Address;
import com.test.sample.hirecooks.RoomDatabase.SecondryAddress_DB.AddressDataBaseClient;

import java.util.Objects;

public class UpdateAddressActivity extends AppCompatActivity {
    private EditText mAddress, mSubAddress, mPincode,mhouse_number,mfloor,mlandmark,mlocation_tag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Update Address");
        setContentView(R.layout.activity_update_secondry_address);
        mAddress = findViewById(R.id.address);
        mSubAddress = findViewById(R.id.subAddress);
        mPincode = findViewById(R.id.pincode);
        mhouse_number = findViewById(R.id.house_number);
        mfloor = findViewById(R.id.floor);
        mlandmark = findViewById(R.id.landmark);
        mlocation_tag = findViewById(R.id.location_tag);
        final Address address = (Address) getIntent().getSerializableExtra("address");

        loadAddress(address);

        findViewById(R.id.button_update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Clicked", Toast.LENGTH_LONG).show();
                updateAddress(address);
            }
        });

        findViewById(R.id.button_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateAddressActivity.this);
                builder.setTitle("Are you sure?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteAddress(address);
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                AlertDialog ad = builder.create();
                ad.show();
            }
        });
    }

    private void loadAddress( Address address) {
        mAddress.setText(address.getAddress());
        mSubAddress.setText(address.getSub_address());
        mPincode.setText(""+address.getPincode());
        mhouse_number.setText(""+address.getHouse_number());
        mfloor.setText(""+address.getFloor());
        mlandmark.setText(""+address.getLandmark());
        mlocation_tag.setText(""+address.getLocation_tag());
    }

    private void updateAddress(final Address address) {
        final String sAddress = mAddress.getText().toString().trim();
        final String sSubAddress = mSubAddress.getText().toString().trim();
        final String sPincode = mPincode.getText().toString().trim();
        final String sHouseNumber = mhouse_number.getText().toString().trim();
        final String sFloor = mfloor.getText().toString().trim();
        final String sLandMark = mlandmark.getText().toString().trim();
        final String sLocation_tag = mlocation_tag.getText().toString().trim();

        if (sAddress.isEmpty()) {
            mAddress.setError("Address required");
            mAddress.requestFocus();
            return;
        }else if (sSubAddress.isEmpty()) {
            mSubAddress.setError("SubAddress required");
            mSubAddress.requestFocus();
            return;
        }else if (sPincode.isEmpty()) {
            mPincode.setError("Pincode required");
            mPincode.requestFocus();
            return;
        }else if (sHouseNumber.isEmpty()) {
            mhouse_number.setError("HouseNumber required");
            mhouse_number.requestFocus();
            return;
        }else if (sFloor.isEmpty()) {
            mfloor.setError("Floor required");
            mfloor.requestFocus();
            return;
        }else if (sLandMark.isEmpty()) {
            mlandmark.setError("LandMark required");
            mlandmark.requestFocus();
            return;
        }else if (sLocation_tag.isEmpty()) {
            mlocation_tag.setError("Location Tag required");
            mlocation_tag.requestFocus();
            return;
        }

        class UpdateAddress extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                address.setAddress(sAddress);
                address.setSub_address(sSubAddress);
                address.setPincode(Integer.parseInt(sPincode));
                address.setHouse_number(sHouseNumber);
                address.setFloor(sFloor);
                address.setLandmark(sLandMark);
                address.setLocation_tag(sLocation_tag);
                AddressDataBaseClient.getInstance(getApplicationContext()).getAddressDatabase().addressDao().update(address);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_LONG).show();
                UpdateAddressActivity.this.finish();
                startActivity(new Intent(UpdateAddressActivity.this, SecondryAddressActivity.class));
            }
        }

        UpdateAddress ut = new UpdateAddress();
        ut.execute();
    }

    private void deleteAddress(final Address address) {
        class DeleteAddress extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                AddressDataBaseClient.getInstance(getApplicationContext()).getAddressDatabase().addressDao().delete(address);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_LONG).show();
                UpdateAddressActivity.this.finish();
                startActivity(new Intent(UpdateAddressActivity.this, SecondryAddressActivity.class));
            }
        }

        DeleteAddress dt = new DeleteAddress();
        dt.execute();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}