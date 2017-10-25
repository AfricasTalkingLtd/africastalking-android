package com.africastalking.services;


import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.africastalking.R;
import com.africastalking.models.payment.checkout.CardCheckoutRequest;
import com.africastalking.models.payment.checkout.CheckoutResponse;
import com.africastalking.utils.Callback;

import xyz.belvi.luhn.Luhn;
import xyz.belvi.luhn.cardValidator.models.LuhnCard;
import xyz.belvi.luhn.interfaces.LuhnCallback;
import xyz.belvi.luhn.interfaces.LuhnCardVerifier;

public class CardCheckout {

    PaymentService paymentService;

    public CardCheckout(PaymentService service) {
        paymentService = service;
    }

    /**
     *
     * @param context
     * @param callback
     */
    public void startCheckout(CardCheckoutRequest request, final Activity context, Callback<CheckoutResponse> callback) {

        // ... paymentService.checkout()

        Luhn.startLuhn(context, new LuhnCallback() {
            @Override
            public void cardDetailsRetrieved(Context luhnContext, LuhnCard creditCard, final LuhnCardVerifier cardVerifier) {
                Log.e("LLLL", "Details Received: " + creditCard.toString());
                cardVerifier.startProgress();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(3500);
                            context.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    cardVerifier.requestOTP(10);
                                }
                            });
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }).start();
            }

            @Override
            public void otpRetrieved(Context luhnContext, final LuhnCardVerifier cardVerifier, String otp) {
                Log.e("LLLL", "OTP Received: " + otp);
                cardVerifier.startProgress();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(3500);
                            context.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    cardVerifier.onCardVerified(false, "Payment failed", "We ave not implemented this yet!");
                                }
                            });
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }).start();
            }

            @Override
            public void onFinished(boolean isVerified) {
                Log.e("LLLL", "Verified " + String.valueOf(isVerified));
            }
        }, R.style.AfricasTalkingStyle);
    }
}