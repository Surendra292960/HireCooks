package com.test.sample.hirecooks.Models.OrderResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.test.sample.hirecooks.Models.Order.Order;

import java.io.Serializable;
import java.util.List;

public class Result implements Serializable {

    @SerializedName("order")
    @Expose
    private List<Order> order = null;

    public List<Order> getOrder() {
        return order;
    }

    public void setOrder(List<Order> order) {
        this.order = order;
    }

}