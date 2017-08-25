package com.africastalking.services;

import com.africastalking.models.AccountResponse;
import com.africastalking.models.UserData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Account Endpoints
 */
public interface AccountServiceInterface {
    @GET("user")
    Call<AccountResponse> getUser(@Query("username") String username);
}
