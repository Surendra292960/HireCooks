package com.test.sample.hirecooks.Models.SubCategory;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import androidx.annotation.NonNull;

public class Subcategory implements Serializable {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("subcategoryid")
    @Expose
    private String subcategoryid;
    @SerializedName("last_update")
    @Expose
    private String lastUpdate;
    @SerializedName("search_key")
    @Expose
    private String searchKey;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("product_uniquekey")
    @Expose
    private String productUniquekey;
    @SerializedName("link2")
    @Expose
    private String link2;
    @SerializedName("link3")
    @Expose
    private String link3;
    @SerializedName("link4")
    @Expose
    private String link4;
    @SerializedName("shield_link")
    @Expose
    private String shieldLink;
    @SerializedName("discription")
    @Expose
    private String discription;
    @SerializedName("detail_discription")
    @Expose
    private String detailDiscription;
    @SerializedName("sellRate")
    @Expose
    private int sellRate;
    @SerializedName("displayRate")
    @Expose
    private int displayRate;
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
    private int frimPincode;
    @SerializedName("stock")
    @Expose
    private int stock;
    @SerializedName("accepting_order")
    @Expose
    private int acceptingOrder;
    @SerializedName("available_stock")
    @Expose
    private String availableStock;
    @SerializedName("brand")
    @Expose
    private String brand;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("age")
    @Expose
    private Integer age;
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

    double totalAmount;

    int itemQuantity;

    public Subcategory() {

    }

    public Subcategory(int id, String subcategoryid, String lastUpdate, String searchKey, String name, String productUniquekey, String link2, String link3,
                       String link4, String shieldLink, String discription, String detailDiscription, int sellRate, int displayRate, String firmId,
                       double firmLat, double firmLng, String firmAddress, int frimPincode, List<Color> colors, List<Image> images, List<Size> sizes,
                       List<Weight> weights, double totalAmount, String availableStock, int itemQuantity, String brand, String gender, int age) {
        this.id = id;
        this.subcategoryid = subcategoryid;
        this.lastUpdate = lastUpdate;
        this.searchKey = searchKey;
        this.name = name;
        this.productUniquekey = productUniquekey;
        this.link2 = link2;
        this.link3 = link3;
        this.link4 = link4;
        this.shieldLink = shieldLink;
        this.discription = discription;
        this.detailDiscription = detailDiscription;
        this.sellRate = sellRate;
        this.displayRate = displayRate;
        this.firmId = firmId;
        this.firmLat = firmLat;
        this.firmLng = firmLng;
        this.firmAddress = firmAddress;
        this.frimPincode = frimPincode;
        this.stock = stock;
        this.acceptingOrder = acceptingOrder;
        this.availableStock = availableStock;
        this.brand = brand;
        this.gender = gender;
        this.age = age;
        this.colors = colors;
        this.images = images;
        this.sizes = sizes;
        this.weights = weights;
        this.totalAmount = totalAmount;
        this.itemQuantity = itemQuantity;
    }

    public Subcategory(int id, String subcategoryid, String lastUpdate, String searchKey, String name, String productUniquekey, String link2, String link3,
                       String link4, String shieldLink, String discription, String detailDiscription, int sellRate, int displayRate, String firmId,
                       double firmLat, double firmLng, String firmAddress, int frimPincode, List<Color> colors, List<Image> images, List<Size> sizes,
                       List<Weight> weights, double totalAmount, int itemQuantity,String brand, String gender, int age,int acceptingOrder) {
        this.id = id;
        this.subcategoryid = subcategoryid;
        this.lastUpdate = lastUpdate;
        this.searchKey = searchKey;
        this.name = name;
        this.productUniquekey = productUniquekey;
        this.link2 = link2;
        this.link3 = link3;
        this.link4 = link4;
        this.shieldLink = shieldLink;
        this.discription = discription;
        this.detailDiscription = detailDiscription;
        this.sellRate = sellRate;
        this.displayRate = displayRate;
        this.firmId = firmId;
        this.firmLat = firmLat;
        this.firmLng = firmLng;
        this.firmAddress = firmAddress;
        this.frimPincode = frimPincode;
        this.stock = stock;
        this.acceptingOrder = acceptingOrder;
        this.availableStock = availableStock;
        this.brand = brand;
        this.gender = gender;
        this.age = age;
        this.colors = colors;
        this.images = images;
        this.sizes = sizes;
        this.weights = weights;
        this.totalAmount = totalAmount;
        this.itemQuantity = itemQuantity;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSubcategoryid() {
        return subcategoryid;
    }

    public void setSubcategoryid(String subcategoryid) {
        this.subcategoryid = subcategoryid;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getSearchKey() {
        return searchKey;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProductUniquekey() {
        return productUniquekey;
    }

    public void setProductUniquekey(String productUniquekey) {
        this.productUniquekey = productUniquekey;
    }

    public String getLink2() {
        return link2;
    }

    public void setLink2(String link2) {
        this.link2 = link2;
    }

    public String getLink3() {
        return link3;
    }

    public void setLink3(String link3) {
        this.link3 = link3;
    }

    public String getLink4() {
        return link4;
    }

    public void setLink4(String link4) {
        this.link4 = link4;
    }

    public String getShieldLink() {
        return shieldLink;
    }

    public void setShieldLink(String shieldLink) {
        this.shieldLink = shieldLink;
    }

    public String getDiscription() {
        return discription;
    }

    public void setDiscription(String discription) {
        this.discription = discription;
    }

    public String getDetailDiscription() {
        return detailDiscription;
    }

    public void setDetailDiscription(String detailDiscription) {
        this.detailDiscription = detailDiscription;
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

    public int getFrimPincode() {
        return frimPincode;
    }

    public void setFrimPincode(int frimPincode) {
        this.frimPincode = frimPincode;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getAcceptingOrder() {
        return acceptingOrder;
    }

    public void setAcceptingOrder(int acceptingOrder) {
        this.acceptingOrder = acceptingOrder;
    }

    public String getAvailableStock() {
        return availableStock;
    }

    public void setAvailableStock(String availableStock) {
        this.availableStock = availableStock;
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

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
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

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(int itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public static Comparator<Subcategory> priceComparator = new Comparator<Subcategory>() {
        @Override
        public int compare(Subcategory o1, Subcategory o2) {
            return (int) (o1.getSellRate() - o2.getSellRate());
        }
    };
    public static Comparator<Subcategory> genderComparator = new Comparator<Subcategory>() {
        @Override
        public int compare(Subcategory o1, Subcategory o2) {
            return o1.getGender().compareTo(o2.getGender());
        }
    };
}