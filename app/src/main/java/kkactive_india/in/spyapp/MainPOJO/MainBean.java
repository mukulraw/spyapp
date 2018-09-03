package kkactive_india.in.spyapp.MainPOJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MainBean {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("IMEI1")
    @Expose
    private String iMEI1;
    @SerializedName("IMEI2")
    @Expose
    private String iMEI2;
    @SerializedName("locationAdd")
    @Expose
    private String locationAdd;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getIMEI1() {
        return iMEI1;
    }

    public void setIMEI1(String iMEI1) {
        this.iMEI1 = iMEI1;
    }

    public String getIMEI2() {
        return iMEI2;
    }

    public void setIMEI2(String iMEI2) {
        this.iMEI2 = iMEI2;
    }

    public String getLocationAdd() {
        return locationAdd;
    }

    public void setLocationAdd(String locationAdd) {
        this.locationAdd = locationAdd;
    }

}
