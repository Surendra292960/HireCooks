package com.test.sample.hirecooks.Models.MapLocationResponse;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MapResponse implements Serializable {
    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("maps")
    @Expose
    private Map maps;

    public MapResponse(Boolean error, String message, Map maps) {
        this.error = error;
        this.message = message;
        this.maps = maps;
    }

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

    public Map getMaps() {
        return maps;
    }
}
