package com.test.sample.hirecooks.Models.SearchSubCategory;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class Result implements Serializable {
    @SerializedName("search")
    @Expose
    private List<Search> search = null;

    public List<Search> getSearch() {
        return search;
    }

    public void setSearch(List<Search> search) {
        this.search = search;
    }
}
