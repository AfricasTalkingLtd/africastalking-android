package com.africastalking;


import java.util.ArrayList;
import java.util.List;
import com.africastalking.proto.SdkServerServiceGrpc.*;
import com.africastalking.proto.SdkServerServiceOuterClass;
import com.africastalking.proto.SdkServerServiceOuterClass.*;
import io.grpc.stub.StreamObserver;

final class SdkServerService extends SdkServerServiceImplBase {

    private static String TOKEN_HOST;

    private ArrayList<SipCredentials> credentialsList = new ArrayList<>();

    private String username, apiKey;
    private boolean isSandbox = false;

    SdkServerService(String username, String apiKey) {
        this.username = username;
        this.apiKey = apiKey;
        isSandbox = username.toLowerCase().contentEquals("sandbox");

        TOKEN_HOST = isSandbox ? "https://api.sandbox.africastalking.com" : "https://api.africastalking.com";
    }

    void addSipCredentials(String username, String password, String host, int port, String transport) {
        credentialsList.add(new SipCredentials(username, password, host, port, transport));
    }

    @Override
    public void getToken(ClientTokenRequest request, final StreamObserver<ClientTokenResponse> response) {

        // TODO: http request to token server with username and api
        String token = "6e44229611d255b5d58f80d057fc2da8708aa95dad0aba6843314fdac3e2d75c";
        long expires = System.currentTimeMillis() + 30000;


        ClientTokenResponse tokenResponse = ClientTokenResponse.newBuilder()
                    .setToken(token)
                    .setExpiration(expires)
                    .setUsername(username)
                    .setEnvironment(isSandbox ? "sandbox" : "production")
                    .build();
        response.onNext(tokenResponse);
        response.onCompleted();
    }

    @Override
    public void getSipCredentials(SipCredentialsRequest request, StreamObserver<SipCredentialsResponse> response) {
        List<SdkServerServiceOuterClass.SipCredentials> credentials = new ArrayList<>();
        for(SipCredentials item: credentialsList) {
            SdkServerServiceOuterClass.SipCredentials cred = SdkServerServiceOuterClass.SipCredentials.newBuilder()
                .setHost(item.host)
                .setPort(item.port)
                .setUsername(item.username)
                .setPassword(item.password)
                .setTransport(item.transport)
                .build();
            credentials.add(cred);
        }
        SipCredentialsResponse res = SipCredentialsResponse.newBuilder().addAllCredentials(credentials).build();
        response.onNext(res);
        response.onCompleted();
    }



    class SipCredentials {
        String username, password;
        String host;
        int port;
        String transport;

        SipCredentials(String username, String password, String host, int port, String transport) {
            this.username = username;
            this.password = password;
            this.host = host;
            this.port = port;
            this.transport = transport;
        }
    }
}