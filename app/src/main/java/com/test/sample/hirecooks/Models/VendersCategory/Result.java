package com.test.sample.hirecooks.Models.VendersCategory;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Result implements Serializable {
    @SerializedName("venders_category")
    @Expose
    private List<VendersCategory> vendersCategory = null;

    public List<VendersCategory> getVendersCategory() {
        return vendersCategory;
    }

    public void setVendersCategory(List<VendersCategory> vendersCategory) {
        this.vendersCategory = vendersCategory;
    }

}

