package com.test.sample.hirecooks.Models.Images;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Image implements Serializable {
@SerializedName("userImageId")
@Expose
private String userImageId;
@SerializedName("user")
@Expose
private String user;
@SerializedName("images")
@Expose
private String images;
@SerializedName("phone")
@Expose
private String phone;
@SerializedName("Caption")
@Expose
private String caption;
@SerializedName("imageOrder")
@Expose
private String imageOrder;
@SerializedName("userId")
@Expose
private String userId;

public String getUserImageId() {
return userImageId;
}

public void setUserImageId(String userImageId) {
this.userImageId = userImageId;
}

public String getUser() {
return user;
}

public void setUser(String user) {
this.user = user;
}

public String getImages() {
return images;
}

public void setImages(String images) {
this.images = images;
}

public String getPhone() {
return phone;
}

public void setPhone(String phone) {
this.phone = phone;
}

public String getCaption() {
return caption;
}

public void setCaption(String caption) {
this.caption = caption;
}

public String getImageOrder() {
return imageOrder;
}

public void setImageOrder(String imageOrder) {
this.imageOrder = imageOrder;
}

public String getUserId() {
return userId;
}

public void setUserId(String userId) {
this.userId = userId;
}

}