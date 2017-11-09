package com.africastalking.services;

import android.text.TextUtils;

import com.africastalking.models.sms.FetchSubscriptionResponse;
import com.africastalking.models.sms.Message;
import com.africastalking.models.sms.Recipient;
import com.africastalking.models.sms.Subscription;
import com.africastalking.utils.Callback;
import com.africastalking.models.sms.FetchMessageResponse;
import com.africastalking.models.sms.SendMessageResponse;
import com.africastalking.models.sms.SubscriptionResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import retrofit2.Response;


public final class SmsService extends Service {

    static SmsService sInstance;
    private SmsServiceInterface sms;

    SmsService() throws IOException {
        super();
    }

    @Override
    protected SmsService getInstance() throws IOException {

        if (sInstance == null) {
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
        String baseUrl = "https://api." + (isSandbox ? SANDBOX_DOMAIN : PRODUCTION_DOMAIN) + "/version1/";
        sms = retrofitBuilder.baseUrl(baseUrl).build().create(SmsServiceInterface.class);
    }

    @Override
    protected void destroyService() {
        if (sInstance != null) {
            sInstance = null;
        }
    }

    private String formatRecipients(String[] recipients) {

        if (recipients == null) {
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
     * Synchronously send the request and return its response.
     * </p>
     */
    public List<Recipient> send(String message, String from, String[] recipients) throws IOException {
        Response<SendMessageResponse> resp = sms.send(username, formatRecipients(recipients), from, message).execute();
        if (resp.isSuccessful()) {
            try {
                return resp.body().data.recipients;
            } catch (NullPointerException npe) {
            }
        }
        throw new IOException(resp.message());
    }

    /**
     * Send a message
     * <p>
     * Asynchronously send the request and notify {@code callback} of its response or if an error
     * occurred
     * </p>
     */
    public void send(String message, String from, String[] recipients, final Callback<List<Recipient>> callback) {
        sms.send(username, formatRecipients(recipients), from, message).enqueue(makeCallback(new Callback<SendMessageResponse>() {
            @Override
            public void onSuccess(SendMessageResponse data) {
                if (data != null) {
                    callback.onSuccess(data.data.recipients);
                } else {
                    callback.onFailure(new Exception("Invalid API response"));
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                callback.onFailure(throwable);
            }
        }));
    }

    /**
     * Send a message
     * <p>
     * Synchronously send the request and return its response.
     * </p>
     */
    public List<Recipient> send(String message, String[] recipients) throws IOException {
        return send(message, null, recipients);
    }


    /**
     * Send a message
     * <p>
     * Asynchronously send the request and notify {@code callback} of its response or if an error
     * occurred
     * </p>
     */
    public void send(String message, String[] recipients, Callback<List<Recipient>> callback) {
        send(message, null, recipients, callback);
    }


    // -> Bulk

    /**
     * Send a message in bulk
     * <p>
     * Synchronously send the request and return its response.
     * </p>
     */
    public List<Recipient> sendBulk(String message, String from, boolean enqueue, String[] recipients) throws IOException {
        Response<SendMessageResponse> resp = sms.sendBulk(
                username,
                formatRecipients(recipients),
                from,
                message,
                1,
                enqueue ? "1" : null).execute();
        if (resp.isSuccessful()) {
            try {
                return resp.body().data.recipients;
            } catch (NullPointerException npe) {
            }
        }
        throw new IOException(resp.message());
    }

    /**
     * Send a message in bulk
     * <p>
     * Asynchronously send the request and notify {@code callback} of its response or if an error
     * occurred
     * </p>
     */
    public void sendBulk(String message, String from, boolean enqueue, String[] recipients, final Callback<List<Recipient>> callback) {
        sms.sendBulk(username,
                formatRecipients(recipients),
                from,
                message,
                1,
                enqueue ? "1" : null).enqueue(makeCallback(new Callback<SendMessageResponse>() {

                @Override
                public void onSuccess(SendMessageResponse data) {
                    if (data != null) {
                        callback.onSuccess(data.data.recipients);
                    } else {
                        callback.onFailure(new Exception("Invalid API response"));
                    }
                }

                @Override
                public void onFailure(Throwable throwable) {
                    callback.onFailure(throwable);
                }
        }));
    }

    /**
     * Send a message in bulk
     * <p>
     * Synchronously send the request and return its response.
     * </p>
     */
    public List<Recipient> sendBulk(String message, String from, String[] recipients) throws IOException {
        return sendBulk(message, from, false, recipients);
    }

    /**
     * Send a message in bulk
     * <p>
     * Asynchronously send the request and notify {@code callback} of its response or if an error
     * occurred
     * </p>
     */
    public void sendBulk(String message, String from, String[] recipients, Callback<List<Recipient>> callback) {
        sendBulk(message, from, false, recipients, callback);
    }

    /**
     * Send a message in bulk
     * <p>
     * Synchronously send the request and return its response.
     * </p>
     */
    public List<Recipient> sendBulk(String message, boolean enqueue, String[] recipients) throws IOException {
        return sendBulk(message, null, enqueue, recipients);
    }

    /**
     * Send a message in bulk
     * <p>
     * Asynchronously send the request and notify {@code callback} of its response or if an error
     * occurred
     * </p>
     */
    public void sendBulk(String message, boolean enqueue, String[] recipients, Callback<List<Recipient>> callback) {
        sendBulk(message, null, enqueue, recipients, callback);
    }

    /**
     * Send a message in bulk
     * <p>
     * Synchronously send the request and return its response.
     * </p>
     */
    public List<Recipient> sendBulk(String message, String[] recipients) throws IOException {
        return sendBulk(message, null, false, recipients);
    }

    /**
     * Send a message in bulk
     * <p>
     * Asynchronously send the request and notify {@code callback} of its response or if an error
     * occurred
     * </p>
     */
    public void sendBulk(String message, String[] recipients, Callback<List<Recipient>> callback) {
        sendBulk(message, null, false, recipients, callback);
    }


    // -> Premium

    /**
     * Send premium SMS
     * <p>
     * Synchronously send the request and return its response.
     * </p>
     */
    public List<Recipient> sendPremium(String message, String from, String keyword, String linkId, long retryDurationInHours, String[] recipients) throws IOException {
        String retryDuration = retryDurationInHours <= 0 ? null : String.valueOf(retryDurationInHours);
        Response<SendMessageResponse> resp = sms.sendPremium(username, formatRecipients(recipients), from, message, keyword, linkId, retryDuration, 0).execute();
        if (resp.isSuccessful()) {
            try {
                return resp.body().data.recipients;
            } catch (NullPointerException npe) { }
        }
        throw new IOException(resp.message());
    }

    /**
     * Send premium SMS
     * <p>
     * Asynchronously send the request and notify {@code callback} of its response or if an error
     * occurred
     * </p>
     */
    public void sendPremium(String message, String from, String keyword, String linkId, long retryDurationInHours, String[] recipients, final Callback<List<Recipient>> callback) {
        String retryDuration = retryDurationInHours <= 0 ? null : String.valueOf(retryDurationInHours);
        sms.sendPremium(username, formatRecipients(recipients),
                from, message, keyword, linkId, retryDuration, 0)
                .enqueue(makeCallback(new Callback<SendMessageResponse>() {
                    @Override
                    public void onSuccess(SendMessageResponse data) {
                        if (data != null) {
                            callback.onSuccess(data.data.recipients);
                        } else {
                            callback.onFailure(new Exception("Invalid API response"));
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        callback.onFailure(throwable);
                    }
                }));
    }

    /**
     * Send premium SMS
     * <p>
     * Synchronously send the request and return its response.
     * </p>
     */
    public List<Recipient> sendPremium(String message, String keyword, String linkId, long retryDurationInHours, String[] recipients) throws IOException {
        return sendPremium(message, null, keyword, linkId, retryDurationInHours, recipients);
    }

    /**
     * Send premium SMS
     * <p>
     * Asynchronously send the request and notify {@code callback} of its response or if an error
     * occurred
     * </p>
     */
    public void sendPremium(String message, String keyword, String linkId, long retryDurationInHours, String[] recipients, Callback<List<Recipient>> callback) {
        sendPremium(message, null, keyword, linkId, retryDurationInHours, recipients, callback);
    }

    /**
     * Send Premium SMS
     * <p>
     * Synchronously send the request and return its response.
     * </p>
     */
    public List<Recipient> sendPremium(String message, String from, String keyword, String linkId, String[] recipients) throws IOException {
        return sendPremium(message, from, keyword, linkId, -1, recipients);
    }

    /**
     * Send premium SMS
     * <p>
     * Asynchronously send the request and notify {@code callback} of its response or if an error
     * occurred
     * </p>
     */
    public void sendPremium(String message, String from, String keyword, String linkId, String[] recipients, Callback<List<Recipient>> callback) {
        sendPremium(message, from, keyword, linkId, -1, recipients, callback);
    }

    /**
     * Send premium SMS
     * <p>
     * Synchronously send the request and return its response.
     * </p>
     */
    public List<Recipient> sendPremium(String message, String keyword, String linkId, String[] recipients) throws IOException {
        return sendPremium(message, null, keyword, linkId, -1, recipients);
    }

    /**
     * Send premium SMS
     * <p>
     * Asynchronously send the request and notify {@code callback} of its response or if an error
     * occurred
     * </p>
     */
    public void sendPremium(String message, String keyword, String linkId, String[] recipients, Callback<List<Recipient>> callback) {
        sendPremium(message, null, keyword, linkId, -1, recipients, callback);
    }

    // -> Fetch Message

    /**
     * Fetch messages
     * <p>
     * Synchronously send the request and return its response.
     * </p>
     */
    public List<Message> fetchMessage(String lastReceivedId) throws IOException {
        Response<FetchMessageResponse> resp = sms.fetchMessage(username, lastReceivedId).execute();
        if (resp.isSuccessful()) {
            try {
                return resp.body().data.messages;
            } catch (NullPointerException npe) { }
        }
        throw new IOException(resp.message());
    }

    /**
     * Fetch messages
     * <p>
     * Synchronously send the request and return its response.
     * </p>
     */
    public List<Message> fetchMessage() throws IOException {
        return fetchMessage("0");
    }

    /**
     * Fetch messages
     * <p>
     * Asynchronously send the request and notify {@code callback} of its response or if an error
     * occurred
     * </p>
     */
    public void fetchMessage(String lastReceivedId, final Callback<List<Message>> callback) {
        sms.fetchMessage(username, lastReceivedId).enqueue(makeCallback(new Callback<FetchMessageResponse>() {
            @Override
            public void onSuccess(FetchMessageResponse data) {
                if (data != null) {
                    callback.onSuccess(data.data.messages);
                } else {
                    callback.onFailure(new Exception("Invalid API response"));
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                callback.onFailure(throwable);
            }
        }));
    }

    /**
     * Fetch messages
     * <p>
     * Asynchronously send the request and notify {@code callback} of its response or if an error
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
     * Synchronously send the request and return its response.
     * </p>
     */
    public List<Subscription> fetchSubscription(String shortCode, String keyword, String lastReceivedId) throws IOException {
        Response<FetchSubscriptionResponse> resp = sms.fetchSubscription(username, shortCode, keyword, lastReceivedId).execute();
        if (resp.isSuccessful()) {
            try {
                return resp.body().subscriptions;
            } catch (NullPointerException npe) { }
        }
        throw new IOException(resp.message());
    }

    /**
     * Fetch subscription
     * <p>
     * Asynchronously send the request and notify {@code callback} of its response or if an error
     * occurred
     * </p>
     */
    public void fetchSubscription(String shortCode, String keyword, String lastReceivedId, final Callback<List<Subscription>> callback) {
        sms.fetchSubscription(username, shortCode, keyword, lastReceivedId).enqueue(makeCallback(new Callback<FetchSubscriptionResponse>() {
            @Override
            public void onSuccess(FetchSubscriptionResponse data) {
                if (data != null) {
                    callback.onSuccess(data.subscriptions);
                } else {
                    callback.onFailure(new Exception("Invalid API Response"));
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                callback.onFailure(throwable);
            }
        }));
    }

    /**
     * Fetch subscriptions
     * <p>
     * Synchronously send the request and return its response.
     * </p>
     */
    public List<Subscription> fetchSubscription(String shortCode, String keyword) throws IOException {
        return fetchSubscription(shortCode, keyword, "0");
    }


    /**
     * Create subscription
     * <p>
     * Asynchronously send the request and notify {@code callback} of its response or if an error
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
     * Synchronously send the request and return its response.
     * </p>
     */
    public SubscriptionResponse createSubscription(String shortCode, String keyword, String phoneNumber, String checkoutToken) throws IOException {
        Response<SubscriptionResponse> resp = sms.createSubscription(username, shortCode, keyword, phoneNumber, checkoutToken).execute();
        return resp.body();
    }

    /**
     * Create subscription
     * <p>
     * Asynchronously send the request and notify {@code callback} of its response or if an error
     * occurred
     * </p>
     */
    public void createSubscription(String shortCode, String keyword, String phoneNumber, String checkoutToken, Callback<SubscriptionResponse> callback) {
        sms.createSubscription(username, shortCode, keyword, phoneNumber, checkoutToken).enqueue(makeCallback(callback));
    }
}
