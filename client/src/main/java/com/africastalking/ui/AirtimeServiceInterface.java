package com.africastalking.ui;


import com.africastalking.models.airtime.AirtimeResponses;

import retrofit2.Call;
import retrofit2.http.*;

public interface AirtimeServiceInterface {

    @FormUrlEncoded
    @POST("send")
    Call<AirtimeResponses> send(@Field("username") String username, @Field(value = "recipients", encoded = false) String recipients);

}
