package com.africastalking.services;

import com.africastalking.models.airtime.AirtimeResponse;
import com.africastalking.utils.Callback;

import java.io.IOException;
import java.util.HashMap;

import retrofit2.Response;
import retrofit2.Retrofit;


/**
 * AirtimeResponse Service; send airtime
 */
public final class AirtimeService extends Service {

    static AirtimeService sInstance;
    private AirtimeServiceInterface service;

    AirtimeService() throws IOException {
        super();
    }

    @Override
    protected AirtimeService getInstance() throws IOException {
        if (sInstance == null) {
            sInstance = new AirtimeService();
        }
        return sInstance;
    }

    @Override
    protected void initService() {
        String url = "https://api." + (isSandbox ? SANDBOX_DOMAIN : PRODUCTION_DOMAIN);
        url += "/version1/airtime/";
        Retrofit retrofit = retrofitBuilder
                .baseUrl(url)
                .build();

        service = retrofit.create(AirtimeServiceInterface.class);
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


    /**
     * Send airtime
     * <p>
     *     Synchronously send the request and return its response.
     * </p>
     * @param phone
     * @param currency
     * @param amount
     * @return
     * @throws IOException
     */
    public AirtimeResponse send(String phone, String currency, float amount) throws IOException {
        HashMap<String, String> map = new HashMap<>();
        map.put(phone, currency + " " + amount);
        return send(map);
    }

    /**
     * Send airtime
     * <p>
     *     Asynchronously send the request and notify {@code callback} of its response or if an error
     * occurred
     * </p>
     * @param phone
     * @param currency
     * @param amount
     * @param callback
     */
    public void send(String phone, String currency, float amount, Callback<AirtimeResponse> callback) {
        HashMap<String, String> map = new HashMap<>();
        map.put(phone, currency + " " + amount);
        send(map, callback);
    }

    /**
     * Send airtime
     * <p>
     *     Synchronously send the request and return its response.
     * </p>
     * @param recipients
     * @return
     * @throws IOException
     */
    public AirtimeResponse send(HashMap<String, String> recipients) throws IOException {
        String json = _makeRecipientsJSON(recipients);
        Response<AirtimeResponse> resp = service.send(username, json).execute();
        if (!resp.isSuccessful()) {
            throw new IOException(resp.errorBody().string());
        }
        return resp.body();
    }

    /**
     * Send airtime
     * <p>
     *     Asynchronously send the request and notify {@code callback} of its response or if an error
     * occurred
     * </p>
     * @param recipients
     * @param callback
     */
    public void send(HashMap<String, String> recipients, Callback<AirtimeResponse> callback) {
        try{
            String json = _makeRecipientsJSON(recipients);
            service.send(username, json).enqueue(makeCallback(callback));
        }catch (IOException ioe) {
            callback.onFailure(ioe);
        }
    }


    /**
     * Create required json for recipients
     * @param recipients
     * @return
     * @throws IOException
     */
    private String _makeRecipientsJSON(HashMap<String, String> recipients) throws IOException {

        if (recipients == null || recipients.size() == 0) {
            throw new IOException("Invalid recipients");
        }

        StringBuilder body = new StringBuilder();
        int count = recipients.size();
        for (String phone:recipients.keySet()) {
            String amount = recipients.get(phone);
            String target = "{\"phoneNumber\":\"" + phone + "\", \"amount\": \""+ amount +"\"}";
            body.append(target);

            if (count > 1) {
                body.append(",");
            }
            count--;
        }

        return "[" + body.toString() + "]";
    }

}
