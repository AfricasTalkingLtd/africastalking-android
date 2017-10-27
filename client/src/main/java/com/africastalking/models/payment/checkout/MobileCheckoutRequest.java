package com.africastalking.models.payment.checkout;


public class MobileCheckoutRequest extends CheckoutRequest {

    public String providerChannel;
    public String phoneNumber;

    public MobileCheckoutRequest(String productName, String currencyCode, float amount, String phoneNumber) {
        super(productName, currencyCode, amount);
        this.type = TYPE.MOBILE;
        this.phoneNumber = phoneNumber;
    }
}
