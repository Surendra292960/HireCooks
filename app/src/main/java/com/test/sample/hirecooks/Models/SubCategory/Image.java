package com.test.sample.hirecooks.Models.SubCategory;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import androidx.annotation.Nullable;

public class Image implements Serializable {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("subcategory_id")
    @Expose
    private int subcategoryId;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("x_id")
    @Expose
    private String xId;
    @SerializedName("colorName")
    @Expose
    private String colorName;

    private boolean isSelected = false;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSubcategoryId() {
        return subcategoryId;
    }

    public void setSubcategoryId(int subcategoryId) {
        this.subcategoryId = subcategoryId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getxId() {
        return xId;
    }

    public void setxId(String xId) {
        this.xId = xId;
    }

    public String getColorName() {
        return colorName;
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}