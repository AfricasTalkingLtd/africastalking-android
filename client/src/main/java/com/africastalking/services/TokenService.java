package com.africastalking.services;



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

    public String createCheckoutToken(String phoneNumber) throws IOException {
        Response<String> resp = service.createCheckoutToken(phoneNumber).execute();
        if (resp.isSuccessful()) {
            return resp.body();
        }
        throw new IOException(resp.message());
    }

    public void createCheckoutToken(String phoneNumber, Callback<String> callback) {
        service.createCheckoutToken(phoneNumber).enqueue(makeCallback(callback));
    }
}
