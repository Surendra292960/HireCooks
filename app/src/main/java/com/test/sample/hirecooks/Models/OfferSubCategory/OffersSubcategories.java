package com.test.sample.hirecooks.Models.OfferSubCategory;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class OffersSubcategories implements Serializable {
    @SerializedName("offers_subcategory")
    @Expose
    private List<OffersSubcategory> offersSubcategory = null;

    public List<OffersSubcategory> getOffersSubcategory() {
        return offersSubcategory;
    }

    public void setOffersSubcategory(List<OffersSubcategory> offersSubcategory) {
        this.offersSubcategory = offersSubcategory;
    }
}
