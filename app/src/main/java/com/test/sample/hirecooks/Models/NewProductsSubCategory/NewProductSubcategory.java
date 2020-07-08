package com.test.sample.hirecooks.Models.NewProductsSubCategory;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class NewProductSubcategory implements Serializable {

@SerializedName("id")
@Expose
private Integer id;
@SerializedName("name")
@Expose
private String name;
@SerializedName("subcategoryid")
@Expose
private Integer subcategoryid;
@SerializedName("link")
@Expose
private String link;
@SerializedName("discription")
@Expose
private String discription;
@SerializedName("sellRate")
@Expose
private Integer sellRate;
@SerializedName("displayRate")
@Expose
private Integer displayRate;
@SerializedName("weight")
@Expose
private String weight;
@SerializedName("firm_id")
@Expose
private String firm_id;
@SerializedName("stock")
@Expose
private Integer stock;

public Integer getId() {
return id;
}

public void setId(Integer id) {
this.id = id;
}

public String getName() {
return name;
}

public void setName(String name) {
this.name = name;
}

public Integer getSubcategoryid() {
return subcategoryid;
}

public void setSubcategoryid(Integer subcategoryid) {
this.subcategoryid = subcategoryid;
}

public String getLink() {
return link;
}

public void setLink(String link) {
this.link = link;
}

public String getDiscription() {
return discription;
}

public void setDiscription(String discription) {
this.discription = discription;
}

public Integer getSellRate() {
return sellRate;
}

public void setSellRate(Integer sellRate) {
this.sellRate = sellRate;
}

public Integer getDisplayRate() {
return displayRate;
}

public void setDisplayRate(Integer displayRate) {
this.displayRate = displayRate;
}

public String getWeight() {
return weight;
}

public void setWeight(String weight) {
this.weight = weight;
}

public String getFirm_id() {
        return firm_id;
    }

public void setFirm_id(String firm_id) {
        this.firm_id = firm_id;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
}