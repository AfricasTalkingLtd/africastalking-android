package com.africastalking.models.payment;

import java.util.List;

public class B2CResponse {

    private Integer numQueued;
    private String totalValue;
    private String totalTransactionFee;
    private List<B2CEntry> entries = null;

    public Integer getNumQueued() {
        return numQueued;
    }

    public void setNumQueued(Integer numQueued) {
        this.numQueued = numQueued;
    }

    public String getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(String totalValue) {
        this.totalValue = totalValue;
    }

    public String getTotalTransactionFee() {
        return totalTransactionFee;
    }

    public void setTotalTransactionFee(String totalTransactionFee) {
        this.totalTransactionFee = totalTransactionFee;
    }

    public List<B2CEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<B2CEntry> entries) {
        this.entries = entries;
    }


}
