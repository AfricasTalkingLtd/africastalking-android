package com.africastalking.ui;

import com.africastalking.models.sms.FetchMessageResponse;
import com.africastalking.models.sms.SendMessageResponse;
import com.africastalking.models.sms.SubscriptionResponse;
import com.africastalking.models.sms.Subscriptions;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SmsServiceInterface {

    @FormUrlEncoded
    @POST("messaging")
    Call<SendMessageResponse> send(@Field("username") String username, @Field("to") String to,
                                   @Field("from") String from, @Field("message") String message);

    @FormUrlEncoded
    @POST("messaging")
    Call<SendMessageResponse> sendBulk(@Field("username") String username, @Field("to") String to,
                                   @Field("from") String from, @Field("message") String message,
                                   @Field("bulkSMSMode") int bulkMode, @Field("enqueue") String enqueue);
    @FormUrlEncoded
    @POST("messaging")
    Call<SendMessageResponse> sendPremium(@Field("username") String username, @Field("to") String to,
                                      @Field("from") String from, @Field("message") String message,
                                      @Field("keyword") String keyword, @Field("linkId") String linkId,
                                      @Field("retryDurationInHours") String retryDurationInHours,
                                          @Field("bulkSMSMode") int bulkMode);

    @GET("messaging")
    Call<FetchMessageResponse> fetchMessage(@Query("username") String username, @Query("lastReceivedId") String lastReceivedId);


    @GET("subscription")
    Call<Subscriptions> fetchSubscription(@Query("username") String username, @Query("shortCode") String shortCode,
                                         @Query("keyword") String keyword, @Query("lastReceivedId") String lastReceivedId);

    @FormUrlEncoded
    @POST("subscription/create")
    Call<SubscriptionResponse> createSubscription(@Field("username") String username, @Field("shortCode") String shortCode,
                                                  @Field("keyword") String keyword, @Field("phoneNumber") String phoneNumber,
                                                  @Field("checkoutToken") String checkoutToken);

}
