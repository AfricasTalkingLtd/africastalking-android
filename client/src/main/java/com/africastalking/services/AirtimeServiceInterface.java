package com.africastalking.services;

import com.africastalking.models.airtime.AirtimeResponse;

import retrofit2.Call;
import retrofit2.http.*;

public interface AirtimeServiceInterface {

    @FormUrlEncoded
    @POST("send")
    Call<AirtimeResponse> send(@Field("username") String username, @Field(value = "recipients", encoded = false) String recipients);

}
