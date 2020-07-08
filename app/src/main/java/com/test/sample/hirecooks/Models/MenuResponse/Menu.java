package com.test.sample.hirecooks.Models.MenuResponse;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Menu implements Serializable {

    @SerializedName("menuid")
    @Expose
    private Integer menuid;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("link")
    @Expose
    private String link;
    @SerializedName("discount")
    @Expose
    private String discount;


    public Menu(Integer menuid, String name, String link, String discount) {
        this.menuid = menuid;
        this.name = name;
        this.link = link;
        this.discount = discount;
    }

    public Integer getMenuid() {
        return menuid;
    }

    public void setMenuid(Integer menuid) {
        this.menuid = menuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

}