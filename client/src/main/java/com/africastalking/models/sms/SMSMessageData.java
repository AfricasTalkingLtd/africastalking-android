package com.africastalking.models.sms;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SMSMessageData {

    @SerializedName("Recipients")
    @Expose
    private List<Recipient> recipients = null;

    @SerializedName("Messages")
    @Expose
    private List<Message> messages = null;

    public List<Recipient> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<Recipient> recipients) {
        this.recipients = recipients;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

}
