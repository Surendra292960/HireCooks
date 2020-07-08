package com.test.sample.hirecooks.Models.NewProductsSubCategory;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class NewProductSubcategories implements Serializable {

    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("new_product_subcategory")
    @Expose
    private List<NewProductSubcategory> newProductSubcategory = null;

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

    public List<NewProductSubcategory> getNewProductSubcategory() {
        return newProductSubcategory;
    }

    public void setNewProductSubcategory(List<NewProductSubcategory> newProductSubcategory) {
        this.newProductSubcategory = newProductSubcategory;
    }
}
