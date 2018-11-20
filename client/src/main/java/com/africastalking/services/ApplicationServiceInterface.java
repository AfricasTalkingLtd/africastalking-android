package com.africastalking.services;

import com.africastalking.models.account.ApplicationResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Account Endpoints
 */
public interface ApplicationServiceInterface {
    @GET("user")
    Call<ApplicationResponse> getUser(@Query("username") String username);
}
