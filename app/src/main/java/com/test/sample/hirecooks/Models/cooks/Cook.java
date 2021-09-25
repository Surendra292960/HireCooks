package com.test.sample.hirecooks.Models.cooks;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Cook implements Serializable {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("cook_type")
    @Expose
    private String cook_type;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("address_book")
    @Expose
    private String address;

    public Cook(String name, String email, String password, String gender, String phone, String cook_type, String image, String address) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.phone = phone;
        this.image = image;
        this.address = address;
        this.cook_type = cook_type;
    }

    public Cook(int id, String name, String email, String gender, String phone, String cook_type, String image, String address){
        this.id = id;
        this.name = name;
        this.email = email;
        this.gender = gender;
        this.phone = phone;
        this.image = image;
        this.address = address;
        this.cook_type = cook_type;
    }

    public Cook(int id, String name, String email, String password, String gender, String phone, String cook_type, String image, String address) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.phone = phone;
        this.image = image;
        this.address = address;
        this.cook_type = cook_type;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword(){
        return password;
    }

    public String getGender() {
        return gender;
    }

    public String getPhone() {
        return phone;
    }

    public String getCook_type() {
        return cook_type;
    }

    public String getImage(){
        return image;
    }

    public String getAddress() {
        return address;
    }
}
