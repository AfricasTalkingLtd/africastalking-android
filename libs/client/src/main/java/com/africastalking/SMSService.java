package com.africastalking;

import android.text.TextUtils;

import com.africastalking.interfaces.ISMS;
import com.africastalking.models.Message;
import com.africastalking.models.Recipient;
import com.africastalking.models.SendMessageResponse;
import com.africastalking.models.Subscription;
import com.africastalking.models.SubscriptionResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
//import java.util.StringJoiner;

import retrofit2.Response;


public final class SMSService extends Service {

    private static SMSService smsService;
    private ISMS sms;
    private String callType = "server";

    SMSService(String username, Format format, Currency currency) {
        super(username, format, currency);
    }

    SMSService(){
        super();
    }

    @Override
    protected SMSService getInstance(String username, Format format, Currency currency) {

        if(smsService == null){
            smsService = new SMSService(username, format, currency);
        }

        return smsService;
    }

    @Override
    protected boolean isInitialized() {
        return smsService != null;
    }

    @Override
    protected void initService() {
        String baseUrl = "https://api."+ (AfricasTalking.ENV == Environment.SANDBOX ? Const.SANDBOX_DOMAIN : Const.PRODUCTION_DOMAIN) + "/version1/";
        sms =  retrofitBuilder.baseUrl(baseUrl).build().create(ISMS.class) ;
    }

    @Override
    protected void destroyService() {
        if(smsService != null){
            smsService = null;
        }
    }

    public String formatRecipients(String[] recipients) {

        if (recipients == null){
            return null;
        }

        if (recipients.length == 1) {
            return recipients[0];
        }

        return TextUtils.join(",", Arrays.asList(recipients));

    }

    public void setCallType(String callType){
        this.callType = callType;
    }

    // -> Normal

    /**
     * Send a message
     * <p>
     *     Synchronously send the request and return its response.
     * </p>
     */
    public SendMessageResponse send(String message, String from, String[] recipients) throws IOException {
        Response<SendMessageResponse> resp = sms.send(username, formatRecipients(recipients), from, message).execute();
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
        sms.send(username, formatRecipients(recipients), from, message).enqueue(makeCallback(callback));
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
        Response<SendMessageResponse> resp = sms.sendBulk(username,
                formatRecipients(recipients), from, message,
                1, enqueue ? "1":null).execute();
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
        sms.sendBulk(username,
                formatRecipients(recipients), from, message,
                1, enqueue ? "1":null).enqueue(makeCallback(callback));
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
        Response<SendMessageResponse> resp = sms.sendPremium(username, formatRecipients(recipients), from, message, keyword, linkId, retryDuration).execute();
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
        sms.sendPremium(username, formatRecipients(recipients),
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
    public List<Message> fetchMessage(String lastReceivedId) throws IOException {
        Response<List<Message>> resp = sms.fetchMessage(username, lastReceivedId).execute();
        return resp.body();
    }

    /**
     * Fetch messages
     * <p>
     *     Synchronously send the request and return its response.
     * </p>
     */
    public List<Message> fetchMessage() throws IOException {
        return fetchMessage("0");
    }

    /**
     * Fetch messages
     * <p>
     *     Asynchronously send the request and notify {@code callback} of its response or if an error
     * occurred
     * </p>
     */
    public void fetchMessage(String lastReceivedId, Callback<List<Message>> callback) {
        sms.fetchMessage(username, lastReceivedId).enqueue(makeCallback(callback));
    }

    /**
     * Fetch messages
     * <p>
     *     Asynchronously send the request and notify {@code callback} of its response or if an error
     * occurred
     * </p>
     */
    public void fetchMessage(Callback<List<Message>> callback) {
        fetchMessage("0", callback);
    }

    // -> Fetch Subscription

    /**
     * Fetch subscriptions
     * <p>
     *     Synchronously send the request and return its response.
     * </p>
     */
    public List<Subscription> fetchSubscription(String shortCode, String keyword, String lastReceivedId) throws IOException {
        Response<List<Subscription>> resp = sms.fetchSubsciption(username, shortCode, keyword, lastReceivedId).execute();
        return resp.body();
    }

    /**
     * Fetch subscription
     * <p>
     *     Asynchronously send the request and notify {@code callback} of its response or if an error
     * occurred
     * </p>
     */
    public void fetchSubscription(String shortCode, String keyword, String lastReceivedId, Callback<List<Subscription>> callback) {
        sms.fetchSubsciption(username, shortCode, keyword, lastReceivedId).enqueue(makeCallback(callback));

    }

    /**
     * Fetch subscriptions
     * <p>
     *     Synchronously send the request and return its response.
     * </p>
     */
    public List<Subscription> fetchSubscription(String shortCode, String keyword) throws IOException {
        return fetchSubscription(shortCode, keyword, "0");
    }


    /**
     * Create subscription
     * <p>
     *     Asynchronously send the request and notify {@code callback} of its response or if an error
     * occurred
     * </p>
     */
    public void fetchSubscription(String shortCode, String keyword, Callback<List<Subscription>> callback) {
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
        Response<SubscriptionResponse> resp = sms.createSubscription(username, shortCode, keyword, phoneNumber).execute();
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
        sms.createSubscription(username, shortCode, keyword, phoneNumber).enqueue(makeCallback(callback));
    }
}
