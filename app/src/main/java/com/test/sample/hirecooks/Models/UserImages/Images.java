package com.test.sample.hirecooks.Models.UserImages;
import androidx.fragment.app.Fragment;
import androidx.room.Query;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Images implements Serializable {
    @SerializedName("authority")
    @Expose
    private Authority authority;
    @SerializedName("fragment")
    @Expose
    private Fragment fragment;
    @SerializedName("path")
    @Expose
    private Path path;
    @SerializedName("query")
    @Expose
    private Query query;
    @SerializedName("scheme")
    @Expose
    private String scheme;
    @SerializedName("uriString")
    @Expose
    private String uriString;
    @SerializedName("host")
    @Expose
    private String host;
    @SerializedName("port")
    @Expose
    private Integer port;

    public Authority getAuthority() {
        return authority;
    }

    public void setAuthority(Authority authority) {
        this.authority = authority;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public Query getQuery() {
        return query;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getUriString() {
        return uriString;
    }

    public void setUriString(String uriString) {
        this.uriString = uriString;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

}
