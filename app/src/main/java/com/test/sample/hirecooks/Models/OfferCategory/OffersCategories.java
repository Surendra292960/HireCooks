package com.test.sample.hirecooks.Models.OfferCategory;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class OffersCategories implements Serializable {

    @SerializedName("offers_category")
    @Expose
    private List<OffersCategory> offersCategory = null;

    public List<OffersCategory> getOffersCategory() {
        return offersCategory;
    }

    public void setOffersCategory(List<OffersCategory> offersCategory) {
        this.offersCategory = offersCategory;
    }
}
