package com.test.sample.hirecooks.Models.MenuResponse;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class Menus implements Serializable {
    @SerializedName("menu")
    @Expose
    private List<Menu> menu = null;

    public List<Menu> getMenus() {
        return menu;
    }

    public void setMenus(List<Menu> menu) {
        this.menu = menu;
    }
}