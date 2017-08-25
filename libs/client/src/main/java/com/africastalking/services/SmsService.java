package com.africastalking.services;

import android.text.TextUtils;

import com.africastalking.utils.Callback;
import com.africastalking.utils.Environment;
import com.africastalking.models.FetchMessageResponse;
import com.africastalking.models.SendMessageResponse;
import com.africastalking.models.SubscriptionResponse;
import com.africastalking.models.Subscriptions;
import com.africastalking.proto.SdkServerServiceOuterClass;

import java.io.IOException;
import java.util.Arrays;

import retrofit2.Response;


public final class SmsService extends Service {

    static SmsService sInstance;
    private SmsServiceInterface sms;

    SmsService() throws IOException {
        super();
    }

    @Override
    protected void fetchToken(String host, int port) throws IOException {
        fetchServiceToken(host, port, SdkServerServiceOuterClass.ClientTokenRequest.Capability.SMS);
    }

    @Override
    protected SmsService getInstance() throws IOException {

        if(sInstance == null){
            sInstance = new SmsService();
        }

        return sInstance;
    }

    @Override
    protected boolean isInitialized() {
        return sInstance != null;
    }

    @Override
    protected void initService() {
        String baseUrl = "https://api."+ (ENV == Environment.SANDBOX ? SANDBOX_DOMAIN : PRODUCTION_DOMAIN) + "/version1/";
        sms =  retrofitBuilder.baseUrl(baseUrl).build().create(SmsServiceInterface.class) ;
    }

    @Override
    protected void destroyService() {
        if(sInstance != null){
            sInstance = null;
        }
    }

    private String formatRecipients(String[] recipients) {

        if (recipients == null){
            return null;
        }

        if (recipients.length == 1) {
            return recipients[0];
        }

        return TextUtils.join(",", Arrays.asList(recipients));

    }

    // -> Normal

    /**
     * Send a message
     * <p>
     *     Synchronously send the request and return its response.
     * </p>
     */
    public SendMessageResponse send(String message, String from, String[] recipients) throws IOException {
        Response<SendMessageResponse> resp = sms.send(USERNAME, formatRecipients(recipients), from, message).execute();
        return resp.body();
    }

    /**
     * Send a message
     * <p>
     *     Asynchronously send the request and notify {@code callback} of its response or if an error
     * occurred
     * </p>
     */
    public void send(String message, String from, String[] recipients, Callback<SendMessageResponse> callback) {
        sms.send(USERNAME, formatRecipients(recipients), from, message).enqueue(makeCallback(callback));
    }

    /**
     * Send a message
     * <p>
     *     Synchronously send the request and return its response.
     * </p>
     */
    public SendMessageResponse send(String message, String[] recipients) throws IOException {
        return send(message, null, recipients);
    }


    /**
     * Send a message
     * <p>
     *     Asynchronously send the request and notify {@code callback} of its response or if an error
     * occurred
     * </p>
     */
    public void send(String message, String[] recipients, Callback<SendMessageResponse> callback) {
        send(message, null, recipients, callback);
    }


    // -> Bulk

    /**
     * Send a message in bulk
     * <p>
     *     Synchronously send the request and return its response.
     * </p>
     */
    public SendMessageResponse sendBulk(String message, String from, boolean enqueue, String[] recipients) throws IOException {
        Response<SendMessageResponse> resp = sms.sendBulk(
                USERNAME,
                formatRecipients(recipients),
                from,
                message,
                1,
                enqueue ? "1" : null).execute();
        return resp.body();
    }

    /**
     * Send a message in bulk
     * <p>
     *     Asynchronously send the request and notify {@code callback} of its response or if an error
     * occurred
     * </p>
     */
    public void sendBulk(String message, String from, boolean enqueue, String[] recipients, Callback<SendMessageResponse> callback) {
        sms.sendBulk(USERNAME,
                formatRecipients(recipients),
                from,
                message,
                1,
                enqueue ? "1" : null).enqueue(makeCallback(callback));
    }

    /**
     * Send a message in bulk
     * <p>
     *     Synchronously send the request and return its response.
     * </p>
     */
    public SendMessageResponse sendBulk(String message, String from, String[] recipients) throws IOException {
        return sendBulk(message, from, false, recipients);
    }

