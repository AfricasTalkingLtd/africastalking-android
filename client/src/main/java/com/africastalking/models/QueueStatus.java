package com.africastalking.models;

public class QueueStatus {
    public String phoneNumber;
    public String queueName;
    public String numCalls;

    public String getNumCalls() {
        return numCalls;
    }

    public void setNumCalls(String numCalls) {
        this.numCalls = numCalls;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }
}
