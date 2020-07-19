package com.test.sample.hirecooks.Models.OfferSubCategory;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class OffersSubcategory implements Serializable {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("subcategoryid")
    @Expose
    private Integer subcategoryid;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("link")
    @Expose
    private String link;
    @SerializedName("link2")
    @Expose
    private String link2;
    @SerializedName("link3")
    @Expose
    private String link3;
    @SerializedName("link4")
    @Expose
    private String link4;
    @SerializedName("shield_link")
    @Expose
    private String shieldLink;
    @SerializedName("discription")
    @Expose
    private String discription;
    @SerializedName("detail_discription")
    @Expose
    private String detailDiscription;
    @SerializedName("sellRate")
    @Expose
    private Integer sellRate;
    @SerializedName("displayRate")
    @Expose
    private Integer displayRate;
    @SerializedName("firm_id")
    @Expose
    private String firmId;
    @SerializedName("stock")
    @Expose
    private Integer stock;
    @SerializedName("accepting_order")
    @Expose
    private Integer acceptingOrder;
    @SerializedName("available_stock")
    @Expose
    private Integer availableStock;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSubcategoryid() {
        return subcategoryid;
    }

    public void setSubcategoryid(Integer subcategoryid) {
        this.subcategoryid = subcategoryid;
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

    public String getLink2() {
        return link2;
    }

    public void setLink2(String link2) {
        this.link2 = link2;
    }

    public String getLink3() {
        return link3;
    }

    public void setLink3(String link3) {
        this.link3 = link3;
    }

    public String getLink4() {
        return link4;
    }

    public void setLink4(String link4) {
        this.link4 = link4;
    }

    public String getShieldLink() {
        return shieldLink;
    }

    public void setShieldLink(String shieldLink) {
        this.shieldLink = shieldLink;
    }

    public String getDiscription() {
        return discription;
    }

    public void setDiscription(String discription) {
        this.discription = discription;
    }

    public String getDetailDiscription() {
        return detailDiscription;
    }

    public void setDetailDiscription(String detailDiscription) {
        this.detailDiscription = detailDiscription;
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

    public String getFirm_id() {
        return firmId;
    }

    public void setFirm_id(String firmId) {
        this.firmId = firmId;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getAcceptingOrder() {
        return acceptingOrder;
    }

    public void setAcceptingOrder(Integer acceptingOrder) {
        this.acceptingOrder = acceptingOrder;
    }

    public Integer getAvailableStock() {
        return availableStock;
    }

    public void setAvailableStock(Integer availableStock) {
        this.availableStock = availableStock;
    }
}