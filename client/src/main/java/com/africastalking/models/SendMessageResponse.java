package com.africastalking.models;

/**
 * Created by jay on 7/17/17.
 */


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

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