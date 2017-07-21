package com.africastalking;

import android.content.Context;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;

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

    public VoiceService(Context context){
        this.context = context;
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
    }

    VoiceService(String username) {
        ManagedChannel channel = AfricasTalking.getChannel();
        blockingStub = SdkServerServiceGrpc.newBlockingStub(channel);
        asyncStub = SdkServerServiceGrpc.newStub(channel);

        sipCredentials = getSipCredentials();

        HOST = getHost();
        PASSWORD = getPassword();
        USERNAME = username;
    }

    private void initService() {
        if (isSipAvailable()) {
            if (isInitialized()) {
                sipManager = SipManager.newInstance(context);
            }
            createSipProfile();
        }
        else { // if android sip is not available, initialize PJSIP

        }
    }

    private Boolean isSipAvailable() {
        return SipManager.isApiSupported(context) && SipManager.isVoipSupported(context);
    }

    private Boolean isInitialized() {
      return sipManager != null;
    }

    private void createSipProfile() {
        SipProfile.Builder builder = null;
        try {
            builder = new SipProfile.Builder(USERNAME, HOST);
        } catch (ParseException e) {

        }
        builder.setPassword(PASSWORD);
        sipProfile = builder.build();
    }

    private List<SipCredentials> getSipCredentials(){
        SipCredentialsRequest req = SipCredentialsRequest.newBuilder().build();
        return blockingStub.getSipCredentials(req).getCredentialsList();
    }

    private String getHost() {
        if (sipCredentials != null) {
            if (USERNAME != null) {
                return sipCredentials.get(getIndex(USERNAME)).getHost();
            }
            return sipCredentials.get(0).getHost();
        }
        return null;
    }

    private String getUsername(){
        if (sipCredentials != null) {
            return sipCredentials.get(0).getHost();
        }
        return null;
    }

    private String getPassword(){
        if (sipCredentials != null) {
            if (USERNAME != null) {
                return sipCredentials.get(getIndex(USERNAME)).getPassword();
            }
            return sipCredentials.get(0).getPassword();
        }
        return null;
    }

    // get index of object SipCredential object with provided credentials
    private int getIndex(String username) {
        if(username != null && sipCredentials != null) {
            for(SipCredentials credentials: sipCredentials){
                if(credentials.getUsername().equals(USERNAME))
                    return sipCredentials.indexOf(credentials);
            }
        }
        return 0;
    }

    public void makeCall(String phoneNumber) { // TODO initiate call

    }

}
