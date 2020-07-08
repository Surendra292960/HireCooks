package com.test.sample.hirecooks.Models.cooks;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Cooks implements Serializable {

    @SerializedName("cook")
    private ArrayList<Cook> cooks;

    public Cooks() {

    }

    public ArrayList<Cook> getCooks() {
        return cooks;
    }

    public void setCooks(ArrayList<Cook> cooks) {
        this.cooks = cooks;
    }
}
