package com.test.sample.hirecooks.Models.UserImages;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Path implements Serializable {

@SerializedName("decoded")
@Expose
private String decoded;
@SerializedName("encoded")
@Expose
private String encoded;

public String getDecoded() {
return decoded;
}

public void setDecoded(String decoded) {
this.decoded = decoded;
}

public String getEncoded() {
return encoded;
}

public void setEncoded(String encoded) {
this.encoded = encoded;
}

}