    /**
     * Send a message in bulk
     * <p>
     *     Asynchronously send the request and notify {@code callback} of its response or if an error
     * occurred
     * </p>
     */
    public void sendBulk(String message, String from, String[] recipients, Callback<SendMessageResponse> callback) {
        sendBulk(message, from, false, recipients, callback);
    }

    /**
     * Send a message in bulk
     * <p>
     *     Synchronously send the request and return its response.
     * </p>
     */
    public SendMessageResponse sendBulk(String message, boolean enqueue, String[] recipients) throws IOException {
        return sendBulk(message, null, enqueue, recipients);
    }

    /**
     * Send a message in bulk
     * <p>
     *     Asynchronously send the request and notify {@code callback} of its response or if an error
     * occurred
     * </p>
     */
    public void sendBulk(String message, boolean enqueue, String[] recipients, Callback<SendMessageResponse> callback) {
        sendBulk(message, null, enqueue, recipients, callback);
    }

    /**
     * Send a message in bulk
     * <p>
     *     Synchronously send the request and return its response.
     * </p>
     */
    public SendMessageResponse sendBulk(String message, String[] recipients) throws IOException {
        return sendBulk(message, null, false, recipients);
    }

    /**
     * Send a message in bulk
     * <p>
     *     Asynchronously send the request and notify {@code callback} of its response or if an error
     * occurred
     * </p>
     */
    public void sendBulk(String message, String[] recipients, Callback<SendMessageResponse> callback) {
        sendBulk(message, null, false, recipients, callback);
    }


    // -> Premium

    /**
     * Send premium SMS
     * <p>
     *     Synchronously send the request and return its response.
     * </p>
     */
    public SendMessageResponse sendPremium(String message, String from, String keyword, String linkId, long retryDurationInHours, String[] recipients) throws IOException {
        String retryDuration = retryDurationInHours <= 0 ? null : String.valueOf(retryDurationInHours);
        Response<SendMessageResponse> resp = sms.sendPremium(USERNAME, formatRecipients(recipients), from, message, keyword, linkId, retryDuration).execute();
        return resp.body();
    }

    /**
     * Send premium SMS
     * <p>
     *     Asynchronously send the request and notify {@code callback} of its response or if an error
     * occurred
     * </p>
     */
    public void sendPremium(String message, String from, String keyword, String linkId, long retryDurationInHours, String[] recipients, Callback<SendMessageResponse> callback) {
        String retryDuration = retryDurationInHours <= 0 ? null : String.valueOf(retryDurationInHours);
        sms.sendPremium(USERNAME, formatRecipients(recipients),
                from, message, keyword, linkId, retryDuration)
                .enqueue(makeCallback(callback));
    }

    /**
     * Send premium SMS
     * <p>
     *     Synchronously send the request and return its response.
     * </p>
     */
    public SendMessageResponse sendPremium(String message, String keyword, String linkId, long retryDurationInHours, String[] recipients) throws IOException {
        return sendPremium(message, null, keyword, linkId, retryDurationInHours, recipients);
    }

    /**
     * Send premium SMS
     * <p>
     *     Asynchronously send the request and notify {@code callback} of its response or if an error
     * occurred
     * </p>
     */
    public void sendPremium(String message, String keyword, String linkId, long retryDurationInHours, String[] recipients, Callback<SendMessageResponse> callback){
        sendPremium(message, null, keyword, linkId, retryDurationInHours, recipients, callback);
    }

    /**
     * Send Premium SMS
     * <p>
     *     Synchronously send the request and return its response.
     * </p>
     */
    public SendMessageResponse sendPremium(String message, String from, String keyword, String linkId, String[] recipients) throws IOException {
        return sendPremium(message, from, keyword, linkId, -1, recipients);
    }

    /**
     * Send premium SMS
     * <p>
     *     Asynchronously send the request and notify {@code callback} of its response or if an error
     * occurred
     * </p>
     */
    public void sendPremium(String message, String from, String keyword, String linkId, String[] recipients, Callback<SendMessageResponse> callback){
        sendPremium(message, from, keyword, linkId, -1, recipients, callback);
    }

