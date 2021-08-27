package com.test.sample.hirecooks.Models.NewOrder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.test.sample.hirecooks.Models.SubCategory.Color;
import com.test.sample.hirecooks.Models.SubCategory.Image;
import com.test.sample.hirecooks.Models.SubCategory.Size;
import com.test.sample.hirecooks.Models.SubCategory.Weight;

import java.io.Serializable;
import java.util.List;

public class Order implements Serializable {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("subcategoryid")
    @Expose
    private int subcategoryid;
    @SerializedName("order_id")
    @Expose
    private int orderId;
    @SerializedName("product_uniquekey")
    @Expose
    private String productUniquekey;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("sellRate")
    @Expose
    private int sellRate;
    @SerializedName("displayRate")
    @Expose
    private int displayRate;
    @SerializedName("discount")
    @Expose
    private int discount;
    @SerializedName("quantity")
    @Expose
    private int quantity;
    @SerializedName("totalAmount")
    @Expose
    private double totalAmount;
    @SerializedName("link2")
    @Expose
    private String link2;
    @SerializedName("order_weight")
    @Expose
    private String orderWeight;
    @SerializedName("firm_id")
    @Expose
    private String firmId;
    @SerializedName("firm_lat")
    @Expose
    private double firmLat;
    @SerializedName("firm_lng")
    @Expose
    private double firmLng;
    @SerializedName("firm_address")
    @Expose
    private String firmAddress;
    @SerializedName("firm_pincode")
    @Expose
    private int firmPincode;
    @SerializedName("brand")
    @Expose
    private String brand;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("age")
    @Expose
    private int age;
    @SerializedName("colors")
    @Expose
    private List<Color> colors = null;
    @SerializedName("images")
    @Expose
    private List<Image> images = null;
    @SerializedName("sizes")
    @Expose
    private List<Size> sizes = null;
    @SerializedName("weights")
    @Expose
    private List<Weight> weights = null;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSubcategoryid() {
        return subcategoryid;
    }

    public void setSubcategoryid(int subcategoryid) {
        this.subcategoryid = subcategoryid;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getProductUniquekey() {
        return productUniquekey;
    }

    public void setProductUniquekey(String productUniquekey) {
        this.productUniquekey = productUniquekey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getLink2() {
        return link2;
    }

    public void setLink2(String link2) {
        this.link2 = link2;
    }

    public String getOrderWeight() {
        return orderWeight;
    }

    public void setOrderWeight(String orderWeight) {
        this.orderWeight = orderWeight;
    }

    public String getFirmId() {
        return firmId;
    }

    public void setFirmId(String firmId) {
        this.firmId = firmId;
    }

    public double getFirmLat() {
        return firmLat;
    }

    public void setFirmLat(double firmLat) {
        this.firmLat = firmLat;
    }

    public double getFirmLng() {
        return firmLng;
    }

    public void setFirmLng(double firmLng) {
        this.firmLng = firmLng;
    }

    public String getFirmAddress() {
        return firmAddress;
    }

    public void setFirmAddress(String firmAddress) {
        this.firmAddress = firmAddress;
    }

    public int getFirmPincode() {
        return firmPincode;
    }

    public void setFirmPincode(int firmPincode) {
        this.firmPincode = firmPincode;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public List<Color> getColors() {
        return colors;
    }

    public void setColors(List<Color> colors) {
        this.colors = colors;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public List<Size> getSizes() {
        return sizes;
    }

    public void setSizes(List<Size> sizes) {
        this.sizes = sizes;
    }

    public List<Weight> getWeights() {
        return weights;
    }

    public void setWeights(List<Weight> weights) {
        this.weights = weights;
    }
}