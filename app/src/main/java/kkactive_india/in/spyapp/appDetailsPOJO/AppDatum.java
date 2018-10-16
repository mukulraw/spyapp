package kkactive_india.in.spyapp.appDetailsPOJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AppDatum {


    @SerializedName("appname")
    @Expose
    private String appname;
    @SerializedName("date")
    @Expose
    private String date;

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


}
