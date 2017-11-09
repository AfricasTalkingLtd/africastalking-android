package com.africastalking.services;

import com.africastalking.models.voice.QueuedCallsResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface VoiceServiceInterface {

    @FormUrlEncoded
    @POST("mediaUpload")
    Call<String> mediaUpload(@Field("username") String username, @Field(value = "url") String url);


    @FormUrlEncoded
    @POST("queueStatus")
    Call<QueuedCallsResponse> queueStatus(@Field("username") String username, @Field(value = "phoneNumbers") String phoneNumbers);
}
