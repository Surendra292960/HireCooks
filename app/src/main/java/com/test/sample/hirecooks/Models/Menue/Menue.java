package com.test.sample.hirecooks.Models.Menue;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Menue implements Serializable {

@SerializedName("id")
@Expose
private Integer id;
@SerializedName("menueid")
@Expose
private String menueid;
@SerializedName("link")
@Expose
private String link;
@SerializedName("name")
@Expose
private String name;

public Integer getId() {
return id;
}

public void setId(Integer id) {
this.id = id;
}

public String getMenueid() {
return menueid;
}

public void setMenueid(String menueid) {
this.menueid = menueid;
}

public String getLink() {
return link;
}

public void setLink(String link) {
this.link = link;
}

public String getName() {
return name;
}

public void setName(String name) {
this.name = name;
}

}