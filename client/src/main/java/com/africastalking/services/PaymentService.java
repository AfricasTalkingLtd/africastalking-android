package com.africastalking.services;

import com.africastalking.models.payment.checkout.CheckoutRequest;
import com.africastalking.models.payment.checkout.MobileCheckoutRequest;
import com.africastalking.utils.Callback;
import com.africastalking.models.payment.B2BResponse;
import com.africastalking.models.payment.B2CResponse;
import com.africastalking.models.payment.Business;
import com.africastalking.models.payment.checkout.CheckoutResponse;
import com.africastalking.models.payment.Consumer;
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

    static PaymentService sInstance;
    private PaymentServiceInterface payment;


    PaymentService() throws IOException {
        super();
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
        String baseUrl = "https://payments."+ (isSandbox ? SANDBOX_DOMAIN : PRODUCTION_DOMAIN) + "/";
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


    private HashMap<String, Object> makeCheckoutRequest(CheckoutRequest request) {

        HashMap<String, Object> body = new HashMap<>();

        switch (request.type) {
            case MOBILE:
                MobileCheckoutRequest rq = (MobileCheckoutRequest) request;
                body.put("username", username);
                body.put("productName", rq.productName);
                body.put("phoneNumber", rq.phoneNumber);
                body.put("amount", rq.amount);
                body.put("currencyCode", rq.currencyCode);
                body.put("metadata", rq.metadata);
                break;
            case CARD:
                // TODO: Card Request
                break;
        }
        return body;
    }

    private HashMap<String, Object> makeB2CRequest(String product, List<Consumer> recipients) {
        HashMap<String, Object> body = new HashMap<>();
        body.put("username", username);
        body.put("productName", product);
        body.put("recipients", recipients);

        return body;
    }

    private HashMap<String, Object> makeB2BRequest(String product, Business recipient) {
        HashMap<String, Object> body = new HashMap<>();
        body.put("username", username);
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
     * @param request
     * @return
     * @throws IOException
     */
    public CheckoutResponse checkout(CheckoutRequest request) throws IOException {
        HashMap<String, Object> body = makeCheckoutRequest(request);
        Call<CheckoutResponse> call;
        switch (request.type) {
            case MOBILE:
                call = payment.mobileCheckout(body);
                break;
            case CARD:
                call = payment.cardCheckout(body);
                break;
            default:
                throw new IOException("Invalid checkout type");
        }
        Response<CheckoutResponse> res = call.execute();
        return res.body();
    }

    /**
     *
     * @param request
     * @param callback
     */
    public void checkout(CheckoutRequest request, Callback<CheckoutResponse> callback) {
        HashMap<String, Object> body = makeCheckoutRequest(request);
        Call<CheckoutResponse> call;
        switch (request.type) {
            case MOBILE:
                call = payment.mobileCheckout(body);
                break;
            case CARD:
                call = payment.cardCheckout(body);
                break;
            default:
                callback.onFailure(new IOException("Invalid checkout type"));
                return;
        }
        call.enqueue(makeCallback(callback));
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
