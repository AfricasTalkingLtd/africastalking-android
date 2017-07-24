package com.africastalking;


import com.africastalking.interfaces.IAirtime;
import com.africastalking.models.AirtimeResponses;

import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.IOException;
import java.util.HashMap;


/**
 * AirtimeResponse Service; send airtime
 */
public final class AirtimeService extends Service {


    private static AirtimeService sInstance;
    private IAirtime service;

    private AirtimeService(String username, Format format, Currency currency) {
        super(username, format, currency);
    }

    AirtimeService() {
        super();
    }

    @Override
    protected AirtimeService getInstance(String username, Format format, Currency currency) {

        if (sInstance == null) {
            sInstance = new AirtimeService(username, format, currency);
        }

        return sInstance;
    }

    @Override
    protected void initService() {
        String url = "https://api."+ (AfricasTalking.ENV == Environment.SANDBOX ? Const.SANDBOX_DOMAIN : Const.PRODUCTION_DOMAIN);
        url += "/version1/airtime/";
        Retrofit retrofit = retrofitBuilder
                .baseUrl(url)
                .build();

        service = retrofit.create(IAirtime.class);
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
     * @param amount
     * @return
     * @throws IOException
     */
    public AirtimeResponses send(String phone, float amount) throws IOException {
        HashMap<String, Float> map = new HashMap<>();
        map.put(phone, amount);
        return send(map);
    }

    /**
     * Send airtime
     * <p>
     *     Asynchronously send the request and notify {@code callback} of its response or if an error
     * occurred
     * </p>
     * @param phone
     * @param amount
     * @param callback
     */
    public void send(String phone, float amount, Callback<AirtimeResponses> callback) {
        HashMap<String, Float> map = new HashMap<>();
        map.put(phone, amount);
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
    public AirtimeResponses send(HashMap<String, Float> recipients) throws IOException {
        String json = _makeRecipientsJSON(recipients);
        Response<AirtimeResponses> resp = service.send(username, json).execute();
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
    public void send(HashMap<String, Float> recipients, Callback<AirtimeResponses> callback) {
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
    private String _makeRecipientsJSON(HashMap<String, Float> recipients) throws IOException {

        if (recipients == null || recipients.size() == 0) {
            throw new IOException("Invalid recipients");
        }

        StringBuilder body = new StringBuilder();
        int count = recipients.size();
        for (String phone:recipients.keySet()) {
            String amount = currency.toString() + " " + recipients.get(phone).toString();
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
