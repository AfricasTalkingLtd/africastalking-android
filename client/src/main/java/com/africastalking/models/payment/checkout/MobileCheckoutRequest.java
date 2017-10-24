package com.africastalking.models.payment.checkout;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by aksalj on 24/10/2017.
 */

public class MobileCheckoutRequest extends CheckoutRequest {

    public String productName;
    public String phoneNumber;
    public String currencyCode;
    public float amount;
    public Map metadata = new HashMap();

    public MobileCheckoutRequest() {
        this.type = TYPE.MOBILE;
    }
}
