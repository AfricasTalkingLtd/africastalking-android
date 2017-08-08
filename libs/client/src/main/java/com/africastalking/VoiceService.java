package com.africastalking;


import java.io.IOException;
import java.text.ParseException;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.sip.*;
import android.util.Log;

import com.africastalking.interfaces.IVoice;
import com.africastalking.models.QueueStatus;
import com.africastalking.proto.SdkServerServiceGrpc;
import com.africastalking.proto.SdkServerServiceOuterClass;
import com.africastalking.proto.SdkServerServiceOuterClass.SipCredentials;
import com.africastalking.proto.SdkServerServiceOuterClass.SipCredentialsRequest;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.grpc.ManagedChannel;
import retrofit2.Response;

public final class VoiceService extends Service {

    private static final String TAG = VoiceService.class.getName();

    private static final String SIP_CALL_ACTION = "com.africastalking.voice.internal.INCOMING_CALL";
    public static final String INCOMING_CALL = "com.africastalking.voice.INCOMING_CALL";


    public interface VoiceListener {
        void onError(Throwable error);
        void onRegistration();
    }

    private List<SipCredentials> mSipCredentials;
    private SipCredentials mCredentials;

    private SipManager mSipManager = null;
    private SipProfile mSipProfile = null;

    private Intent mActiveCallIntent = null;
    private SipAudioCall mActiveCall = null;

    private VoiceListener mCallback = null;

    private static VoiceService sInstance;
    private IVoice voice;



    VoiceService(Context context, VoiceListener listener, String username) throws IOException, SipException, ParseException {
        super();
        if (listener != null) {
            this.mCallback = listener;
        }

        ManagedChannel channel = Service.getChannel(AfricasTalking.HOST, AfricasTalking.PORT);
        SdkServerServiceGrpc.SdkServerServiceBlockingStub stub = SdkServerServiceGrpc.newBlockingStub(channel);

        SipCredentialsRequest req = SipCredentialsRequest.newBuilder().build();
        mSipCredentials = stub.getSipCredentials(req).getCredentialsList();

        initService(context, username);

        sInstance = this;
    }


    VoiceService(Context context, VoiceListener listener) throws Exception {
        this(context, listener, null);
    }


    @Override
    protected void fetchToken(String host, int port) throws IOException {
        fetchServiceToken(host, port, SdkServerServiceOuterClass.ClientTokenRequest.Capability.VOICE);
    }

    @Override
    protected VoiceService getInstance() throws IOException {
        if (sInstance == null) {
            throw new IOException("VoiceService not initialized");
        }
        return sInstance;
    }

    @Override
    protected boolean isInitialized() {
        return sInstance != null && mSipManager != null;
    }


    @Override
    protected void initService() {
        String baseUrl = "https://voice."+ (AfricasTalking.ENV == Environment.SANDBOX ? Const.SANDBOX_DOMAIN : Const.PRODUCTION_DOMAIN) + "/";
        voice =  retrofitBuilder.baseUrl(baseUrl).build().create(IVoice.class) ;
    }

