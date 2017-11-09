package com.africastalking.models.payment.checkout;

import java.util.HashMap;
import java.util.Map;

public abstract class CheckoutRequest {

    public String productName;
    public float amount;
    public String currencyCode;
    public String narration;
    public Map metadata = new HashMap();

    public TYPE type = TYPE.MOBILE;

    public enum TYPE {
        MOBILE,
        CARD,
        BANK,
    }

    CheckoutRequest(String productName, String amount) {
        this.productName = productName;

        try {
            String[] currenciedAmount = amount.trim().split(" ");
            this.currencyCode = currenciedAmount[0];
            this.amount = Float.parseFloat(currenciedAmount[1]);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
