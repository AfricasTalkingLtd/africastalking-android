package com.africastalking.models.payment.checkout;

public class BankCheckoutRequest extends MobileCheckoutRequest {

    public BankAccount bankAccount;

    public BankCheckoutRequest() {
        this.type = TYPE.BANK;
    }

    class BankAccount {
        public String accountName;
        public String accountNumber;
        public String bankName;
        public String countryCode;
    }

}
