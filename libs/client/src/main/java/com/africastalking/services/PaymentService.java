package com.africastalking.services;

import com.africastalking.Callback;
import com.africastalking.Environment;
import com.africastalking.models.B2BResponse;
import com.africastalking.models.B2CResponse;
import com.africastalking.models.Business;
import com.africastalking.models.CheckoutResponse;
import com.africastalking.models.Consumer;
import com.africastalking.proto.SdkServerServiceOuterClass;
import com.google.gson.Gson;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaymentService extends Service {

    private PaymentService sInstance;
    private PaymentServiceInterface payment;


    public PaymentService() throws IOException {
        super();
    }


    @Override
    protected void fetchToken(String host, int port) throws IOException {
        // TODO: More than one capability for payments
        fetchServiceToken(host, port, SdkServerServiceOuterClass.ClientTokenRequest.Capability.C2B);
    }

    @Override
    protected PaymentService getInstance() throws IOException {
        if(sInstance == null) {
            sInstance = new PaymentService();
        }
        return sInstance;
    }

    @Override
    protected boolean isInitialized() {
        return sInstance != null;
    }

    @Override
    protected void initService() {
        String baseUrl = "https://payments."+ (ENV == Environment.SANDBOX ? SANDBOX_DOMAIN : PRODUCTION_DOMAIN) + "/";
        payment = retrofitBuilder
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(baseUrl)
                .build()
                .create(PaymentServiceInterface.class);
    }

    @Override
    protected void destroyService() {
        if (sInstance != null) {
            sInstance = null;
        }
    }


    private HashMap<String, Object> makeCheckoutRequest(String product, String phone, String currency, float amount, Map metadata) {
        HashMap<String, Object> body = new HashMap<>();
        body.put("username", USERNAME);
        body.put("productName", product);
        body.put("phoneNumber", phone);
        body.put("amount", amount);
        body.put("currencyCode", currency);
        body.put("metadata", metadata);
        return body;
    }

    private HashMap<String, Object> makeB2CRequest(String product, List<Consumer> recipients) {
        HashMap<String, Object> body = new HashMap<>();
        body.put("username", USERNAME);
        body.put("productName", product);
        body.put("recipients", recipients);

        return body;
    }

    private HashMap<String, Object> makeB2BRequest(String product, Business recipient) {
        HashMap<String, Object> body = new HashMap<>();
        body.put("username", USERNAME);
        body.put("productName", product);

        //
        Gson gson = new Gson();
        String json = gson.toJson(recipient);
        HashMap map = gson.fromJson(json, HashMap.class);
        body.putAll(map);

        return body;
    }

    /**
     *
     * @param productName
     * @param phoneNumber
     * @param currency
     * @param amount
     * @param metadata
     * @return
     * @throws IOException
     */
    public CheckoutResponse checkout(String productName, String phoneNumber, String currency, float amount, Map metadata) throws IOException {
        HashMap<String, Object> body = makeCheckoutRequest(productName, phoneNumber, currency, amount, metadata);

        Call<CheckoutResponse> call = payment.checkout(body);
        Response<CheckoutResponse> res = call.execute();
        return res.body();

    }

    /**
     *
     * @param productName
     * @param phoneNumber
     * @param currency
     * @param amount
     * @return
     * @throws IOException
     */
    public CheckoutResponse checkout(String productName, String phoneNumber, String currency, float amount) throws IOException {
        return this.checkout(productName, phoneNumber, currency, amount, new HashMap());
    }

    /**
     *
     * @param productName
     * @param phoneNumber
     * @param amount
     * @param currency
     * @param metadata
     * @param callback
     */
    public void checkout(String productName, String phoneNumber, String currency, float amount, Map metadata, Callback<CheckoutResponse> callback) {
        HashMap<String, Object> body = makeCheckoutRequest(productName, phoneNumber, currency, amount, metadata);
        Call<CheckoutResponse> call = payment.checkout(body);
        call.enqueue(makeCallback(callback));
    }

    public void checkout(String productName, String phoneNumber, String currency, float amount, Callback<CheckoutResponse> callback) {
        this.checkout(productName, phoneNumber, currency, amount, new HashMap(), callback);
    }


    /**
     *
     * @param product
     * @param recipients
     * @return
     */
    public B2CResponse payConsumers(String product, List<Consumer> recipients) throws IOException {
        HashMap<String, Object> body = makeB2CRequest(product, recipients);
        Call<B2CResponse> call = payment.requestB2C(body);
        Response<B2CResponse> res = call.execute();
        return res.body();
    }

    public B2CResponse payConsumer(String product, Consumer recipient) throws IOException {
        List<Consumer> recipients = new ArrayList<>();
        recipients.add(recipient);
        return this.payConsumers(product, recipients);
    }

    /**
     *
     * @param product
     * @param recipients
     * @param callback
     */
    public void payConsumers(String product, List<Consumer> recipients, Callback<B2CResponse> callback) {
        HashMap<String, Object> body = makeB2CRequest(product, recipients);
        Call<B2CResponse> call = payment.requestB2C(body);
        call.enqueue(makeCallback(callback));
    }

    public void payConsumer(String product, Consumer recipient, Callback<B2CResponse> callback) {
        List<Consumer> recipients = new ArrayList<>();
        recipients.add(recipient);
        this.payConsumers(product, recipients, callback);
    }

    public B2BResponse payBusiness(String product, Business recipient) throws IOException {
        HashMap<String, Object> body = makeB2BRequest(product, recipient);
        Call<B2BResponse> call = payment.requestB2B(body);
        Response<B2BResponse> res = call.execute();
        return res.body();
    }

    public void payBusiness(String product, Business recipient, Callback<B2BResponse> callback) {
        HashMap<String, Object> body = makeB2BRequest(product, recipient);
        Call<B2BResponse> call = payment.requestB2B(body);
        call.enqueue(makeCallback(callback));
    }
}
