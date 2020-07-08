package com.test.sample.hirecooks.Models.users;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Users implements Serializable {

    @SerializedName("users")
    @Expose
    private ArrayList<User> users;
 
    public Users() {
    }
 
    public ArrayList<User> getUsers() {
        return users;
    }
 
    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }
}