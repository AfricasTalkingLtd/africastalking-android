package com.africastalking.models.payment.checkout;

public final class BankCheckoutRequest extends CheckoutRequest {

    public BankAccount bankAccount;

    public BankCheckoutRequest(String productName, String amount, String narration) {
        super(productName, amount);
        this.type = TYPE.BANK;
        this.narration = narration;
    }

    public static class BankAccount {

        public String accountName;
        public String accountNumber;
        public int bankCode;
        public String dateOfBirth;

        /**
         * A bank account
         * @param accountName Bank account name e.g. Odeyola LeGrand
         * @param accountNumber Bank account number e.g. 0982627488993
         * @param bankCode Bank code. See supported banks {@link com.africastalking.models.payment.checkout.BankCode BankCode}
         */
        public BankAccount(String accountName, String accountNumber, BankCode bankCode) {
            this.accountName = accountName;
            this.accountNumber = accountNumber;
            this.bankCode = bankCode.code;
        }
    }
}
