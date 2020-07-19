package com.test.sample.hirecooks.Models.TokenResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Tokens implements Serializable {

    @SerializedName("tokens")
    @Expose
    private List<Token> tokens = null;

    public List<Token> getTokens() {
        return tokens;
    }

    public void setTokens(List<Token> tokens) {
        this.tokens = tokens;
    }

}
