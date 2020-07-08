package com.test.sample.hirecooks.Models.MapLocationResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Example implements Serializable {

@SerializedName("maps")
@Expose
private List<Map> maps = null;

public List<Map> getMaps() {
return maps;
}

public void setMaps(List<Map> maps) {
this.maps = maps;
}

}