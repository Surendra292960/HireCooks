package com.test.sample.hirecooks.Models.FirmUsers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;
public class Example  implements Serializable {

    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("firmusers")
    @Expose
    private List<Firmuser> firmusers = null;

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

    public List<Firmuser> getFirmusers() {
        return firmusers;
    }

    public void setFirmusers(List<Firmuser> firmusers) {
        this.firmusers = firmusers;
    }

}
