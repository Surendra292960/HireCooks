package com.test.sample.hirecooks.Models.TokenResponse;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TokenResult implements Serializable {
    @SerializedName("error")
    @Expose
    private Boolean error;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("tokens")
    @Expose
    private Token token;

    public TokenResult(Boolean error, String message, Token token) {
        this.error = error;
        this.message = message;
        this.token = token;
    }
    public Boolean getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public Token getToken() {
        return token;
    }

}