package com.africastalking.interfaces;

import com.africastalking.models.Message;
import com.africastalking.models.Recipient;
import com.africastalking.models.Subscription;
import com.africastalking.models.SubscriptionResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ISMS {

    @FormUrlEncoded
    @POST("messaging")
    Call<List<Recipient>> send(@Field("username") String username, @Field("to") String to,
                               @Field("from") String from, @Field("message") String message);

    Call<List<Recipient>> sendBulk(@Field("username") String username, @Field("to") String to,
                                   @Field("from") String from, @Field("message") String message,
                                   @Field("bulkSMSMode") int bulkMode, @Field("enqueue") String enqueue);

    Call<List<Recipient>> sendPremium(@Field("username") String username, @Field("to") String to,
                                      @Field("from") String from, @Field("message") String message,
                                      @Field("keyword") String keyword, @Field("linkId") String linkId,
                                      @Field("retryDurationInHours") String retryDurationInHours);

    @GET("messaging")
    Call<List<Message>> fetchMessage(@Query("username") String username, @Query("lastReceivedId") String lastReceivedId);


    @GET("subscription")
    Call<List<Subscription>> fetchSubsciption(@Query("username") String username, @Query("shortCode") String shortCode,
                                              @Query("keyword") String keyword, @Query("lastReceivedId") String lastReceivedId);

    @FormUrlEncoded
    @POST("subscription/create")
    Call<SubscriptionResponse> createSubscription(@Field("username") String username, @Field("shortCode") String shortCode,
                                                  @Field("keyword") String keyword, @Field("phoneNumber") String phoneNumber);

}
