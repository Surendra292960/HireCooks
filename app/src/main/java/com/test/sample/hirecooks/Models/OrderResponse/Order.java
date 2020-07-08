package com.test.sample.hirecooks.Models.OrderResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Order implements Serializable {

@SerializedName("id")
@Expose
private String id;
@SerializedName("name")
@Expose
private String name;
@SerializedName("email")
@Expose
private String email;
@SerializedName("gender")
@Expose
private String gender;
@SerializedName("phone")
@Expose
private String phone;
@SerializedName("signup_date")
@Expose
private String signupDate;
@SerializedName("order_id")
@Expose
private String orderId;
@SerializedName("product_name")
@Expose
private String productName;
@SerializedName("product_sellRate")
@Expose
private String productSellRate;
@SerializedName("product_displayRate")
@Expose
private String productDisplayRate;
@SerializedName("product_discount")
@Expose
private String productDiscount;
@SerializedName("product_quantity")
@Expose
private String productQuantity;
@SerializedName("product_totalAmount")
@Expose
private String productTotalAmount;
@SerializedName("order_status")
@Expose
private String orderStatus;
@SerializedName("product_image")
@Expose
private String productImage;
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
@SerializedName("order_pincode")
@Expose
private String orderPincode;
@SerializedName("latitude")
@Expose
private String latitude;
@SerializedName("longitude")
@Expose
private String longitude;
@SerializedName("address")
@Expose
private String address;
@SerializedName("pincode")
@Expose
private String pincode;
@SerializedName("placeId")
@Expose
private String placeId;

public String getId() {
return id;
}

public void setId(String id) {
this.id = id;
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

public String getGender() {
return gender;
}

public void setGender(String gender) {
this.gender = gender;
}

public String getPhone() {
return phone;
}

public void setPhone(String phone) {
this.phone = phone;
}

public String getSignupDate() {
return signupDate;
}

public void setSignupDate(String signupDate) {
this.signupDate = signupDate;
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

public String getProductSellRate() {
return productSellRate;
}

public void setProductSellRate(String productSellRate) {
this.productSellRate = productSellRate;
}

public String getProductDisplayRate() {
return productDisplayRate;
}

public void setProductDisplayRate(String productDisplayRate) {
this.productDisplayRate = productDisplayRate;
}

public String getProductDiscount() {
return productDiscount;
}

public void setProductDiscount(String productDiscount) {
this.productDiscount = productDiscount;
}

public String getProductQuantity() {
return productQuantity;
}

public void setProductQuantity(String productQuantity) {
this.productQuantity = productQuantity;
}

public String getProductTotalAmount() {
return productTotalAmount;
}

public void setProductTotalAmount(String productTotalAmount) {
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

public String getOrderPincode() {
return orderPincode;
}

public void setOrderPincode(String orderPincode) {
this.orderPincode = orderPincode;
}

public String getLatitude() {
return latitude;
}

public void setLatitude(String latitude) {
this.latitude = latitude;
}

public String getLongitude() {
return longitude;
}

public void setLongitude(String longitude) {
this.longitude = longitude;
}

public String getAddress() {
return address;
}

public void setAddress(String address) {
this.address = address;
}

public String getPincode() {
return pincode;
}

public void setPincode(String pincode) {
this.pincode = pincode;
}

public String getPlaceId() {
return placeId;
}

public void setPlaceId(String placeId) {
this.placeId = placeId;
}

}