package com.test.sample.hirecooks.Models.CooksPromotion;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CooksPromotion implements Serializable {
    @SerializedName("Id")
    @Expose
    public int Id ;
    @SerializedName("TagName")
    @Expose
    public String TagName ;
    @SerializedName("CookName")
    @Expose
    public String CookName ;
    @SerializedName("Link")
    @Expose
    public String Link ;
    @SerializedName("RoundIcon")
    @Expose
    public String RoundIcon ;
    @SerializedName("Color")
    @Expose
    public String Color ;

    public CooksPromotion(int Id, String CookName, String TagName, String Link, String RoundIcon, String Color) {
        this.Id = Id;
        this.TagName = TagName;
        this.CookName = CookName;
        this.Link = Link;
        this.RoundIcon = RoundIcon;
        this.Color = Color;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getCookName() {
        return CookName;
    }

    public void setCookName(String cookName) {
        CookName = cookName;
    }

    public String getTagName() {
        return TagName;
    }

    public void setTagName(String tagName) {
        TagName = tagName;
    }

    public String getLink() {
        return Link;
    }

    public void setLink(String link) {
        Link = link;
    }

    public String getRoundIcon() {
        return RoundIcon;
    }

    public void setRoundIcon(String roundIcon) {
        RoundIcon = roundIcon;
    }

    public String getColor() {
        return Color;
    }

    public void setColor(String color) {
        Color = color;
    }
}
