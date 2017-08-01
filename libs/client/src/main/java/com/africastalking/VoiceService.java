package com.africastalking;

import android.content.Context;
import android.content.Intent;
import android.net.sip.SipAudioCall;
import android.net.sip.SipException;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.util.Log;

import com.africastalking.proto.SdkServerServiceGrpc;
import com.africastalking.proto.SdkServerServiceOuterClass.SipCredentials;
import com.africastalking.proto.SdkServerServiceOuterClass.SipCredentialsRequest;

import java.text.ParseException;
import java.util.List;

import io.grpc.ManagedChannel;

public final class VoiceService {
    private static String HOST;
    private static String USERNAME;
    private static String PASSWORD;
//    private static int PORT;
    private SipManager sipManager = null;
    private SipProfile sipProfile = null;
    SdkServerServiceGrpc.SdkServerServiceBlockingStub blockingStub;
    SdkServerServiceGrpc.SdkServerServiceStub asyncStub;
    Context context;
    List<SipCredentials> sipCredentials;
    private SipAudioCall.Listener callListener;
    private SipAudioCall call;

    public VoiceService(Context context){
        this.context = context;
        ManagedChannel channel = AfricasTalking.getChannel();
        blockingStub = SdkServerServiceGrpc.newBlockingStub(channel);
        asyncStub = SdkServerServiceGrpc.newStub(channel);

        sipCredentials = getSipCredentials();

        HOST = getHost();
        PASSWORD = getPassword();
        USERNAME = getUsername();
        initService();
    }

    VoiceService() {
        ManagedChannel channel = AfricasTalking.getChannel();
        blockingStub = SdkServerServiceGrpc.newBlockingStub(channel);
        asyncStub = SdkServerServiceGrpc.newStub(channel);

        sipCredentials = getSipCredentials();

        HOST = getHost();
        PASSWORD = getPassword();
        USERNAME = getUsername();

        initService();
    }

    VoiceService(String username) {
        ManagedChannel channel = AfricasTalking.getChannel();
        blockingStub = SdkServerServiceGrpc.newBlockingStub(channel);
        asyncStub = SdkServerServiceGrpc.newStub(channel);

        sipCredentials = getSipCredentials();

        HOST = getHost();
        PASSWORD = getPassword();
        USERNAME = username;

        initService();
    }

    VoiceService(String username, String password, String host) {
//        ManagedChannel channel = AfricasTalking.getChannel();
//        blockingStub = SdkServerServiceGrpc.newBlockingStub(channel);
//        asyncStub = SdkServerServiceGrpc.newStub(channel);
//
//        sipCredentials = getSipCredentials();

        HOST = host;
        PASSWORD = password;
        USERNAME = username;

//        initService();
    }

    private void initService() {
        if (isSipAvailable()) {
            if (!isInitialized()) {
                sipManager = SipManager.newInstance(context);
            }
            sipProfile = createSipProfile();
        }
        else { // if android sip is not available, initialize PJSIP
            // TODO PJSIP
        }
    }

    private Boolean isSipAvailable() {
        return SipManager.isApiSupported(context) && SipManager.isVoipSupported(context);
    }

    public Boolean isInitialized() {
      return sipManager != null;
    }

    protected SipProfile createSipProfile() {
        SipProfile.Builder builder = null;
        try {
//            builder = new SipProfile.Builder("+254792424735", "sandbox.sip.africastalking.com");
            builder = new SipProfile.Builder(USERNAME, HOST);
        } catch (ParseException e) {

        }
//        builder.setPassword("DOPx_7bb9eab00b");
        builder.setPassword(PASSWORD);
        return builder.build();
    }

    protected List<SipCredentials> getSipCredentials(){
        SipCredentialsRequest req = SipCredentialsRequest.newBuilder().build();
        return blockingStub.getSipCredentials(req).getCredentialsList();
    }

    protected String getHost() {
        if (sipCredentials != null) {
            if (USERNAME != null) {
                return sipCredentials.get(getIndex(USERNAME)).getHost();
            }
            return sipCredentials.get(0).getHost();
        }
        return null;
    }

    protected String getUsername(){
        if (sipCredentials != null) {
            return sipCredentials.get(0).getHost();
        }
        return null;
    }

    protected String getPassword(){
        if (sipCredentials != null) {
            if (USERNAME != null) {
                return sipCredentials.get(getIndex(USERNAME)).getPassword();
            }
            return sipCredentials.get(0).getPassword();
        }
        return null;
    }

    // get index of object SipCredential object with provided credentials
    protected int getIndex(String username) {
        if(username != null && sipCredentials != null) {
            for(SipCredentials credentials: sipCredentials){
                if(credentials.getUsername().equals(USERNAME))
                    return sipCredentials.indexOf(credentials);
            }
        }
        return 0;
    }

    public void makeCall(String phoneNumber) { // TODO initiate call
        callListener = new SipAudioCall.Listener() {
            @Override
            public void onCallEstablished(SipAudioCall call) {
                call.startAudio();
            }

            @Override
            public void onCallEnded(SipAudioCall call) {
                try {
                    call.endCall();
                } catch (SipException e) {
                    Log.d("Error ending call", e.getMessage());
                }
            }
        };
        try {
            call = sipManager.makeAudioCall(sipProfile.getUriString(), phoneNumber, callListener, 30);
        } catch (SipException e) {
            Log.d("Error making call", e.getMessage());
        }
    }

    public void takeAudioCall(Intent intent, SipAudioCall.Listener listener) {
        try {
            SipAudioCall incomingCall = sipManager.takeAudioCall(intent, listener);
            incomingCall.answerCall(30);
            incomingCall.startAudio();
            if (incomingCall.isMuted())
                incomingCall.toggleMute();
        } catch (SipException e) {
            e.printStackTrace();
        }
    }

}
