package com.africastalking.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SMSMessageData {

    @SerializedName("Recipients")
    @Expose
    private List<Recipient> recipients = null;

    public List<Recipient> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<Recipient> recipients) {
        this.recipients = recipients;
    }

}
