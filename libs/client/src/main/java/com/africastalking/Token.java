package com.africastalking;

import android.util.Log;


import com.africastalking.proto.SdkServerServiceGrpc;
import com.africastalking.proto.SdkServerServiceGrpc.SdkServerServiceBlockingStub;
import com.africastalking.proto.SdkServerServiceGrpc.SdkServerServiceStub;
import com.africastalking.proto.SdkServerServiceOuterClass;

import java.io.IOException;

import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;

class Token {

    private static SdkServerServiceStub asyncStub;
    private static SdkServerServiceBlockingStub blockingStub;
    private static SdkServerServiceOuterClass.ClientTokenResponse resp;
    private static String token;
    private static long expiration;

    Token() {
        ManagedChannel channel = AfricasTalking.getChannel();
        blockingStub = SdkServerServiceGrpc.newBlockingStub(channel);
        asyncStub = SdkServerServiceGrpc.newStub(channel);
        try {
            resp = getToken();
        } catch (IOException e) {
            Log.d("ClientTokenResponse",e.getMessage());
        }
    }

    String getTokenString(){
        if(token != null){
            token = resp.getToken();
        }
        return token;
    }

    long getExpiration(){
        if(expiration != 0){
            expiration = resp.getExpiration();
        }
        return expiration;
    }

    private static SdkServerServiceOuterClass.ClientTokenResponse getToken() throws IOException {
        SdkServerServiceOuterClass.ClientTokenRequest req = SdkServerServiceOuterClass.ClientTokenRequest.newBuilder().build();
        resp = blockingStub.getToken(req);
        return resp;
    }

    private void getToken(final Callback<String> callback) {
        SdkServerServiceOuterClass.ClientTokenRequest req = SdkServerServiceOuterClass.ClientTokenRequest.newBuilder().build();
        asyncStub.getToken(req, new StreamObserver<SdkServerServiceOuterClass.ClientTokenResponse>() {
            @Override
            public void onNext(SdkServerServiceOuterClass.ClientTokenResponse value) {
                callback.onSuccess(value.getToken());
            }

            @Override
            public void onError(Throwable t) {
                callback.onFailure(t);
            }

            @Override
            public void onCompleted() {

            }
        });
    }
}
