package com.africastalking.models.sms;

/**
 * Created by jay on 7/17/17.
 */


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SendMessageResponse {

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