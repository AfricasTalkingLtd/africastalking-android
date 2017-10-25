package com.africastalking.ui;


import com.africastalking.models.payment.B2BResponse;
import com.africastalking.models.payment.B2CResponse;
import com.africastalking.models.payment.checkout.CheckoutResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

import java.util.HashMap;

public interface PaymentServiceInterface {

    @POST("mobile/checkout/request")
    Call<CheckoutResponse> mobileCheckout(@Body HashMap<String, Object> body);

    @POST("card/checkout")
    Call<CheckoutResponse> cardCheckout(@Body HashMap<String, Object> body);

    @POST("mobile/b2c/request")
    Call<B2CResponse> requestB2C(@Body HashMap<String, Object> body);


    @POST("mobile/b2b/request")
    Call<B2BResponse> requestB2B(@Body HashMap<String, Object> body);


}
