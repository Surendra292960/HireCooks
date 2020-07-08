package com.test.sample.hirecooks.Models.NewProductsCategory;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class NewProductCategory implements Serializable {

@SerializedName("id")
@Expose
private Integer id;
@SerializedName("categoryid")
@Expose
private Integer categoryid;
@SerializedName("category_name")
@Expose
private String categoryName;
@SerializedName("link")
@Expose
private String link;

public Integer getId() {
return id;
}

public void setId(Integer id) {
this.id = id;
}

public Integer getCategoryid() {
return categoryid;
}

public void setCategoryid(Integer categoryid) {
this.categoryid = categoryid;
}

public String getCategoryName() {
return categoryName;
}

public void setCategoryName(String categoryName) {
this.categoryName = categoryName;
}

public String getLink() {
return link;
}

public void setLink(String link) {
this.link = link;
}

}