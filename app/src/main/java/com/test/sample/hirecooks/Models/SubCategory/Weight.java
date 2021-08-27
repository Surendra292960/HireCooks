package com.test.sample.hirecooks.Models.SubCategory;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Weight implements Serializable {

    @SerializedName("dozan")
    @Expose
    private Integer dozan;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("kg")
    @Expose
    private Integer kg;
    @SerializedName("pond")
    @Expose
    private Integer pond;
    @SerializedName("subcategory_id")
    @Expose
    private Integer subcategoryId;
    @SerializedName("x_id")
    @Expose
    private String xId;

    private boolean isSelected = false;

    public Integer getDozan() {
        return dozan;
    }

    public void setDozan(Integer dozan) {
        this.dozan = dozan;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getKg() {
        return kg;
    }

    public void setKg(Integer kg) {
        this.kg = kg;
    }

    public Integer getPond() {
        return pond;
    }

    public void setPond(Integer pond) {
        this.pond = pond;
    }

    public Integer getSubcategoryId() {
        return subcategoryId;
    }

    public void setSubcategoryId(Integer subcategoryId) {
        this.subcategoryId = subcategoryId;
    }

    public String getxId() {
        return xId;
    }

    public void setxId(String xId) {
        this.xId = xId;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}