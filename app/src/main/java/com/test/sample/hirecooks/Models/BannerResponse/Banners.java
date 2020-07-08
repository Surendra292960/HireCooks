package com.test.sample.hirecooks.Models.BannerResponse;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class Banners implements Serializable {

    @SerializedName("banner")
    @Expose
    private List<Banner> banner = null;

    public List<Banner> getBanners() {
        return banner;
    }

    public void setBanners(List<Banner> banner) {
        this.banner = banner;
    }
}
