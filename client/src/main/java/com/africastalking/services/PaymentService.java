package com.africastalking.services;

import com.africastalking.models.payment.checkout.BankCheckoutRequest;
import com.africastalking.models.payment.checkout.CardCheckoutRequest;
import com.africastalking.models.payment.checkout.CheckoutRequest;
import com.africastalking.models.payment.checkout.CheckoutValidateRequest;
import com.africastalking.models.payment.checkout.CheckoutValidationResponse;
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
import java.util.HashMap;
import java.util.List;

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

        body.put("username", username);
        body.put("productName", request.productName);
        body.put("amount", request.amount);
        body.put("currencyCode", request.currencyCode);
        body.put("metadata", request.metadata);
        if (request.narration != null) {
            body.put("narration", request.narration);
        }

        switch (request.type) {
            case MOBILE:
                MobileCheckoutRequest mobileRequest = (MobileCheckoutRequest) request;
                body.put("phoneNumber", mobileRequest.phoneNumber);
                break;
            case CARD:
                CardCheckoutRequest cardRequest = (CardCheckoutRequest) request;
                if (cardRequest.checkoutToken != null) {
                    body.put("checkoutToken", cardRequest.checkoutToken);
                } else {
                    body.put("paymentCard", cardRequest.paymentCard);
                }
                break;
            case BANK:
                BankCheckoutRequest bankRequest = (BankCheckoutRequest) request;
                body.put("bankAccount", bankRequest.bankAccount);
                break;
        }

        return body;
    }

    private HashMap<String, Object> makeCheckoutValidationRequest(CheckoutValidateRequest request) {
        HashMap<String, Object> body = new HashMap<>();
        body.put("transactionId", request.transactionId);
        body.put("otp", request.token);
        body.put("username", username);
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
                call = payment.cardCheckoutCharge(body);
                break;
            case BANK:
                call = payment.bankCheckoutCharge(body);
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
                call = payment.cardCheckoutCharge(body);
                break;
            case BANK:
                call = payment.bankCheckoutCharge(body);
                break;
            default:
                callback.onFailure(new IOException("Invalid checkout type"));
                return;
        }
        call.enqueue(makeCallback(callback));
    }

    /**
     *
     * @param request
     * @return
     * @throws IOException
     */
    public CheckoutValidationResponse validateCheckout(CheckoutValidateRequest request) throws IOException {
        HashMap<String, Object> body = makeCheckoutValidationRequest(request);
        Call<CheckoutValidationResponse> call;
        switch (request.type) {
            case CARD:
                call = payment.cardCheckoutValidate(body);
                break;
            case BANK:
                call = payment.bankCheckoutValidate(body);
                break;
            default:
                throw new IOException("Invalid type: Only CARD and BANK are allowed");
        }
        Response<CheckoutValidationResponse> res = call.execute();
        return res.body();
    }

    /**
     *
     * @param request
     * @param callback
     */
    public void validateCheckout(CheckoutValidateRequest request, Callback<CheckoutValidationResponse> callback) {
        HashMap<String, Object> body = makeCheckoutValidationRequest(request);
        Call<CheckoutValidationResponse> call;
        switch (request.type) {
            case CARD:
                call = payment.cardCheckoutValidate(body);
                break;
            case BANK:
                call = payment.bankCheckoutValidate(body);
                break;
            default:
                callback.onFailure(new IOException("Invalid type: Only CARD and BANK are allowed"));
                return;
        }
        call.enqueue(makeCallback(callback));
    }

    public B2CResponse mobileB2C(String product, List<Consumer> recipients) throws IOException {
        HashMap<String, Object> body = makeB2CRequest(product, recipients);
        Call<B2CResponse> call = payment.requestB2C(body);
        Response<B2CResponse> res = call.execute();
        return res.body();
    }

    public void mobileB2C(String product, List<Consumer> recipients, Callback<B2CResponse> callback) {
        HashMap<String, Object> body = makeB2CRequest(product, recipients);
        Call<B2CResponse> call = payment.requestB2C(body);
        call.enqueue(makeCallback(callback));
    }



    public B2BResponse mobileB2B(String product, Business recipient) throws IOException {
        HashMap<String, Object> body = makeB2BRequest(product, recipient);
        Call<B2BResponse> call = payment.requestB2B(body);
        Response<B2BResponse> res = call.execute();
        return res.body();
    }

    public void mobileB2B(String product, Business recipient, Callback<B2BResponse> callback) {
        HashMap<String, Object> body = makeB2BRequest(product, recipient);
        Call<B2BResponse> call = payment.requestB2B(body);
        call.enqueue(makeCallback(callback));
    }
}
