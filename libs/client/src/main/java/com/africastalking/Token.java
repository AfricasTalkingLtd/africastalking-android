package com.africastalking;

import com.africastalking.proto.RemoteToken.ClientTokenRequest;
import com.africastalking.proto.RemoteToken.ClientTokenResponse;
import com.africastalking.proto.SdkServerServiceGrpc;
import com.africastalking.proto.SdkServerServiceGrpc.SdkServerServiceBlockingStub;
import com.africastalking.proto.SdkServerServiceGrpc.SdkServerServiceStub;

import java.io.IOException;

import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;

public class Token {

    private SdkServerServiceStub asyncStub;
    private SdkServerServiceBlockingStub blockingStub;

    Token() {
        ManagedChannel channel = AfricasTalking.getChannel();
        blockingStub = SdkServerServiceGrpc.newBlockingStub(channel);
        asyncStub = SdkServerServiceGrpc.newStub(channel);
    }

    public String getToken() throws IOException {
        ClientTokenRequest req = ClientTokenRequest.newBuilder().build();
        ClientTokenResponse resp = blockingStub.getToken(req);
        return resp.getToken();
    }

    public void getToken(final Callback<String> callback) {
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
