package xyz.belvi.luhn.interfaces;

import android.content.Context;

import xyz.belvi.luhn.cardValidator.models.LuhnBank;
import xyz.belvi.luhn.cardValidator.models.LuhnCard;

/**
 * Created by zone2 on 7/6/17.
 */

public interface LuhnCallback {

    void bankDetailsRetrieved(Context luhnContext, LuhnBank bank, LuhnVerifier verifier);

    void cardDetailsRetrieved(Context luhnContext, LuhnCard creditCard, LuhnVerifier verifier);

    void otpRetrieved(Context luhnContext, LuhnVerifier verifier, String otp);

    void onFinished(boolean isVerified);
}
