package com.test.sample.hirecooks.RoomDatabase.SecondryAddress_DB;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Address implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "latitude")
    private String latitude;

    @ColumnInfo(name = "longitude")
    private String longitude;

    @ColumnInfo(name = "address_book")
    private String address;

    @ColumnInfo(name = "sub_address")
    private String sub_address;

    @ColumnInfo(name = "pincode")
    private Integer pincode;

    @ColumnInfo(name = "placeId")
    private String placeId;

    @ColumnInfo(name = "userId")
    private Integer userId;

    @ColumnInfo(name = "firm_id")
    private String firm_id;

    @ColumnInfo(name = "house_number")
    private String house_number;

    @ColumnInfo(name = "floor")
    private String floor;

    @ColumnInfo(name = "landmark")
    private String landmark;

    @ColumnInfo(name = "location_tag")
    private String location_tag;

    /*
     * Getters and Setters
     * */

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getSub_address() {
        return sub_address;
    }

    public void setSub_address(String sub_address) {
        this.sub_address = sub_address;
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
        return firm_id;
    }

    public void setFirm_id(String firm_id) {
        this.firm_id = firm_id;
    }

    public String getHouse_number() {
        return house_number;
    }

    public void setHouse_number(String house_number) {
        this.house_number = house_number;
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

    public String getLocation_tag() {
        return location_tag;
    }

    public void setLocation_tag(String location_tag) {
        this.location_tag = location_tag;
    }
}