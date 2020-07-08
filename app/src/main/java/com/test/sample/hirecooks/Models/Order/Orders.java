package com.test.sample.hirecooks.Models.Order;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Orders implements Serializable {

    @SerializedName("order")
    @Expose
    private ArrayList<Orders> orders = null;
    private Order order = null;
    public Orders() {
    }

    public ArrayList<Orders> getOrders() {
        return orders;
    }

    public void setMaps(ArrayList<Orders> orders) {
        this.orders = orders;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}