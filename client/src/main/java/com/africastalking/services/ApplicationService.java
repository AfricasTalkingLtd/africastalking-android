package com.africastalking.services;


import com.africastalking.utils.Callback;
import com.africastalking.models.account.ApplicationResponse;

import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.IOException;

/**
 * Account service. Retrieve user account info
 */
public class ApplicationService extends Service {

    static ApplicationService sInstance;
    private ApplicationServiceInterface service;

    ApplicationService() throws IOException {
        super();
    }

    @Override
    protected ApplicationService getInstance() throws IOException {

        if (sInstance == null) {
            sInstance = new ApplicationService();
        }

        return sInstance;
    }

    @Override
    protected void initService() {
        String url = "https://api."+ (isSandbox ? SANDBOX_DOMAIN : PRODUCTION_DOMAIN);
        url += "/version1/";
        Retrofit retrofit = retrofitBuilder
                .baseUrl(url)
                .build();

        service = retrofit.create(ApplicationServiceInterface.class);
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
    public ApplicationResponse getUser() throws IOException {
        Response<ApplicationResponse> resp = service.getUser(username).execute();
        if (!resp.isSuccessful()) {
            throw new IOException(resp.errorBody().string());
        }
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
    public void getUser(final Callback<ApplicationResponse> callback) {
        service.getUser(username).enqueue(makeCallback(callback));
    }
}
