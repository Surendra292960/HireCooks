package com.test.sample.hirecooks.Models.Order;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Order implements Serializable {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("order_id")
    @Expose
    private String orderId;
    @SerializedName("product_name")
    @Expose
    private String productName;
    @SerializedName("product_sellRate")
    @Expose
    private Integer productSellRate;
    @SerializedName("product_displayRate")
    @Expose
    private Integer productDisplayRate;
    @SerializedName("product_discount")
    @Expose
    private Integer productDiscount;
    @SerializedName("product_quantity")
    @Expose
    private Integer productQuantity;
    @SerializedName("product_totalAmount")
    @Expose
    private Double productTotalAmount;
    @SerializedName("order_status")
    @Expose
    private String orderStatus;
    @SerializedName("product_image")
    @Expose
    private String productImage;
    @SerializedName("firm_id")
    @Expose
    private String firmId;
    @SerializedName("userId")
    @Expose
    private Integer userId;
    @SerializedName("order_date_time")
    @Expose
    private String orderDateTime;
    @SerializedName("order_weight")
    @Expose
    private String orderWeight;
    @SerializedName("payment_method")
    @Expose
    private String paymentMethod;
    @SerializedName("order_placeId")
    @Expose
    private String orderPlaceId;
    @SerializedName("order_latitude")
    @Expose
    private String orderLatitude;
    @SerializedName("order_longitude")
    @Expose
    private String orderLongitude;
    @SerializedName("order_address")
    @Expose
    private String orderAddress;
    @SerializedName("order_sub_address")
    @Expose
    private String orderSubAddress;
    @SerializedName("order_pincode")
    @Expose
    private Integer orderPincode;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("firm_lat")
    @Expose
    private String firmLat;
    @SerializedName("firm_lng")
    @Expose
    private String firmLng;
    @SerializedName("firm_address")
    @Expose
    private String firmAddress;
    @SerializedName("firm_pincode")
    @Expose
    private String firmPincode;
    @SerializedName("order_confirm")
    @Expose
    private String orderConfirm;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getProductSellRate() {
        return productSellRate;
    }

    public void setProductSellRate(Integer productSellRate) {
        this.productSellRate = productSellRate;
    }

    public Integer getProductDisplayRate() {
        return productDisplayRate;
    }

    public void setProductDisplayRate(Integer productDisplayRate) {
        this.productDisplayRate = productDisplayRate;
    }

    public Integer getProductDiscount() {
        return productDiscount;
    }

    public void setProductDiscount(Integer productDiscount) {
        this.productDiscount = productDiscount;
    }

    public Integer getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(Integer productQuantity) {
        this.productQuantity = productQuantity;
    }

    public Double getProductTotalAmount() {
        return productTotalAmount;
    }

    public void setProductTotalAmount(Double productTotalAmount) {
        this.productTotalAmount = productTotalAmount;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getFirmId() {
        return firmId;
    }

    public void setFirmId(String firmId) {
        this.firmId = firmId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getOrderDateTime() {
        return orderDateTime;
    }

    public void setOrderDateTime(String orderDateTime) {
        this.orderDateTime = orderDateTime;
    }

    public String getOrderWeight() {
        return orderWeight;
    }

    public void setOrderWeight(String orderWeight) {
        this.orderWeight = orderWeight;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getOrderPlaceId() {
        return orderPlaceId;
    }

    public void setOrderPlaceId(String orderPlaceId) {
        this.orderPlaceId = orderPlaceId;
    }

    public String getOrderLatitude() {
        return orderLatitude;
    }

    public void setOrderLatitude(String orderLatitude) {
        this.orderLatitude = orderLatitude;
    }

    public String getOrderLongitude() {
        return orderLongitude;
    }

    public void setOrderLongitude(String orderLongitude) {
        this.orderLongitude = orderLongitude;
    }

    public String getOrderAddress() {
        return orderAddress;
    }

    public void setOrderAddress(String orderAddress) {
        this.orderAddress = orderAddress;
    }

    public String getOrderSubAddress() {
        return orderSubAddress;
    }

    public void setOrderSubAddress(String orderSubAddress) {
        this.orderSubAddress = orderSubAddress;
    }

    public Integer getOrderPincode() {
        return orderPincode;
    }

    public void setOrderPincode(Integer orderPincode) {
        this.orderPincode = orderPincode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFirmLat() {
        return firmLat;
    }

    public void setFirmLat(String firmLat) {
        this.firmLat = firmLat;
    }

    public String getFirmLng() {
        return firmLng;
    }

    public void setFirmLng(String firmLng) {
        this.firmLng = firmLng;
    }

    public String getFirmAddress() {
        return firmAddress;
    }

    public void setFirmAddress(String firmAddress) {
        this.firmAddress = firmAddress;
    }

    public String getFirmPincode() {
        return firmPincode;
    }

    public void setFirmPincode(String firmPincode) {
        this.firmPincode = firmPincode;
    }

    public String getOrderConfirm() {
        return orderConfirm;
    }

    public void setOrderConfirm(String orderConfirm) {
        this.orderConfirm = orderConfirm;
    }
}