package com.test.sample.hirecooks.Models.MapLocationResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Map implements Serializable {

    @SerializedName("mapId")
    @Expose
    private Integer mapId;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("sub_address")
    @Expose
    private String subAddress;
    @SerializedName("pincode")
    @Expose
    private Integer pincode;
    @SerializedName("placeId")
    @Expose
    private String placeId;
    @SerializedName("userId")
    @Expose
    private Integer userId;
    @SerializedName("firm_id")
    @Expose
    private String firmId;
    @SerializedName("houseNumber")
    @Expose
    private String houseNumber;
    @SerializedName("floor")
    @Expose
    private String floor;
    @SerializedName("landmark")
    @Expose
    private String landmark;
    @SerializedName("location_type")
    @Expose
    private String locationType;

    public Integer getMapId() {
        return mapId;
    }

    public void setMapId(Integer mapId) {
        this.mapId = mapId;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSubAddress() {
        return subAddress;
    }

    public void setSubAddress(String subAddress) {
        this.subAddress = subAddress;
    }

    public Integer getPincode() {
        return pincode;
    }

    public void setPincode(Integer pincode) {
        this.pincode = pincode;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getFirm_id() {
        return firmId;
    }

    public void setFirm_id(String firmId) {
        this.firmId = firmId;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getLocationType() {
        return locationType;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

}