package com.test.sample.hirecooks.Models.NewOrder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class OrdersResponse implements Serializable {
    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("orders_table")
    @Expose
    public List<OrdersTable> orders_table;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<OrdersTable> getOrders_table() {
        return orders_table;
    }

    public void setOrders_table(List<OrdersTable> orders_table) {
        this.orders_table = orders_table;
    }
}