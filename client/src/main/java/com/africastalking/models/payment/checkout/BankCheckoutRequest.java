package com.africastalking.models.payment.checkout;

public class BankCheckoutRequest extends CheckoutRequest {

    public BankAccount bankAccount;

    public BankCheckoutRequest(String productName, String currencyCode, float amount) {
        super(productName, currencyCode, amount);
        this.type = TYPE.BANK;
    }

    public static class BankAccount {
        public String accountName;
        public String accountNumber;
        public String bankName;
        public String countryCode;
    }
}
