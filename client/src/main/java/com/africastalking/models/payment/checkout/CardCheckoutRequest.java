package com.africastalking.models.payment.checkout;

public class CardCheckoutRequest extends CheckoutRequest {

    public PaymentCard paymentCard;
    public String checkoutToken;

    public CardCheckoutRequest() {
        this.type = TYPE.CARD;
    }

    public static class PaymentCard {
        public long number;
        public int cvvNumber;
        public int expiryMonth;
        public int expiryYear;
        public String cardType;
        public String countryCode;
        public String authModel;
        public String authToken;
    }
}
