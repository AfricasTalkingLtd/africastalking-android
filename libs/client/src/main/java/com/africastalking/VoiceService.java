package com.africastalking;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.sip.*;
import android.util.Log;

import com.africastalking.proto.SdkServerServiceGrpc;
import com.africastalking.proto.SdkServerServiceOuterClass.SipCredentials;
import com.africastalking.proto.SdkServerServiceOuterClass.SipCredentialsRequest;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.grpc.ManagedChannel;

public final class VoiceService {

    private static final String TAG = VoiceService.class.getName();

    private static final String SIP_CALL_ACTION = "com.africastalking.INCOMING_CALL";

    private List<SipCredentials> mSipCredentials;
    private SipCredentials mCredentials;

    private SipManager mSipManager = null;
    private SipProfile mSipProfile = null;

    private Intent mUIIntent = null;
    private SipAudioCall mActiveCall = null;

    private VoiceListener mCallback = new VoiceListener() {
        @Override
        public void onError(Throwable error) {
            Log.d("Default Listener", "Error " + error.getMessage());
        }

        @Override
        public void onRegistration() {
            Log.d("Default Listener", "Reg. Done");
        }
    };

    public interface VoiceListener {
        void onError(Throwable error);
        void onRegistration();
    }


    VoiceService(Context context, VoiceListener listener, String username) throws Exception {
        if (listener != null) {
            this.mCallback = listener;
        }

        ManagedChannel channel = AfricasTalking.getChannel();
        SdkServerServiceGrpc.SdkServerServiceBlockingStub stub = SdkServerServiceGrpc.newBlockingStub(channel);

        SipCredentialsRequest req = SipCredentialsRequest.newBuilder().build();
        mSipCredentials = stub.getSipCredentials(req).getCredentialsList();

        initService(context, username);
    }


    VoiceService(Context context, VoiceListener listener) throws Exception {
        this(context, listener, null);
    }


    private void initService(Context context, String username) throws Exception {
        if (isAndroidSipAvailable(context)) {
            if (!isInitialized()) {
                mSipManager = SipManager.newInstance(context);
            }
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
                takeAudioCall(context, intent);
            }
        }, filter);

        mSipManager.open(mSipProfile, PendingIntent.getBroadcast(context, 0, new Intent(SIP_CALL_ACTION), Intent.FILL_IN_DATA), new SipRegistrationListener() {
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

    public Boolean isInitialized() {
        return mSipManager != null;
    }


    private SipProfile createSipProfile(String defaultUsername) throws Exception {

        if (mSipCredentials == null || mSipCredentials.size() == 0) {
            throw new RuntimeException("No SIP credentials found");
        }

        mCredentials = defaultUsername == null ? mSipCredentials.get(0) : mSipCredentials.get(getIndex(defaultUsername));

        String host = mCredentials.getHost();
        String username = mCredentials.getUsername();
        String password = mCredentials.getPassword();

        SipProfile.Builder builder = null;

        // builder = new SipProfile.Builder("+254792424735", "sandbox.sip.africastalking.com");
        builder = new SipProfile.Builder(username, host);
        // builder.setPassword("DOPx_7bb9eab00b");
        builder.setPassword(password);
        return builder.build();
    }


    // get index of object SipCredential object with provided credentials
    private int getIndex(String username) throws Exception {
        if (username != null && mSipCredentials != null) {
            for (SipCredentials credentials : mSipCredentials) {
                if (credentials.getUsername().equals(username))
                    return mSipCredentials.indexOf(credentials);
            }
        }
         throw new Exception("Invalid username");
    }

    public void makeCall(String address, SipAudioCall.Listener listener, int timeout) throws Exception {
        String peerUri = address;
        if (isSipUri(peerUri)) {
            if (!peerUri.startsWith("sip:")) {
                peerUri = "sip:" + peerUri;
            }
        } else {
            SipProfile peer = new SipProfile.Builder(address, mCredentials.getHost()).build();
            peerUri = peer.getUriString();
        }
        mActiveCall = mSipManager.makeAudioCall(mSipProfile.getUriString(), peerUri, listener, timeout);
    }

    public void makeCall(String phoneNumber, SipAudioCall.Listener listener) throws Exception {
        Log.d(TAG, "Calling " + phoneNumber);
        this.makeCall(phoneNumber, listener, 10000);
    }

    public SipAudioCall getActiveCall(SipAudioCall.Listener listener) {
        mActiveCall.setListener(listener);
        return mActiveCall;
    }

    public void setIncomingCallActivity(Intent intent) {
        this.mUIIntent = intent;
    }

    private void takeAudioCall(Context context, Intent intent) {
        try {
            mActiveCall = mSipManager.takeAudioCall(intent, new SipAudioCall.Listener(){});
            if (this.mUIIntent != null) {
                context.startActivity(mUIIntent);
            }
        } catch (SipException e) {
            Log.e(TAG, "Failed to take audio call");
            e.printStackTrace();
        }
    }

    private static boolean isSipUri(String uri) {
        String expression = "/^(sip:)?(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$/";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(uri);
        return matcher.matches();
    }

}
