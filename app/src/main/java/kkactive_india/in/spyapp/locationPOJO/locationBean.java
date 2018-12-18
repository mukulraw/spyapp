package kkactive_india.in.spyapp.locationPOJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class locationBean {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("lat")
    @Expose
    private String lat;
    @SerializedName("lon")
    @Expose
    private String lon;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("battery")
    @Expose
    private String battery;
    @SerializedName("storage_available")
    @Expose
    private String storageAvailable;
    @SerializedName("storage_total")
    @Expose
    private String storageTotal;
    @SerializedName("wifi")
    @Expose
    private String wifi;
    @SerializedName("gps")
    @Expose
    private Object gps;
    @SerializedName("data")
    @Expose
    private String data;

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

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBattery() {
        return battery;
    }

    public void setBattery(String battery) {
        this.battery = battery;
    }

    public String getStorageAvailable() {
        return storageAvailable;
    }

    public void setStorageAvailable(String storageAvailable) {
        this.storageAvailable = storageAvailable;
    }

    public String getStorageTotal() {
        return storageTotal;
    }

    public void setStorageTotal(String storageTotal) {
        this.storageTotal = storageTotal;
    }

    public String getWifi() {
        return wifi;
    }

    public void setWifi(String wifi) {
        this.wifi = wifi;
    }

    public Object getGps() {
        return gps;
    }

    public void setGps(Object gps) {
        this.gps = gps;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
