package com.africastalking.models.payment.checkout;

/**
 * Created by aksalj on 24/10/2017.
 */

public class CardCheckoutRequest extends CheckoutRequest {

    public String number;
    public int expirationMonth;
    public int expirationYear;
    public int cvv;

    public CardCheckoutRequest() {
        this.type = TYPE.CARD;
    }
}
