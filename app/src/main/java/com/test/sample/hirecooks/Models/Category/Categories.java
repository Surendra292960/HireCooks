package com.test.sample.hirecooks.Models.Category;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.test.sample.hirecooks.Models.UsersResponse.UserResponse;

import java.io.Serializable;
import java.util.List;

public class Categories implements Serializable {

    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("category")
    @Expose
    private List<Category> category = null;

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

    public List<Category> getCategory() {
        return category;
    }

    public void setCategory(List<Category> category) {
        this.category = category;
    }

}