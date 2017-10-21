package com.africastalking.models;

import java.util.List;

public class AirtimeResponses {

    private Integer numSent;
    private String totalAmount;
    private String totalDiscount;
    private List<AirtimeResponse> responses = null;
    private String errorMessage;

    public Integer getNumSent() {
        return numSent;
    }

    public void setNumSent(Integer numSent) {
        this.numSent = numSent;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getTotalDiscount() {
        return totalDiscount;
    }

    public void setTotalDiscount(String totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    public List<AirtimeResponse> getResponses() {
        return responses;
    }

    public void setResponses(List<AirtimeResponse> responses) {
        this.responses = responses;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
