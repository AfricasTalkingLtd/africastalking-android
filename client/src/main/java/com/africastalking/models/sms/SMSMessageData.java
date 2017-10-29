package com.africastalking.models.sms;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.ArrayList;

public class SMSMessageData {

    @SerializedName("Recipients")
    @Expose
    private List<Recipient> recipients = new ArrayList<Recipient>();

    @SerializedName("Message")
    @Expose
    private String message = null;

    public List<Recipient> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<Recipient> recipients) {
        this.recipients = recipients;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
