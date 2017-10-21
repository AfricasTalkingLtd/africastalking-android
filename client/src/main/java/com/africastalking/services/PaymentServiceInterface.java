package com.africastalking.services;


import com.africastalking.models.B2BResponse;
import com.africastalking.models.B2CResponse;
import com.africastalking.models.CheckoutResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

import java.util.HashMap;

public interface PaymentServiceInterface {

    @POST("mobile/checkout/request")
    Call<CheckoutResponse> checkout(@Body HashMap<String, Object> body);

    @POST("mobile/b2c/request")
    Call<B2CResponse> requestB2C(@Body HashMap<String, Object> body);


    @POST("mobile/b2b/request")
    Call<B2BResponse> requestB2B(@Body HashMap<String, Object> body);

}
