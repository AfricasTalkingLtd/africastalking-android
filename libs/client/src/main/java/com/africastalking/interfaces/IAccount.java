package com.africastalking.interfaces;

import com.africastalking.models.UserData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Account Endpoints
 */
public interface IAccount {
    @GET("user")
    Call<UserData> getUser(@Query("username") String username);
}
