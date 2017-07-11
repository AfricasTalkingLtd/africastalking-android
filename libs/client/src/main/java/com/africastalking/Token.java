package com.africastalking;

import android.util.Log;

import com.africastalking.proto.RemoteToken.ClientTokenRequest;
import com.africastalking.proto.RemoteToken.ClientTokenResponse;
import com.africastalking.proto.SdkServerServiceGrpc;
import com.africastalking.proto.SdkServerServiceGrpc.SdkServerServiceBlockingStub;
import com.africastalking.proto.SdkServerServiceGrpc.SdkServerServiceStub;

import java.io.IOException;

import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;

public class Token {

    private static SdkServerServiceStub asyncStub;
    private static SdkServerServiceBlockingStub blockingStub;
    private static ClientTokenResponse resp;
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

    public String getTokenString(){
        if(token != null){
            token = resp.getToken();
        }
        return token;
    }

    public long getExpiration(){
        if(expiration != 0){
            expiration = resp.getExpiration();
        }
        return expiration;
    }

    private static ClientTokenResponse getToken() throws IOException {
        ClientTokenRequest req = ClientTokenRequest.newBuilder().build();
        resp = blockingStub.getToken(req);
        return resp;
    }

    private void getToken(final Callback<String> callback) {
        ClientTokenRequest req = ClientTokenRequest.newBuilder().build();
        asyncStub.getToken(req, new StreamObserver<ClientTokenResponse>() {
            @Override
            public void onNext(ClientTokenResponse value) {
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
