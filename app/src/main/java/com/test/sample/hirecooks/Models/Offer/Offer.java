package com.test.sample.hirecooks.Models.Offer;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Offer implements Serializable {
    @SerializedName("Id")
    @Expose
    public int Id ;
    @SerializedName("Name")
    @Expose
    public String Name ;
    @SerializedName("Link")
    @Expose
    public String Link ;
    @SerializedName("RoundIcon")
    @Expose
    public String RoundIcon ;
    @SerializedName("Color")
    @Expose
    public String Color ;

    public Offer(int id, String name, String link, String roundIcon, String color) {
        Id = id;
        Name = name;
        Link = link;
        RoundIcon = roundIcon;
        Color = color;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
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
