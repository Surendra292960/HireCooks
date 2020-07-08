package com.test.sample.hirecooks.Models.cooks;
import com.google.gson.annotations.SerializedName;

public class Result {
    @SerializedName("error")
    private Boolean error;

    @SerializedName("message")
    private String message;

    @SerializedName("cook")
    private Cook cook;

    public Result(Boolean error, String message, Cook cook) {
        this.error = error;
        this.message = message;
        this.cook = cook;
    }

    public Boolean getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public Cook getCook() {
        return cook;
    }
}