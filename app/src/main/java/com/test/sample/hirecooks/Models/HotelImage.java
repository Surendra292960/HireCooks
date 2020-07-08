package com.test.sample.hirecooks.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Comparator;

public class HotelImage {
    public static Comparator compareHotelImageByOrder = new Comparator ( ) {
        @Override
        public int compare ( Object o , Object t1 ) {
            HotelImage profile = ( HotelImage ) o;
            HotelImage profile1 = ( HotelImage ) t1;
            return profile.getImageOrder ( ) - profile1.getImageOrder ( );
        }
    };

    @SerializedName("userImageId")
    @Expose
    private Integer userImageId;
    @SerializedName("users")
    @Expose
    private String users;
    @SerializedName("images")
    @Expose
    private String images;
    @SerializedName("Caption")
    @Expose
    private Integer caption;
    @SerializedName("imageOrder")
    @Expose
    private Integer imageOrder;
    @SerializedName("userId")
    @Expose
    private Integer userId;

    public Integer getUserImageId() {
        return userImageId;
    }

    public void setUserImageId(Integer userImageId) {
        this.userImageId = userImageId;
    }

    public String getUsers() {
        return users;
    }

    public void setUsers(String users) {
        this.users = users;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public Integer getCaption() {
        return caption;
    }

    public void setCaption(Integer caption) {
        this.caption = caption;
    }

    public Integer getImageOrder() {
        return imageOrder;
    }

    public void setImageOrder(Integer imageOrder) {
        this.imageOrder = imageOrder;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

}