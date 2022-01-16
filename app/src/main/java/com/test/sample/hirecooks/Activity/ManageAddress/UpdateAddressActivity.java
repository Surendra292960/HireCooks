package com.test.sample.hirecooks.Activity.ManageAddress;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.textfield.TextInputEditText;
import com.test.sample.hirecooks.Models.MapLocationResponse.Map;
import com.test.sample.hirecooks.Models.MapLocationResponse.Maps;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.BaseActivity;
import com.test.sample.hirecooks.Utils.Common;
import com.test.sample.hirecooks.Utils.ProgressBarUtil;
import com.test.sample.hirecooks.WebApis.MapApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class UpdateAddressActivity extends BaseActivity {
    private TextInputEditText mAddress, mSubAddress, mPincode,mhouse_number,mfloor,mlandmark,mlocation_tag;
    private Map address;
    private ProgressBarUtil progressBarUtil;
    private MapApi mService = Common.getAPI();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_secondry_address);
        initViews();
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
           address = (Map) getIntent().getSerializableExtra("address_book");
           if(address!=null){
               loadAddress(address);
           }
        }
    }

    private void initViews() {
        progressBarUtil = new ProgressBarUtil( this );
        mAddress = findViewById(R.id.address);
        mSubAddress = findViewById(R.id.subAddress);
        mPincode = findViewById(R.id.pincode);
        mhouse_number = findViewById(R.id.house_number);
        mfloor = findViewById(R.id.floor);
        mlandmark = findViewById(R.id.landmark);
        mlocation_tag = findViewById(R.id.location_tag);

        findViewById(R.id.button_update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
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

    private void loadAddress( Map address) {
        mAddress.setText(address.getAddress());
        mSubAddress.setText(address.getSubAddress());
        mPincode.setText(String.valueOf( address.getPincode() ));
        mhouse_number.setText(String.valueOf(address.getHouseNumber()));
        mfloor.setText(String.valueOf(address.getFloor()));
        mlandmark.setText(String.valueOf(address.getLandmark()));
        mlocation_tag.setText(String.valueOf(address.getLocationType()));
    }

    private void validateData() {
        final String sAddress = mAddress.getText().toString().trim();
        final String sSubAddress = mSubAddress.getText().toString().trim();
        final String sPincode = mPincode.getText().toString().trim();
        final String sHouseNumber = mhouse_number.getText().toString().trim();
        final String sFloor = mfloor.getText().toString().trim();
        final String sLandMark = mlandmark.getText().toString().trim();
        final String sLocation_tag = mlocation_tag.getText().toString().trim();

        if (sAddress.isEmpty()) {
            mAddress.setError( "Address required" );
            mAddress.requestFocus();
            return;
        }if (sSubAddress.isEmpty()) {
            mSubAddress.setError( "SubAddress required" );
            mSubAddress.requestFocus();
            return;
        }if (sPincode.isEmpty()) {
            mPincode.setError( "Pincode required" );
            mPincode.requestFocus();
            return;
        }if (sHouseNumber.isEmpty()) {
            mhouse_number.setError( "HouseNumber required" );
            mhouse_number.requestFocus();
            return;
        }if (sFloor.isEmpty()) {
            mfloor.setError( "Floor required" );
            mfloor.requestFocus();
            return;
        }if (sLandMark.isEmpty()) {
            mlandmark.setError( "LandMark required" );
            mlandmark.requestFocus();
            return;
        }if (sLocation_tag.isEmpty()) {
            mlocation_tag.setError( "Location Tag required" );
            mlocation_tag.requestFocus();
            return;
        }

        setAddressData();
    }

    private void setAddressData() {
        Map map = new Map();
        map.setMapId( address.getMapId() );
        map.setLatitude( address.getLatitude() );
        map.setLongitude( address.getLongitude() );
        map.setAddress( mAddress.getText().toString() );
        map.setSubAddress( mSubAddress.getText().toString() );
        map.setPincode(Integer.parseInt( mPincode.getText().toString() ));
        map.setPlaceId( address.getPlaceId() );
        map.setUserId( address.getUserId() );
        map.setFirm_id( address.getFirm_id() );
        map.setHouseNumber( mhouse_number.getText().toString() );
        map.setFloor( mfloor.getText().toString() );
        map.setLandmark( mlandmark.getText().toString() );
        map.setLocationType( mlocation_tag.getText().toString() );

        List<Maps> mapsList = new ArrayList<>();
        List<Map> mapList = new ArrayList<>();
        Maps maps = new Maps();
        mapList.add(map);
        maps.setMaps(mapList);
        mapsList.add(maps);
        updateAddress(map.getMapId(),mapsList);
    }

    private void updateAddress(Integer mapId, List<Maps> mapsList) {
        progressBarUtil.showProgress();
        mService.updateAddress(mapId, mapsList)
                .subscribeOn( Schedulers.io())
                .observeOn( AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(new Observer<List<Maps>>() {

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull List<Maps> result) {
                        for(Maps maps:result){
                            ShowToast( maps.getMessage() );
                            if(!maps.getError()){
                                UpdateAddressActivity.this.finish();
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable t) {
                        progressBarUtil.hideProgress();
                        ShowToast(t.getMessage());
                        System.out.println("New data received: " + t.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        progressBarUtil.hideProgress();
                    }
                });
    }

    private void deleteAddress(Map address) {
        progressBarUtil.showProgress();
        mService.deleteAddress(address.getMapId())
                .subscribeOn( Schedulers.io())
                .observeOn( AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(new Observer<List<Maps>>() {

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<Maps> result) {
                        progressBarUtil.hideProgress();
                        for(Maps maps:result){
                            ShowToast( maps.getMessage() );
                            if(!maps.getError()){
                                UpdateAddressActivity.this.finish();
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        progressBarUtil.hideProgress();
                        ShowToast(t.getMessage());
                        System.out.println("New data received: " + t.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        progressBarUtil.hideProgress();
                    }
                });
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