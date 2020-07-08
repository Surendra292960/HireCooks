package com.test.sample.hirecooks.Models.Category;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Category implements Serializable {

@SerializedName("categoryid")
@Expose
private Integer categoryid;
@SerializedName("name")
@Expose
private String name;
@SerializedName("link")
@Expose
private String link;
@SerializedName("discount")
@Expose
private String discount;
@SerializedName("category")
@Expose
private String category;
@SerializedName("categoryName")
@Expose
private String categoryName;

    public Category(Integer categoryid, String name, String link, String discount, String category, String categoryName) {
        this.categoryid = categoryid;
        this.name = name;
        this.link = link;
        this.discount = discount;
        this.category = category;
        this.categoryName = categoryName;
    }

    public Integer getCategoryid() {
return categoryid;
}

public void setCategoryid(Integer categoryid) {
this.categoryid = categoryid;
}

public String getName() {
return name;
}

public void setName(String name) {
this.name = name;
}

public String getLink() {
return link;
}

public void setLink(String link) {
this.link = link;
}

public String getDiscount() {
return discount;
}

public void setDiscount(String discount) {
this.discount = discount;
}

public String getCategory() {
        return category;
    }

public void setCategory(String category) {
        this.category = category;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}