package kkactive_india.in.spyapp.MainPOJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MainBean {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("IMEI1")
    @Expose
    private String iMEI1;
    @SerializedName("IMEI2")
    @Expose
    private String iMEI2;
    @SerializedName("model")
    @Expose
    private String model;
    @SerializedName("android_version")
    @Expose
    private String androidVersion;
    @SerializedName("app_version")
    @Expose
    private String appVersion;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("carrier")
    @Expose
    private String carrier;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getAndroidVersion() {
        return androidVersion;
    }

    public void setAndroidVersion(String androidVersion) {
        this.androidVersion = androidVersion;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

}
