package com.africastalking.voice;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.sip.SipAudioCall;
import android.net.sip.SipException;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.net.sip.SipRegistrationListener;
import android.util.Log;

import com.africastalking.AfricasTalkingException;
import com.africastalking.proto.SdkServerServiceOuterClass.*;

import java.text.ParseException;
import java.util.Locale;

import static com.africastalking.voice.VoiceBackgroundService.INCOMING_CALL;

/**
 * Copyright (c) 2017 Salama AB
 * All rights reserved
 * Contact: aksalj@aksalj.me
 * Website: http://www.aksalj.me
 * <p>
 * Project : dev-mvp
 * File : AndroidSipStack
 * Date : 8/12/17 10:34 AM
 * Description :
 */
class AndroidSipStack extends BaseSipStack {

    private static final String TAG = AndroidSipStack.class.getName();
    private static final String SIP_CALL_ACTION = "com.africastalking.voice.internal.INCOMING_CALL";

    private SipManager mSipManager;
    private SipProfile mSipProfile;

    private SipAudioCall mCall;
    private static CallListener mCallListener;


    private SipAudioCall.Listener mBaseCallListener = new SipAudioCall.Listener() {
        @Override
        public void onError(SipAudioCall call, int errorCode, String errorMessage) {
            Log.e(TAG, "Call Error: " + errorMessage);
            if (mCallListener != null) {
                mCallListener.onError(makeCallInfo(call), errorCode, errorMessage);
            }
        }

        @Override
        public void onRinging(SipAudioCall call, SipProfile caller) {
            Log.d(TAG, "Call Ringing");
            if (mCallListener != null) {
                mCallListener.onRinging(makeCallInfo(call), caller.getAuthUserName());
            }
        }

        @Override
        public void onRingingBack(SipAudioCall call) {
            Log.d(TAG, "Call Ring Back");
            if (mCallListener != null) {
                mCallListener.onRingingBack(makeCallInfo(call));
            }
        }

        @Override
        public void onCallBusy(SipAudioCall call) {
            Log.d(TAG, "Call Busy");
            if (mCallListener != null) {
                mCallListener.onCallBusy(makeCallInfo(call));
            }
        }

        @Override
        public void onCallEnded(SipAudioCall call) {
            Log.d(TAG, "Call Ended");
            if (mCallListener != null) {
                mCallListener.onCallEnded(makeCallInfo(call));
            }

            mCall.setListener(null);
            mCall = null;
        }

        @Override
        public void onCallEstablished(SipAudioCall call) {
            Log.d(TAG, "Call Established");
            if (mCallListener != null) {
                mCallListener.onCallEstablished(makeCallInfo(call));
            }
        }

        @Override
        public void onCallHeld(SipAudioCall call) {
            Log.d(TAG, "Call Held");
            if (mCallListener != null) {
                mCallListener.onCallHeld(makeCallInfo(call));
            }
        }

        @Override
        public void onCalling(SipAudioCall call) {
            Log.d(TAG, "Call Calling");
            if (mCallListener != null) {
                mCallListener.onCalling(makeCallInfo(call));
            }
        }

        @Override
        public void onReadyToCall(SipAudioCall call) {
            Log.d(TAG, "Call Ready To Call");
            if (mCallListener != null) {
                mCallListener.onReadyToCall(makeCallInfo(call));
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
            context.sendBroadcast(new Intent(INCOMING_CALL));
        }
    };




    private AndroidSipStack(final VoiceBackgroundService context, SipCredentials credentials) throws ParseException, SipException {
        super(credentials);
        Log.d(TAG, "Initializing Android SIP...");

        mSipManager = SipManager.newInstance(context);

        if (mCredentials == null) {
            throw new RuntimeException("No SIP credentials found");
        }

        String host = mCredentials.getHost();
        int port = mCredentials.getPort();
        String transport = mCredentials.getTransport();
        String username = mCredentials.getUsername();
        String password = mCredentials.getPassword();

        SipProfile.Builder builder =  new SipProfile.Builder(username, host);
        builder.setPort(port);
        builder.setProtocol(transport);
        builder.setPassword(password);
        mSipProfile = builder.build();


        // Incoming call
        IntentFilter filter = new IntentFilter();
        filter.addAction(SIP_CALL_ACTION);
        context.registerReceiver(mCallReceiver, filter);


        Log.d(TAG, "Opening local SIP profile...");

        mSipManager.open(mSipProfile, PendingIntent.getBroadcast(context, 0,
                new Intent(SIP_CALL_ACTION), Intent.FILL_IN_DATA), null);

        Log.d(TAG, "Setting registration listener...");
        mSipManager.setRegistrationListener(mSipProfile.getUriString(), new SipRegistrationListener() {
            @Override
            public void onRegistering(String localProfileUri) {
                Log.d(TAG, "Registration In Progress: " + localProfileUri);
                setReady(false);
                if (VoiceBackgroundService.mRegistrationListener != null) {
                    VoiceBackgroundService.mRegistrationListener.onStartRegistration();
                }
            }

            @Override
            public void onRegistrationDone(String localProfileUri, long expiryTime) {
                Log.d(TAG, "Registration Complete!");
                setReady(true);
                if (VoiceBackgroundService.mRegistrationListener != null) {
                    VoiceBackgroundService.mRegistrationListener.onCompleteRegistration();
                }
            }

            @Override
            public void onRegistrationFailed(String localProfileUri, int errorCode, String errorMessage) {
                Log.e(TAG, "Registration Error (" + errorCode + "): " + errorMessage);
                setReady(false);
                if (VoiceBackgroundService.mRegistrationListener != null) {
                    VoiceBackgroundService.mRegistrationListener.onFailedRegistration(
                            new Exception(String.format(Locale.ENGLISH, "Failed to register (%d): %s", errorCode, localProfileUri)));
                }
            }
        });
    }


    static AndroidSipStack newInstance(VoiceBackgroundService context, SipCredentials credentials) throws ParseException, SipException {
        return new AndroidSipStack(context, credentials);
    }

    private CallInfo makeCallInfo(SipAudioCall call) {
        CallInfo info = new CallInfo();
        return info;
    }

    @Override
    public void destroy(VoiceBackgroundService context) {

        try {
            context.unregisterReceiver(mCallReceiver);
        } catch (Exception e) { /* ignore */ }

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
    }

    @Override
    public void setCallListener(CallListener listener) {
        if (listener != null) {
            mCallListener = listener;
        }
    }

    @Override
    public void makeCall(String destination, int timeout, CallListener listener) throws AfricasTalkingException {
        String peerUri = destination;
        if (BaseSipStack.isSipUri(peerUri)) {
            if (!peerUri.startsWith("sip:")) {
                peerUri = "sip:" + peerUri;
            }
        } else {
            try {
                SipProfile peer = new SipProfile.Builder(destination, mCredentials.getHost()).build();
                peerUri = peer.getUriString();
            } catch (ParseException ex) {
                throw new AfricasTalkingException("Invalid destination");
            }
        }
        Log.d(TAG, "Calling " + peerUri);
        setCallListener(listener);
        try {
            mCall = mSipManager.makeAudioCall(mSipProfile.getUriString(), peerUri, mBaseCallListener, timeout);
        } catch (SipException e) {
            throw new AfricasTalkingException(e);
        }

    }

    @Override
    public void pickCall(int timeout, CallListener listener) throws AfricasTalkingException {
        try {
            setCallListener(listener);
            mCall.answerCall(timeout);
        } catch (SipException e) {
            Log.e(TAG, "Failed to take audio call");
            throw new AfricasTalkingException(e);
        }
    }

    @Override
    public void endCall() throws AfricasTalkingException {
        if (mCall != null) {
            try {
                mCall.endCall();
            } catch (SipException e) {
                Log.e(TAG, e.getMessage() + "");
                throw new AfricasTalkingException(e);
            }
        }
    }

    @Override
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

    @Override
    public void holdCall(int timeout) throws AfricasTalkingException {
        if (isCallInProgress()) {
            try {
                mCall.holdCall(timeout);
            } catch (SipException e) {
                Log.e(TAG, e.getMessage() + "");
                throw new AfricasTalkingException(e);
            }
        }
    }

    @Override
    public void resumeCall(int timeout) throws AfricasTalkingException {
        if (mCall != null && mCall.isOnHold()) {
            try {
                mCall.continueCall(timeout);
            } catch (SipException e) {
                Log.e(TAG, e.getMessage() + "");
                throw new AfricasTalkingException(e);
            }
        }
    }

    @Override
    public void toggleMute() {
        if(mCall != null) {
            mCall.toggleMute();
        }
    }

    @Override
    public void setSpeakerMode(boolean speaker) {
        if (mCall != null) {
            mCall.setSpeakerMode(speaker);
        }
    }

    @Override
    public void startAudio() {
        if (mCall != null) {
            mCall.startAudio();
        }
    }

    @Override
    public boolean isCallInProgress() {
        return mCall != null && mCall.isInCall();
    }

}
