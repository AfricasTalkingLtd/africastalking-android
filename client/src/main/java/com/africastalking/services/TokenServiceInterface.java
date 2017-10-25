package com.africastalking.services;


import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

interface TokenServiceInterface {

    @FormUrlEncoded
    @POST("checkout/token/create")
    Call<String> createCheckoutToken(@Field("phoneNumber") String phoneNumber);

}
