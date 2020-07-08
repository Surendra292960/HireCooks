package com.test.sample.hirecooks.Models.TokenResponse;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.ArrayList;

public class Tokens implements Serializable {

    @SerializedName("tokens")
    @Expose
    private ArrayList<Token> tokens;

    public Tokens() {
    }

    public ArrayList<Token> getTokens() {
        return tokens;
    }

    public void setTokens(ArrayList<Token> tokens) {
        this.tokens = tokens;
    }
}
