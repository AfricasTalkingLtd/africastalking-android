package com.africastalking.models.payment;

import java.util.List;

public final class BankTransferResponse {
    public String errorMessage;
    public List<BankEntry> entries;

    public static final class BankEntry {
        public String accountNumber;
        public String status;
        public String transactionId;
        public String transactionFee;
        public String errorMessage;
    }
}