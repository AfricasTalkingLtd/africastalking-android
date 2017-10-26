package com.africastalking.models.payment.checkout;


public class MobileCheckoutRequest extends CheckoutRequest {

    public String providerChannel;
    public String phoneNumber;

    public MobileCheckoutRequest() {
        this.type = TYPE.MOBILE;
    }
}
