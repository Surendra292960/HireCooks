package com.test.sample.hirecooks.Models.Images;
import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Images implements Serializable {

    @SerializedName("images")
    @Expose
    private List<Image> images = null;


    public Images() {
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

}