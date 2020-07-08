package com.test.sample.hirecooks.Models.VendersSubCategory;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class Result implements Serializable {
    @SerializedName("venders_subcategory")
    @Expose
    private List<VendersSubcategory> vendersSubcategory = null;

    public List<VendersSubcategory> getVendersSubcategory() {
        return vendersSubcategory;
    }

    public void setVendersSubcategory(List<VendersSubcategory> vendersSubcategory) {
        this.vendersSubcategory = vendersSubcategory;
    }

}
