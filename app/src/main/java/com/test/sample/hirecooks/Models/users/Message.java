package com.test.sample.hirecooks.Models.users;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Message implements Serializable {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("from")
    @Expose
    private String from;
    @SerializedName("to")
    @Expose
    private String to;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("sent")
    @Expose
    private String sent;


    public Message(int id, String from, String to, String title, String message, String sent) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.title = title;
        this.message = message;
        this.sent = sent;
    }

    public int getId() {
        return id;
    }

       public String getFrom() {
        return from;
    }
    public String getTo() {
        return to;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getSent() {
        return sent;
    }
}