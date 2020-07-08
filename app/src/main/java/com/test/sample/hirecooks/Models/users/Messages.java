package com.test.sample.hirecooks.Models.users;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;


public class Messages implements Serializable {

    @SerializedName("messages")
    private ArrayList<Message> messages = null;

    public Messages() {

    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }
}