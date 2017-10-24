package com.africastalking.models.payment;

public class B2BResponse {

    private String status;
    private String transactionId;
    private String transactionFee;
    private String providerChannel;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionFee() {
        return transactionFee;
    }

    public void setTransactionFee(String transactionFee) {
        this.transactionFee = transactionFee;
    }

    public String getProviderChannel() {
        return providerChannel;
    }

    public void setProviderChannel(String providerChannel) {
        this.providerChannel = providerChannel;
    }

}
