package com.africastalking.ui;


import android.app.Activity;
import android.content.Context;

import com.africastalking.models.payment.checkout.CardCheckoutRequest;
import com.africastalking.models.payment.checkout.CheckoutRequest;
import com.africastalking.models.payment.checkout.CheckoutResponse;
import com.africastalking.models.payment.checkout.CheckoutValidateRequest;
import com.africastalking.models.payment.checkout.CheckoutValidationResponse;
import com.africastalking.services.PaymentService;
import com.africastalking.utils.Callback;

import xyz.belvi.luhn.Luhn;
import xyz.belvi.luhn.cardValidator.models.LuhnCard;
import xyz.belvi.luhn.interfaces.LuhnCallback;
import xyz.belvi.luhn.interfaces.LuhnCardVerifier;

public class CardCheckout {

    private PaymentService paymentService;
    private String currentTransaction = null;
    private CheckoutResponse currentResponse = null;

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
                request.checkoutToken = null; // FIXME: ???
                request.paymentCard = new CardCheckoutRequest.PaymentCard();
                request.paymentCard.cvvNumber = Integer.parseInt(creditCard.getCvv());
                request.paymentCard.expiryMonth = creditCard.getExpMonth();
                request.paymentCard.expiryYear = creditCard.getExpYear();
                request.paymentCard.number = Long.parseLong(creditCard.getPan()); // FIXME: ???

                paymentService.checkout(request, new Callback<CheckoutResponse>() {
                    @Override
                    public void onSuccess(CheckoutResponse data) {
                        boolean success = data.getStatus().contentEquals("Success");
                        if (success) {
                            currentTransaction = data.getTransactionId();
                            currentResponse = data;
                            cardVerifier.requestOTP(4); // FIXME: Lenght??
                        } else {
                            currentTransaction = null;
                            currentResponse = null;
                            cardVerifier.onCardVerified(false, "Charge Failed", data.getDescription());
                            callback.onFailure(new Exception(data.getDescription()));
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        currentTransaction = null;
                        currentResponse = null;
                        cardVerifier.onCardVerified(false, "Payment failed", throwable.getMessage() + "");
                        callback.onFailure(throwable);
                    }
                });
            }

            @Override
            public void otpRetrieved(Context luhnContext, final LuhnCardVerifier cardVerifier, String otp) {
                CheckoutValidateRequest request = new CheckoutValidateRequest();
                request.token = otp;
                request.transactionId = currentTransaction;
                paymentService.validateCheckout(CheckoutRequest.TYPE.CARD, request, new Callback<CheckoutValidationResponse>() {
                    @Override
                    public void onSuccess(CheckoutValidationResponse data) {
                        boolean success = data.status.contentEquals("Success");
                        cardVerifier.onCardVerified(success, "Payment failed", data.description);
                        if (success) {
                            callback.onSuccess(currentResponse);
                        } else {
                            callback.onFailure(new Exception(data.description));
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        cardVerifier.onCardVerified(false, "Payment failed", throwable.getMessage() + "");
                        callback.onFailure(throwable);
                    }
                });
            }

            @Override
            public void onFinished(boolean isVerified) { }
        }, R.style.AfricasTalkingStyle);
    }
}