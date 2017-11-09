package com.africastalking.models.payment.checkout;


public final class MobileCheckoutRequest extends CheckoutRequest {

    public String providerChannel;
    public String phoneNumber;

    public MobileCheckoutRequest(String productName, String amount, String phoneNumber, String providerChannel) {
        super(productName, amount);
        this.type = TYPE.MOBILE;
        this.phoneNumber = phoneNumber;
        this.providerChannel = providerChannel;
    }

    public MobileCheckoutRequest(String productName, String amount, String phoneNumber) {
        this(productName, amount, phoneNumber, null);
    }
}
