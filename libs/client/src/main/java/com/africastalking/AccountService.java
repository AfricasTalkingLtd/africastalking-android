package com.africastalking;


import com.africastalking.interfaces.IAccount;
import com.africastalking.models.UserData;

import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.IOException;

/**
 * Account service. Retrieve user account info
 */
public class AccountService extends Service {

    private static AccountService sInstance;
    private IAccount service;

    private AccountService(String username, Format format, Currency currency) {
        super(username, format, currency);
    }

    AccountService() {
        super();
    }

    @Override
    protected AccountService getInstance(String username, Format format, Currency currency) {

        if (sInstance == null) {
            sInstance = new AccountService(username, format, currency);
        }

        return sInstance;
    }

    @Override
    protected void initService() {
        String url = "https://api."+ (AfricasTalking.ENV == Environment.SANDBOX ? Const.SANDBOX_DOMAIN : Const.PRODUCTION_DOMAIN);
        url += "/version1/";
        Retrofit retrofit = retrofitBuilder
                .baseUrl(url)
                .build();

        service = retrofit.create(IAccount.class);
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

    /**
     * Get user info.
     * <p>
     *     Synchronously send the request and return its response.
     * </p>
     * @return String in specified format, xml or json
     * @throws IOException
     */
    public UserData getUser() throws IOException {
        Response<UserData> resp = service.getUser(username).execute();
        return resp.body();
    }

    /**
     * Get user info.
     * <p>
     *     Asynchronously send the request and notify {@code callback} of its response or if an error
     * occurred
     * </p>
     * @param callback
     */
    public void getUser(final Callback<UserData> callback) {
        service.getUser(username).enqueue(makeCallback(callback));
    }
}
