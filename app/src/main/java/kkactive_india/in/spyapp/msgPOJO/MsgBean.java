package kkactive_india.in.spyapp.msgPOJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MsgBean {

    @SerializedName("msg_data")
    @Expose
    private List<MsgDatum> msgData = null;

    public List<MsgDatum> getMsgData() {
        return msgData;
    }

    public void setMsgData(List<MsgDatum> msgData) {
        this.msgData = msgData;
    }

}
