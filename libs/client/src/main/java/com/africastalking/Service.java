package com.africastalking;

import com.africastalking.interceptors.AccountMockInterceptor;
import com.africastalking.interceptors.AirtimeMockInterceptor;
import com.africastalking.interceptors.PaymentMockInterceptor;
import com.africastalking.interceptors.SMSMockInterceptor;
import com.google.gson.GsonBuilder;

import okhttp3.*;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.*;
import retrofit2.Call;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;


/**
 * A given service offered by AT API
 */
abstract class Service {

    Retrofit.Builder retrofitBuilder;

    String username;
    String token;
    Currency currency;


    Service(final String username, final Format format, Currency currency) {

        this.username = username;
        this.currency = currency;

        if(token != null) { //TODO check if token is not expired
            token = AfricasTalking.getToken();
        }

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        if (AfricasTalking.LOGGING) {
            HttpLoggingInterceptor logger = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(String message) {
                    AfricasTalking.LOGGER.log(message);
                }
            });
            logger.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClient.addInterceptor(logger);
        }

        // Mock response for testing purposes
        if(AfricasTalking.CALLTYPE == CallType.MOCK){
            if(AfricasTalking.CALLSERVICE == CallService.SMS)
                httpClient.addInterceptor(new SMSMockInterceptor());
            if(AfricasTalking.CALLSERVICE == CallService.AIRTIME)
                httpClient.addInterceptor(new AirtimeMockInterceptor());
            if(AfricasTalking.CALLSERVICE == CallService.ACCOUNT)
                httpClient.addInterceptor(new AccountMockInterceptor());
            if(AfricasTalking.CALLSERVICE == CallService.PAYMENT)
                httpClient.addInterceptor(new PaymentMockInterceptor());
        }
        else {
            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    Request request = original.newBuilder()
                            .addHeader("token", token)
                            .addHeader("Accept", format.toString())
                            .build();

                    return chain.proceed(request);
                }
            });
        }


        retrofitBuilder = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create())) // switched from ScalarsConverterFactory
                .client(httpClient.build());

        initService();
    }

    Service() {}


    /**
     *
     * @param cb
     * @param <T>
     * @return
     */
    protected <T> retrofit2.Callback<T> makeCallback(final Callback<T> cb) {
        return new retrofit2.Callback<T>() {
            @Override
            public void onResponse(Call<T> call, retrofit2.Response<T> response) {
                if (response.isSuccessful()) {
                    cb.onSuccess(response.body());
                } else {
                    cb.onFailure(new Exception(response.message()));
                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                cb.onFailure(t);
            }
        };
    }

    /**
     * Get an instance of a service.
     * @param username
     * @param format
     * @param currency
     * @param <T>
     * @return
     */
    protected abstract <T extends Service> T getInstance(String username, Format format, Currency currency);

    /**
     * Check if a service is initialized
     * @return boolean true if yes, false otherwise
     */
    protected abstract boolean isInitialized();

    /**
     * Initializes a service
     */
    protected abstract void initService();

    /**
     * Destroys a service
     */
    protected abstract void destroyService();
}
