package com.africastalking.services;


import com.africastalking.models.payment.B2BResponse;
import com.africastalking.models.payment.B2CResponse;
import com.africastalking.models.payment.checkout.CheckoutResponse;
import com.africastalking.models.payment.checkout.CheckoutValidationResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

import java.util.HashMap;

public interface PaymentServiceInterface {

    @POST("mobile/checkout/request")
    Call<CheckoutResponse> mobileCheckout(@Body HashMap<String, Object> body);

    @POST("mobile/b2c/request")
    Call<B2CResponse> requestB2C(@Body HashMap<String, Object> body);

    @POST("mobile/b2b/request")
    Call<B2BResponse> requestB2B(@Body HashMap<String, Object> body);

    @POST("card/checkout/charge")
    Call<CheckoutResponse> cardCheckoutCharge(@Body HashMap<String, Object> body);

    @POST("card/checkout/validate")
    Call<CheckoutValidationResponse> cardCheckoutValidate(@Body HashMap<String, Object> body);

    @POST("bank/checkout/charge")
    Call<CheckoutResponse> bankCheckoutCharge(@Body HashMap<String, Object> body);

    @POST("bank/checkout/validate")
    Call<CheckoutValidationResponse> bankCheckoutValidate(@Body HashMap<String, Object> body);

}
