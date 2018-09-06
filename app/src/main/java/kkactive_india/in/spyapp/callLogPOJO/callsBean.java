package kkactive_india.in.spyapp.callLogPOJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class callsBean {

    @SerializedName("call_logs")
    @Expose
    private List<calls> callLogs = null;

    public List<calls> getCallLogs() {
        return callLogs;
    }

    public void setCallLogs(List<calls> callLogs) {
        this.callLogs = callLogs;
    }
}
