package com.test.sample.hirecooks.Models.Menue;
import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MenueResponse implements Serializable {

@SerializedName("error")
@Expose
private Boolean error;
@SerializedName("message")
@Expose
private String message;
@SerializedName("Menue")
@Expose
private List<Menue> menue = null;

public Boolean getError() {
return error;
}

public void setError(Boolean error) {
this.error = error;
}

public String getMessage() {
return message;
}

public void setMessage(String message) {
this.message = message;
}

public List<Menue> getMenue() {
return menue;
}

public void setMenue(List<Menue> menue) {
this.menue = menue;
}

}
