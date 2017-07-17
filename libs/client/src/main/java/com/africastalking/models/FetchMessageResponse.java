package com.africastalking.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by jay on 7/17/17.
 */

public class FetchMessageResponse {
    @SerializedName("SMSMessageData")
    @Expose
    private SMSMessageData sMSMessageData;

    public SMSMessageData getSMSMessageData() {
        return sMSMessageData;
    }

    public void setSMSMessageData(SMSMessageData sMSMessageData) {
        this.sMSMessageData = sMSMessageData;
    }
}
