package kkactive_india.in.spyapp.contactPOJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class contactBean {

    @SerializedName("contact_data")
    @Expose
    private List<ContactDatum> contactData = null;

    public List<ContactDatum> getContactData() {
        return contactData;
    }

    public void setContactData(List<ContactDatum> contactData) {
        this.contactData = contactData;
    }

}
