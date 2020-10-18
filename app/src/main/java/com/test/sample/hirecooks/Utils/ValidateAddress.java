package com.test.sample.hirecooks.Utils;
import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;
import com.test.sample.hirecooks.Models.MapLocationResponse.Map;

class ValidateAddress extends ValidationCallBack {

    private final Context context;

    ValidateAddress(Context context){
        this.context = context;
    }
    @Override
    public void addressValidation(Map map) {
        if (TextUtils.isEmpty( map.getAddress() )) {
           Toast.makeText( context,"fill Address",Toast.LENGTH_LONG ).show();
            return;
        }if (TextUtils.isEmpty( map.getLatitude() )) {
           Toast.makeText( context,"fill Lattitude",Toast.LENGTH_LONG ).show();
            return;
        }if (TextUtils.isEmpty( map.getLongitude() )) {
            Toast.makeText( context,"fill Longitude",Toast.LENGTH_LONG ).show();
            return;
        }if (TextUtils.isEmpty( map.getPlaceId() )) {
           Toast.makeText( context,"fill Lattitude",Toast.LENGTH_LONG ).show();
            return;
        }if (map.getPincode()==0) {
           Toast.makeText( context,"fill Pincode",Toast.LENGTH_LONG ).show();
            return;
        }if (TextUtils.isEmpty( map.getSubAddress() )) {
           Toast.makeText( context,"fill SubAddress",Toast.LENGTH_LONG ).show();
            return;
        }if (map.getUserId() == 0) {
           Toast.makeText( context,"fill UserId",Toast.LENGTH_LONG ).show();
            return;
        }if (TextUtils.isEmpty( map.getFirm_id() )) {
           Toast.makeText( context,"fill FirmId",Toast.LENGTH_LONG ).show();
            return;
        }if (TextUtils.isEmpty( map.getHouseNumber() )) {
           Toast.makeText( context,"fill houseNumber",Toast.LENGTH_LONG ).show();
            return;
        }if (TextUtils.isEmpty( map.getFloor() )) {
           Toast.makeText( context,"fill Floor",Toast.LENGTH_LONG ).show();
            return;
        }if (TextUtils.isEmpty( map.getLandmark() )) {
           Toast.makeText( context,"fill Landmark",Toast.LENGTH_LONG ).show();
            return;
        }if (TextUtils.isEmpty( map.getLocationType() )) {
           Toast.makeText( context,"fill LocationTag",Toast.LENGTH_LONG ).show();
            return;
        }
    }
}