    private void initService(Context context, String username) throws SipException, ParseException {
        initService();

        if (isAndroidSipAvailable(context)) {
            mSipManager = SipManager.newInstance(context);
            mSipProfile = createSipProfile(username);

        } else { // if android sip is not available, initialize PJSIP
            // TODO: PJSIP
            throw new RuntimeException("SIP is not supported on this device");
        }

        // Incoming call
        IntentFilter filter = new IntentFilter();
        filter.addAction(SIP_CALL_ACTION);
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mActiveCallIntent = intent;
                context.sendBroadcast(new Intent(INCOMING_CALL));
            }
        }, filter);

        mSipManager.open(mSipProfile, PendingIntent.getBroadcast(context, 0, new Intent(SIP_CALL_ACTION), Intent.FILL_IN_DATA), null);
        mSipManager.setRegistrationListener(mSipProfile.getUriString(), new SipRegistrationListener() {
            @Override
            public void onRegistering(String localProfileUri) {
                Log.d(TAG, "Reg. In Progress: " + localProfileUri);
            }

            @Override
            public void onRegistrationDone(String localProfileUri, long expiryTime) {
                Log.d(TAG, "Reg. Success: " + localProfileUri);
                mCallback.onRegistration();
            }

            @Override
            public void onRegistrationFailed(String localProfileUri, int errorCode, String errorMessage) {
                Log.d(TAG, "Reg. Error: " + localProfileUri  + " - " + errorCode + " - " + errorMessage);
                mCallback.onError(new Throwable(errorMessage, new Exception(String.format("Failed to register (%d): %s", errorCode, localProfileUri))));
            }
        });

    }

    private Boolean isAndroidSipAvailable(Context context) {
        return SipManager.isApiSupported(context) && SipManager.isVoipSupported(context);
    }

    private SipProfile createSipProfile(String defaultUsername) throws ParseException {

        if (mSipCredentials == null || mSipCredentials.size() == 0) {
            throw new RuntimeException("No SIP credentials found");
        }

        mCredentials = mSipCredentials.get(0);
        if (defaultUsername != null) {
            for (SipCredentials credentials : mSipCredentials) {
                if (credentials.getUsername().equals(defaultUsername)) {
                    mCredentials = credentials;
                    break;
                }
            }
        }

        String host = mCredentials.getHost();
        String username = mCredentials.getUsername();
        String password = mCredentials.getPassword();

        SipProfile.Builder builder =  new SipProfile.Builder(username, host);
        builder.setPassword(password);
        return builder.build();
    }

    private static boolean isSipUri(String uri) {
        String expression = "/^(sip:)?(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$/";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(uri);
        return matcher.matches();
    }

    @Override
    protected void destroyService() {
        if (mActiveCall != null) {
            try {
                mActiveCall.endCall();
            } catch (SipException e) { /* ignore */ }
        }

        if (mSipManager != null) {
            try {
                if (mSipProfile != null) {
                    mSipManager.close(mSipProfile.getUriString());
                }
            } catch (SipException e) { /* ignore */  }
        }
        sInstance = null;
    }

    /**
     * Initiate a voice call
     * @param destination
     * @param listener
     * @param timeout
     * @throws SipException
     */
    public void makeCall(String destination, SipAudioCall.Listener listener, int timeout) throws SipException {
        String peerUri = destination;
        if (isSipUri(peerUri)) {
            if (!peerUri.startsWith("sip:")) {
                peerUri = "sip:" + peerUri;
            }
        } else {
            try {
                SipProfile peer = new SipProfile.Builder(destination, mCredentials.getHost()).build();
                peerUri = peer.getUriString();
            } catch (ParseException ex) {
                throw new SipException("Invalid destination");
            }
        }
        Log.d(TAG, "Calling " + peerUri);
        mActiveCall = mSipManager.makeAudioCall(mSipProfile.getUriString(), peerUri, listener, timeout);
        mActiveCall.setListener(listener, true);
    }

    /**
     * Initiate a voice call
     * @param destination
     * @param listener
     * @throws Exception
     */
    public void makeCall(String destination, SipAudioCall.Listener listener) throws Exception {
        this.makeCall(destination, listener, 10000);
    }


    /**
     * End an active call
     * @throws SipException
     */
    public void endCall() throws SipException {
        if (mActiveCall != null) {
            mActiveCall.endCall();
            mActiveCall = null;
            mActiveCallIntent = null;
        }
    }


    /**
     * Send DTMF to active call
     * @param character
     */
    public void sendDtmf(char character) {
        if (mActiveCall != null && mActiveCall.isInCall()) {
            final int code;
            if (character =='*'){
                code = 10;
            } else if (character == '#'){
                code = 11;
            } else {
                try {
                    code = Integer.parseInt(String.valueOf(character));
                } catch (Exception ex){
                    // don't setup code to send if can't parse
                    return;
                }
            }
            mActiveCall.sendDtmf(code);
        }
    }


    /**
     * Pick up an incoming call
     * @param listener
     * @param timeout
     * @throws SipException
     */
    public void pickCall(SipAudioCall.Listener listener, int timeout) throws SipException {
        try {
            mActiveCall = mSipManager.takeAudioCall(mActiveCallIntent, listener);
            mActiveCall.answerCall(timeout);
        } catch (SipException e) {
            Log.e(TAG, "Failed to take audio call");
            e.printStackTrace();
        }
    }


    /**
     * Pick up an incoming call
     * @param listener
     * @throws SipException
     */
    public void pickCall(SipAudioCall.Listener listener) throws SipException {
        pickCall(listener, 30);
    }


    /**
     * Upload media file. This media file will be played when called upon by one of our voice actions.
     * @param url
     * @return
     * @throws IOException
     */
    public String mediaUpload(String url) throws IOException {
        Response<String> response = voice.mediaUpload(username, url).execute();
        return response.body();
    }

    public void mediaUpload(String url, Callback<String> callback) {
        voice.mediaUpload(username, url).enqueue(makeCallback(callback));
    }

    /**
     * Get queue status
     * @param phoneNumbers
     * @return
     * @throws IOException
     */
    public List<QueueStatus> queueStatus(String phoneNumbers) throws IOException {
        Response<List<QueueStatus>> response = voice.queueStatus(username, phoneNumbers).execute();
        return response.body();
    }

    public void queueStatus(String phoneNumbers, Callback<List<QueueStatus>> callback) {
        voice.queueStatus(username, phoneNumbers).enqueue(makeCallback(callback));
    }
}
