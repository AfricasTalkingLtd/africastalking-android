package com.africastalking.ui;


import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.africastalking.models.payment.checkout.CardCheckoutRequest;
import com.africastalking.models.payment.checkout.CheckoutResponse;
import com.africastalking.utils.Callback;

import xyz.belvi.luhn.Luhn;
import xyz.belvi.luhn.cardValidator.models.LuhnCard;
import xyz.belvi.luhn.interfaces.LuhnCallback;
import xyz.belvi.luhn.interfaces.LuhnCardVerifier;

public class CardCheckout {

    private PaymentService paymentService;

    public CardCheckout(PaymentService service) {
        paymentService = service;
    }

    /**
     *
     * @param context
     * @param callback
     */
    public void startCheckout(final Activity context, final Callback<CheckoutResponse> callback) {

        Luhn.startLuhn(context, new LuhnCallback() {
            @Override
            public void cardDetailsRetrieved(Context luhnContext, LuhnCard creditCard, final LuhnCardVerifier cardVerifier) {
                cardVerifier.startProgress();

                CardCheckoutRequest request = new CardCheckoutRequest();
                request.cvv = Integer.parseInt(creditCard.getCvv());
                request.expirationMonth = creditCard.getExpMonth();
                request.expirationYear = creditCard.getExpMonth();
                request.number = creditCard.getPan();

                paymentService.checkout(request, new Callback<CheckoutResponse>() {
                    @Override
                    public void onSuccess(CheckoutResponse data) {
                        boolean success = data.getStatus().contentEquals("Success");
                        cardVerifier.onCardVerified(success, "Payment failed", data.getDescription());
                        callback.onSuccess(data);
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        cardVerifier.onCardVerified(false, "Payment failed", throwable.getMessage() + "");
                        callback.onFailure(throwable);
                    }
                });
            }

            @Override
            public void otpRetrieved(Context luhnContext, final LuhnCardVerifier cardVerifier, String otp) {}

            @Override
            public void onFinished(boolean isVerified) { }
        }, R.style.AfricasTalkingStyle);
    }
}