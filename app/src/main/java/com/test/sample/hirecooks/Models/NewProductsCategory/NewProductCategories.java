package com.test.sample.hirecooks.Models.NewProductsCategory;
import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NewProductCategories implements Serializable {

@SerializedName("new_product_category")
@Expose
private List<NewProductCategory> newProductCategory = null;

public List<NewProductCategory> getNewProductCategory() {
return newProductCategory;
}

public void setNewProductCategory(List<NewProductCategory> newProductCategory) {
this.newProductCategory = newProductCategory;
}

}