package com.africastalking.services;


import com.africastalking.models.token.CheckoutTokenResponse;
import com.africastalking.utils.Callback;

import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.IOException;

/**
 * Token service.
 */
public class TokenService extends Service {

    static TokenService sInstance;
    private TokenServiceInterface service;

    TokenService() throws IOException {
        super();
    }

    @Override
    protected TokenService getInstance() throws IOException {

        if (sInstance == null) {
            sInstance = new TokenService();
        }

        return sInstance;
    }

    @Override
    protected void initService() {
        String url = "https://api."+ (isSandbox ? SANDBOX_DOMAIN : PRODUCTION_DOMAIN) + "/";
        Retrofit retrofit = retrofitBuilder
                .baseUrl(url)
                .build();

        service = retrofit.create(TokenServiceInterface.class);
    }

    @Override
    protected boolean isInitialized() {
        return sInstance != null;
    }

    @Override
    protected void destroyService() {
        if (sInstance != null) {
            sInstance = null;
        }
    }


    // ->

    public CheckoutTokenResponse createCheckoutToken(String phoneNumber) throws IOException {
        Response<CheckoutTokenResponse> resp = service.createCheckoutToken(phoneNumber).execute();
        if (!resp.isSuccessful()) {
            throw new IOException(resp.errorBody().string());
        }
        return resp.body();
    }

    public void createCheckoutToken(String phoneNumber, Callback<CheckoutTokenResponse> callback) {
        service.createCheckoutToken(phoneNumber).enqueue(makeCallback(callback));
    }
}
