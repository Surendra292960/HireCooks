package com.test.sample.hirecooks.Models.Chat;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Message implements Serializable {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("from_users_id")
    @Expose
    private Integer from_users_id;
    @SerializedName("to_users_id")
    @Expose
    private Integer to_users_id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("sentat")
    @Expose
    private String sentat;
    @SerializedName("sent")
    @Expose
    private Integer sent;
    @SerializedName("recieve")
    @Expose
    private Integer recieve;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFrom_users_id() {
        return from_users_id;
    }

    public void setFrom_users_id(Integer from_users_id) {
        this.from_users_id = from_users_id;
    }

    public Integer getTo_users_id() {
        return to_users_id;
    }

    public void setTo_users_id(Integer to_users_id) {
        this.to_users_id = to_users_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSentat() {
        return sentat;
    }

    public void setSentat(String sentat) {
        this.sentat = sentat;
    }

    public Integer getSent() {
        return sent;
    }

    public void setSent(Integer sent) {
        this.sent = sent;
    }

    public Integer getRecieve() {
        return recieve;
    }

    public void setRecieve(Integer recieve) {
        this.recieve = recieve;
    }

}