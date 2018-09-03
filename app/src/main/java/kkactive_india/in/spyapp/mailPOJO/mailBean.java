package kkactive_india.in.spyapp.mailPOJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class mailBean {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("result")
    @Expose
    private Result result;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }
}
