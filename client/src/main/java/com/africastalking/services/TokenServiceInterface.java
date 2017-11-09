package com.africastalking.services;


import com.africastalking.models.token.CheckoutTokenResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

interface TokenServiceInterface {

    @FormUrlEncoded
    @POST("checkout/token/create")
    Call<CheckoutTokenResponse> createCheckoutToken(@Field("phoneNumber") String phoneNumber);

}
