package com.test.sample.hirecooks.Models.cooks.Request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class CooksImagesResult implements Serializable {
    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("images")
    @Expose
    private List<CooksImages> images = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<CooksImages> getImages() {
        return images;
    }

    public void setImages(List<CooksImages> images) {
        this.images = images;
    }
}