    /**
     * Send premium SMS
     * <p>
     *     Synchronously send the request and return its response.
     * </p>
     */
    public SendMessageResponse sendPremium(String message, String keyword, String linkId, String[] recipients) throws IOException {
        return sendPremium(message, null, keyword, linkId, -1, recipients);
    }

    /**
     * Send premium SMS
     * <p>
     *     Asynchronously send the request and notify {@code callback} of its response or if an error
     * occurred
     * </p>
     */
    public void sendPremium(String message, String keyword, String linkId, String[] recipients, Callback<SendMessageResponse> callback){
        sendPremium(message, null, keyword, linkId, -1, recipients, callback);
    }

    // -> Fetch Message

    /**
     * Fetch messages
     * <p>
     *     Synchronously send the request and return its response.
     * </p>
     */
    public FetchMessageResponse fetchMessage(String lastReceivedId) throws IOException {
        Response<FetchMessageResponse> resp = sms.fetchMessage(USERNAME, lastReceivedId).execute();
        return resp.body();
    }

    /**
     * Fetch messages
     * <p>
     *     Synchronously send the request and return its response.
     * </p>
     */
    public FetchMessageResponse fetchMessage() throws IOException {
        return fetchMessage("0");
    }

    /**
     * Fetch messages
     * <p>
     *     Asynchronously send the request and notify {@code callback} of its response or if an error
     * occurred
     * </p>
     */
    public void fetchMessage(String lastReceivedId, Callback<FetchMessageResponse> callback) {
        sms.fetchMessage(USERNAME, lastReceivedId).enqueue(makeCallback(callback));
    }

    /**
     * Fetch messages
     * <p>
     *     Asynchronously send the request and notify {@code callback} of its response or if an error
     * occurred
     * </p>
     */
    public void fetchMessage(Callback<FetchMessageResponse> callback) {
        fetchMessage("0", callback);
    }

    // -> Fetch Subscription

    /**
     * Fetch subscriptions
     * <p>
     *     Synchronously send the request and return its response.
     * </p>
     */
    public Subscriptions fetchSubscription(String shortCode, String keyword, String lastReceivedId) throws IOException {
        Response<Subscriptions> resp = sms.fetchSubsciption(USERNAME, shortCode, keyword, lastReceivedId).execute();
        return resp.body();
    }

    /**
     * Fetch subscription
     * <p>
     *     Asynchronously send the request and notify {@code callback} of its response or if an error
     * occurred
     * </p>
     */
    public void fetchSubscription(String shortCode, String keyword, String lastReceivedId, Callback<Subscriptions> callback) {
        sms.fetchSubsciption(USERNAME, shortCode, keyword, lastReceivedId).enqueue(makeCallback(callback));

    }

    /**
     * Fetch subscriptions
     * <p>
     *     Synchronously send the request and return its response.
     * </p>
     */
    public Subscriptions fetchSubscription(String shortCode, String keyword) throws IOException {
        return fetchSubscription(shortCode, keyword, "0");
    }


    /**
     * Create subscription
     * <p>
     *     Asynchronously send the request and notify {@code callback} of its response or if an error
     * occurred
     * </p>
     */
    public void fetchSubscription(String shortCode, String keyword, Callback<Subscriptions> callback) {
        fetchSubscription(shortCode, keyword, "0", callback);
    }

    // -> Create subscription

    /**
     * Create subscription
     * <p>
     *     Synchronously send the request and return its response.
     * </p>
     */
    public SubscriptionResponse createSubscription(String shortCode, String keyword, String phoneNumber) throws IOException {
        Response<SubscriptionResponse> resp = sms.createSubscription(USERNAME, shortCode, keyword, phoneNumber).execute();
        return resp.body();
    }

    /**
     * Create subscription
     * <p>
     *     Asynchronously send the request and notify {@code callback} of its response or if an error
     * occurred
     * </p>
     */
    public void createSubscription(String shortCode, String keyword, String phoneNumber, Callback<SubscriptionResponse> callback) {
        sms.createSubscription(USERNAME, shortCode, keyword, phoneNumber).enqueue(makeCallback(callback));
    }
}
