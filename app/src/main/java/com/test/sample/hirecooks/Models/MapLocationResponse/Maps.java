package com.test.sample.hirecooks.Models.MapLocationResponse;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.ArrayList;

public class Maps implements Serializable {
    @SerializedName("maps")
    @Expose
    private ArrayList<Map> maps;
    private Map map = null;

    public Maps() {
    }

    public ArrayList<Map> getMaps() {
        return maps;
    }

    public void setMaps(ArrayList<Map> maps) {
        this.maps = maps;
    }
    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }
}