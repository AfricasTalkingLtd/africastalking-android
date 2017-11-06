package com.africastalking.models.payment.checkout;

public class CheckoutValidateRequest {
    public transient CheckoutRequest.TYPE type = CheckoutRequest.TYPE.CARD;
    public String transactionId;
    public String token;
}
