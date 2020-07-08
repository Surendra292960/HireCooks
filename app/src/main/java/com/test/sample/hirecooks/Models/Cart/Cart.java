package com.test.sample.hirecooks.Models.Cart;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;

public class Cart implements Serializable {

     int id;
     String name;
     String desc;
     int sellRate;
     int displayRate;
     double totalAmount;
     int itemQuantity;
     String link;
     String weight;
     String firm_id;


    public Cart() {
    }

    public Cart(int id, String name, String link, String desc, int sellRate, int displayRate, double totalAmount, int itemQuantity, String weight, String firm_id ) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.sellRate = sellRate;
        this.displayRate = displayRate;
        this.totalAmount = totalAmount;
        this.itemQuantity = itemQuantity;
        this.link = link;
        this.weight = weight;
        this.firm_id = firm_id;
    }

    public Cart(String name, String link, String desc, int sellRate, int displayRate, int itemQuantity, String weight, String firm_id) {
        this.name = name;
        this.desc = desc;
        this.sellRate = sellRate;
        this.displayRate = displayRate;
        this.itemQuantity = itemQuantity;
        this.link = link;
        this.weight = weight;
        this.firm_id = firm_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(int itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public int getSellRate() {
        return sellRate;
    }

    public void setSellRate(int sellRate) {
        this.sellRate = sellRate;
    }

    public int getDisplayRate() {
        return displayRate;
    }

    public void setDisplayRate(int displayRate) {
        this.displayRate = displayRate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
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
}