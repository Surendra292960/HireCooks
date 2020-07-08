package com.test.sample.hirecooks.Models.SubCategory.Response;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class SubCategories implements Serializable {
    @SerializedName("subcategory")
    @Expose
    private List<SubCategory> subcategory = null;

    public List<SubCategory> getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(List<SubCategory> subcategory) {
        this.subcategory = subcategory;
    }
}
