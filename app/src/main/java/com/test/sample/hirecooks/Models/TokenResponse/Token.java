package com.test.sample.hirecooks.Models.TokenResponse;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Token implements Serializable {

    @SerializedName("tokenId")
    @Expose
    private Integer tokenId;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("deviceId")
    @Expose
    private String deviceId;
    @SerializedName("isServerToken")
    @Expose
    private Integer isServerToken;
    @SerializedName("userId")
    @Expose
    private Integer userId;
    @SerializedName("firm_id")
    @Expose
    private String firm_id;

    public Token(Integer tokenId, String token, String deviceId) {
        this.tokenId = tokenId;
        this.token = token;
        this.deviceId = deviceId;
    }

    public Integer getTokenId() {
        return tokenId;
    }

    public void setTokenId(Integer tokenId) {
        this.tokenId = tokenId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Integer getIsServerToken() {
        return isServerToken;
    }

    public void setIsServerToken(Integer isServerToken) {
        this.isServerToken = isServerToken;
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
}