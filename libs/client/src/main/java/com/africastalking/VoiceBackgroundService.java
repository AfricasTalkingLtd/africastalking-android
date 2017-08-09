package com.africastalking;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.sip.*;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import com.africastalking.proto.SdkServerServiceGrpc;
import com.africastalking.proto.SdkServerServiceOuterClass.*;
import io.grpc.ManagedChannel;

import java.text.ParseException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public final class VoiceBackgroundService extends Service {

    private static final String TAG = VoiceBackgroundService.class.getName();

    public static final String EXTRA_USERNAME = "username";
    public static final String INCOMING_CALL = "com.africastalking.voice.INCOMING_CALL";
    private static final String SIP_CALL_ACTION = "com.africastalking.voice.internal.INCOMING_CALL";

    private static VoiceService mVoiceService;
    private static VoiceListener mRegistrationListener;
    private static SipAudioCall.Listener mCallListener;


    private List<SipCredentials> mSipCredentials;
    private SipCredentials mCredentials;
    private SipManager mSipManager;
    private SipProfile mSipProfile;
    private SipAudioCall mCall;

    private VoiceServiceBinder mBinder = new VoiceServiceBinder();

    public interface VoiceListener {
        void onError(Throwable error);
        void onRegistration();
    }

    public final class VoiceServiceBinder extends Binder {
        public VoiceBackgroundService getService() {
            return VoiceBackgroundService.this;
        }
    }

    private SipAudioCall.Listener mBaseCallListener = new SipAudioCall.Listener() {
        @Override
        public void onError(SipAudioCall call, int errorCode, String errorMessage) {
            Log.e(TAG, "Inbound Call Error:" + errorMessage);
            if (mCallListener != null) {
                mCallListener.onError(call, errorCode, errorMessage);
            }
        }

        @Override
        public void onRinging(SipAudioCall call, SipProfile caller) {
            Log.d(TAG, "Inbound Call Ringing");
            if (mCallListener != null) {
                mCallListener.onRinging(call, caller);
            }
        }

        @Override
        public void onRingingBack(SipAudioCall call) {
            Log.d(TAG, "Inbound Call Ring Back");
            if (mCallListener != null) {
                mCallListener.onRingingBack(call);
            }
        }

        @Override
        public void onCallBusy(SipAudioCall call) {
            Log.d(TAG, "Inbound Call Busy");
            if (mCallListener != null) {
                mCallListener.onCallBusy(call);
            }
        }

        @Override
        public void onCallEnded(SipAudioCall call) {
            Log.d(TAG, "Inbound Call Ended");
            if (mCallListener != null) {
                mCallListener.onCallEnded(call);
            }

            mCall.setListener(null);
            mCall = null;
        }

        @Override
        public void onCallEstablished(SipAudioCall call) {
            Log.d(TAG, "Inbound Call Established");
            if (mCallListener != null) {
                mCallListener.onCallEstablished(call);
            }
        }

        @Override
        public void onCallHeld(SipAudioCall call) {
            Log.d(TAG, "Inbound Call Held");
            if (mCallListener != null) {
                mCallListener.onCallHeld(call);
            }
        }

        @Override
        public void onCalling(SipAudioCall call) {
            Log.d(TAG, "Inbound Call Calling");
            if (mCallListener != null) {
                mCallListener.onCalling(call);
            }
        }

        @Override
        public void onChanged(SipAudioCall call) {
            Log.d(TAG, "Inbound Call Changed");
            if (mCallListener != null) {
                mCallListener.onChanged(call);
            }
        }

        @Override
        public void onReadyToCall(SipAudioCall call) {
            Log.d(TAG, "Inbound Call Ready To Call");
            if (mCallListener != null) {
                mCallListener.onReadyToCall(call);
            }
        }
    };


    private BroadcastReceiver mCallReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                mCall = mSipManager.takeAudioCall(intent, mBaseCallListener);
            } catch (SipException e) {
                e.printStackTrace();
            }

            // if bound, notify; if not broadcast
            context.sendBroadcast(new Intent(INCOMING_CALL));
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        String username = intent == null ? null : intent.getStringExtra(EXTRA_USERNAME);
        if (username != null) {
            // Re-init if new username

            if (mCall != null) {
                try {
                    mCall.endCall();
                } catch (SipException e) { /* ignore */ }
            }

            if (mSipManager != null) {
                try {
                    if (mSipProfile != null) {
                        mSipManager.close(mSipProfile.getUriString());
                    }
                } catch (SipException e) { /* ignore */  }
            }

            try {
                initializeSIP(intent);
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (SipException e) {
                e.printStackTrace();
            }

        }
        return mBinder;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (mVoiceService == null || mSipManager == null) {
            try {

                ManagedChannel channel = com.africastalking.Service.getChannel(AfricasTalking.HOST, AfricasTalking.PORT);
                SdkServerServiceGrpc.SdkServerServiceBlockingStub stub = SdkServerServiceGrpc.newBlockingStub(channel);

                SipCredentialsRequest req = SipCredentialsRequest.newBuilder().build();
                mSipCredentials = stub.getSipCredentials(req).getCredentialsList();

                initializeSIP(intent);

            } catch (Exception e) {
                Log.e(TAG, e.getMessage() + "");
                mVoiceService = null;
            }
        }


        // If we get killed, after returning from here, restart
        return START_STICKY;
    }


    private void initializeSIP(Intent intent) throws ParseException, SipException {

        if (isAndroidSipAvailable()) {
            String username = intent == null ? null : intent.getStringExtra(EXTRA_USERNAME);
            mSipManager = SipManager.newInstance(this);
            mSipProfile = createSipProfile(username);

        } else { // if android sip is not available, initialize PJSIP
            // TODO: PJSIP
            throw new RuntimeException("SIP is not supported on this device");
        }


        // Incoming call
        IntentFilter filter = new IntentFilter();
        filter.addAction(SIP_CALL_ACTION);
        registerReceiver(mCallReceiver, filter);

        mSipManager.open(mSipProfile, PendingIntent.getBroadcast(this, 0,
                new Intent(SIP_CALL_ACTION), Intent.FILL_IN_DATA), null);
        mSipManager.setRegistrationListener(mSipProfile.getUriString(), new SipRegistrationListener() {
            @Override
            public void onRegistering(String localProfileUri) {
                Log.d(TAG, "Reg. In Progress: " + localProfileUri);
            }

            @Override
            public void onRegistrationDone(String localProfileUri, long expiryTime) {
                Log.d(TAG, "Registration Complete!");
                if (mRegistrationListener != null) {
                    mRegistrationListener.onRegistration();
                }
            }

            @Override
            public void onRegistrationFailed(String localProfileUri, int errorCode, String errorMessage) {
                Log.e(TAG, "Registration Error (" + errorCode + "): " + errorMessage);
                if (mRegistrationListener != null) {
                    mRegistrationListener.onError(new Exception(String.format("Failed to register (%d): %s", errorCode, localProfileUri)));
                }
            }
        });
    }

    private Boolean isAndroidSipAvailable() {
        return SipManager.isApiSupported(this) && SipManager.isVoipSupported(this);
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
    public void onDestroy() {
        if (mCall != null) {
            try {
                mCall.endCall();
            } catch (SipException e) { /* ignore */ }
        }

        if (mSipManager != null) {
            try {
                if (mSipProfile != null) {
                    mSipManager.close(mSipProfile.getUriString());
                }
            } catch (SipException e) { /* ignore */  }
        }
        super.onDestroy();
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
        setCallListener(listener);
        mCall = mSipManager.makeAudioCall(mSipProfile.getUriString(), peerUri, mBaseCallListener, timeout);
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
        if (mCall != null) {
            mCall.setListener(null);
            mCall.endCall();
            mCall = null;
        }
    }


    /**
     * Send DTMF to active call
     * @param character
     */
    public void sendDtmf(char character) {
        if (mCall != null && mCall.isInCall()) {
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
            mCall.sendDtmf(code);
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
            setCallListener(listener);
            mCall.answerCall(timeout);
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



    public void setRegistrationListener(VoiceListener listener) {
        if (listener != null) {
            mRegistrationListener = listener;
        }
    }


    public void setCallListener(SipAudioCall.Listener listener) {
        if (listener != null) {
            mCallListener = listener;
        }
    }
}
