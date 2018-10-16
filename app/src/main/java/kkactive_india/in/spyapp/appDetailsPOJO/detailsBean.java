package kkactive_india.in.spyapp.appDetailsPOJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class detailsBean {

    @SerializedName("app_data")
    @Expose
    private List<AppDatum> appData = null;

    public List<AppDatum> getAppData() {
        return appData;
    }

    public void setAppData(List<AppDatum> appData) {
        this.appData = appData;
    }
}
