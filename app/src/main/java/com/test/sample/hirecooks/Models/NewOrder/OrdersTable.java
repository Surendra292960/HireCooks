package com.test.sample.hirecooks.Models.NewOrder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class OrdersTable implements Serializable {
    @SerializedName("id")
    @Expose
    public int id;
    @SerializedName("order_id")
    @Expose
    public int order_id;
    @SerializedName("last_update_order")
    @Expose
    public String last_update_order;
    @SerializedName("order_date_time")
    @Expose
    public String order_date_time;
    @SerializedName("total_amount")
    @Expose
    public Double total_amount;
    @SerializedName("shipping_price")
    @Expose
    public int shipping_price;
    @SerializedName("payment_type")
    @Expose
    public String payment_type;
    @SerializedName("order_status")
    @Expose
    public String order_status;
    @SerializedName("order_latitude")
    @Expose
    public String order_latitude;
    @SerializedName("order_longitude")
    @Expose
    public String order_longitude;
    @SerializedName("order_address")
    @Expose
    public String order_address;
    @SerializedName("order_sub_address")
    @Expose
    public String order_sub_address;
    @SerializedName("order_pincode")
    @Expose
    public int order_pincode;
    @SerializedName("user_id")
    @Expose
    public int user_id;
    @SerializedName("user_name")
    @Expose
    public String user_name;
    @SerializedName("user_email")
    @Expose
    public String user_email;
    @SerializedName("user_phone")
    @Expose
    public String user_phone;
    @SerializedName("confirm_status")
    @Expose
    public String confirm_status;
    @SerializedName("orders")
    @Expose
    public List<Order> orders;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public String getLast_update_order() {
        return last_update_order;
    }

    public void setLast_update_order(String last_update_order) {
        this.last_update_order = last_update_order;
    }

    public String getOrder_date_time() {
        return order_date_time;
    }

    public void setOrder_date_time(String order_date_time) {
        this.order_date_time = order_date_time;
    }

    public Double getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(Double total_amount) {
        this.total_amount = total_amount;
    }

    public int getShipping_price() {
        return shipping_price;
    }

    public void setShipping_price(int shipping_price) {
        this.shipping_price = shipping_price;
    }

    public String getPayment_type() {
        return payment_type;
    }

    public void setPayment_type(String payment_type) {
        this.payment_type = payment_type;
    }

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }

    public String getOrder_latitude() {
        return order_latitude;
    }

    public void setOrder_latitude(String order_latitude) {
        this.order_latitude = order_latitude;
    }

    public String getOrder_longitude() {
        return order_longitude;
    }

    public void setOrder_longitude(String order_longitude) {
        this.order_longitude = order_longitude;
    }

    public String getOrder_address() {
        return order_address;
    }

    public void setOrder_address(String order_address) {
        this.order_address = order_address;
    }

    public String getOrder_sub_address() {
        return order_sub_address;
    }

    public void setOrder_sub_address(String order_sub_address) {
        this.order_sub_address = order_sub_address;
    }

    public int getOrder_pincode() {
        return order_pincode;
    }

    public void setOrder_pincode(int order_pincode) {
        this.order_pincode = order_pincode;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_phone() {
        return user_phone;
    }

    public void setUser_phone(String user_phone) {
        this.user_phone = user_phone;
    }

    public String getConfirm_status() {
        return confirm_status;
    }

    public void setConfirm_status(String confirm_status) {
        this.confirm_status = confirm_status;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
}