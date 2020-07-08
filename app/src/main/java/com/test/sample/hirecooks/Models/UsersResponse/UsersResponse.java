package com.test.sample.hirecooks.Models.UsersResponse;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UsersResponse implements Serializable {

    @SerializedName("users")
    @Expose
    private List<UserResponse> userResponses = null;

    public List<UserResponse> getUsersResponses() {
        return userResponses;
    }

    public void setUsersResponses(List<UserResponse> userResponses) {
        this.userResponses = userResponses;
    }
}