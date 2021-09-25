package com.test.sample.hirecooks.Models.FirmUsers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Firmuser implements Serializable {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("userType")
    @Expose
    private String userType;
    @SerializedName("signin_lat")
    @Expose
    private String signinLat;
    @SerializedName("signin_lng")
    @Expose
    private String signinLng;
    @SerializedName("signout_lat")
    @Expose
    private String signoutLat;
    @SerializedName("signout_lng")
    @Expose
    private String signoutLng;
    @SerializedName("signin_date")
    @Expose
    private String signinDate;
    @SerializedName("signout_date")
    @Expose
    private String signoutDate;
    @SerializedName("signin_address")
    @Expose
    private String signinAddress;
    @SerializedName("signout_address")
    @Expose
    private String signoutAddress;
    @SerializedName("firmId")
    @Expose
    private String firmId;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("created_at")
    @Expose
    private String createdAt;


    public Firmuser() {

    }

    public Firmuser(int id, String name, String userType, String signinLat, String signinLng, String signoutLat, String signoutLng, String signinDate, String signoutDate, String signinAddress, String signoutAddress, String firmId, String status, String userId, String createdAt) {
        this.id = id;
        this.name = name;
        this.userType = userType;
        this.signinLat = signinLat;
        this.signinLng = signinLng;
        this.signoutLat = signoutLat;
        this.signoutLng = signoutLng;
        this.signinDate = signinDate;
        this.signoutDate = signoutDate;
        this.signinAddress = signinAddress;
        this.signoutAddress = signoutAddress;
        this.firmId = firmId;
        this.status = status;
        this.userId = userId;
        this.createdAt = createdAt;
    }

    public Firmuser(String name, String userType) {
        this.name = name;
        this.userType = userType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getSigninLat() {
        return signinLat;
    }

    public void setSigninLat(String signinLat) {
        this.signinLat = signinLat;
    }

    public String getSigninLng() {
        return signinLng;
    }

    public void setSigninLng(String signinLng) {
        this.signinLng = signinLng;
    }

    public String getSignoutLat() {
        return signoutLat;
    }

    public void setSignoutLat(String signoutLat) {
        this.signoutLat = signoutLat;
    }

    public String getSignoutLng() {
        return signoutLng;
    }

    public void setSignoutLng(String signoutLng) {
        this.signoutLng = signoutLng;
    }

    public String getSigninDate() {
        return signinDate;
    }

    public void setSigninDate(String signinDate) {
        this.signinDate = signinDate;
    }

    public String getSignoutDate() {
        return signoutDate;
    }

    public void setSignoutDate(String signoutDate) {
        this.signoutDate = signoutDate;
    }

    public String getSigninAddress() {
        return signinAddress;
    }

    public void setSigninAddress(String signinAddress) {
        this.signinAddress = signinAddress;
    }

    public String getSignoutAddress() {
        return signoutAddress;
    }

    public void setSignoutAddress(String signoutAddress) {
        this.signoutAddress = signoutAddress;
    }

    public String getFirmId() {
        return firmId;
    }

    public void setFirmId(String firmId) {
        this.firmId = firmId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

}