package com.test.sample.hirecooks.Models.VendersCategory;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class VendersCategory implements Serializable {

@SerializedName("id")
@Expose
private Integer id;
@SerializedName("categoryid")
@Expose
private Integer categoryid;
@SerializedName("link")
@Expose
private String link;
@SerializedName("categoryName")
@Expose
private String categoryName;
@SerializedName("firmId")
@Expose
private String firmId;

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

public String getLink() {
return link;
}

public void setLink(String link) {
this.link = link;
}

public String getCategoryName() {
return categoryName;
}

public void setCategoryName(String categoryName) {
this.categoryName = categoryName;
}

public String getFirmId() {
        return firmId;
    }

public void setFirmId(String firmId) {
        this.firmId = firmId;
    }

}