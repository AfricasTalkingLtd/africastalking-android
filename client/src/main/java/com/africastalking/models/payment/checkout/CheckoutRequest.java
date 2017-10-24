package com.africastalking.models.payment.checkout;

/**
 * Created by aksalj on 24/10/2017.
 */

public abstract class CheckoutRequest {
    public enum TYPE {
        MOBILE,
        CARD
    }

    public TYPE type = TYPE.MOBILE;
}
