package com.test.sample.hirecooks.Models.Chat;

public class MessageResponse {
    private boolean error;
    private String message;

    public MessageResponse() {

    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}