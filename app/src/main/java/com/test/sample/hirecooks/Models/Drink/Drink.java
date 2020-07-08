package com.test.sample.hirecooks.Models.Drink;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Drink implements Serializable {
    @SerializedName("ID")
    @Expose
    public String ID ;
    @SerializedName("Name")
    @Expose
    public String Name ;
    @SerializedName("Link")
    @Expose
    public String Link ;
    @SerializedName("Price")
    @Expose
    public String Price ;
    @SerializedName("MenuId")
    @Expose
    public String MenuId;
    @SerializedName("Ribon")
    @Expose
    public String Ribon ;
    @SerializedName("position")
    @Expose
    private int position;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getRibon() {
        return Ribon;
    }

    public void setRibon(String ribon) {
        Ribon = ribon;
    }

    public String getLink() {
        return Link;
    }

    public void setLink(String link) {
        Link = link;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getMenuId() {
        return MenuId;
    }

    public void setMenuId(String menuId) {
        MenuId = menuId;
    }

    public int getPosition() {
        return position;
    }
    public void setPosition(int position){
        this.position = position;
    }
}
