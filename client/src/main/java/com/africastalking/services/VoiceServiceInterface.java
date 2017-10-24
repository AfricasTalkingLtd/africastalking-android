package com.africastalking.services;

import com.africastalking.models.voice.QueueStatus;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

import java.util.List;

public interface VoiceServiceInterface {

    @FormUrlEncoded
    @POST("mediaUpload")
    Call<String> mediaUpload(@Field("username") String username, @Field(value = "url") String url);


    @FormUrlEncoded
    @POST("queueStatus")
    Call<List<QueueStatus>> queueStatus(@Field("username") String username, @Field(value = "phoneNumbers") String phoneNumbers);
}
