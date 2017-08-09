package com.africastalking;


import java.util.ArrayList;
import java.util.List;
import com.africastalking.proto.SdkServerServiceGrpc.*;
import com.africastalking.proto.SdkServerServiceOuterClass;
import com.africastalking.proto.SdkServerServiceOuterClass.*;
import io.grpc.stub.StreamObserver;

final class SdkServerService extends SdkServerServiceImplBase {

    private static String TOKEN_HOST = "https://api.africastalking.com";

    private ArrayList<SipCredentials> mSipCredentials = new ArrayList<>();

    private String mUsername, mAPIKey;
    private String mEnvironment;

    SdkServerService(String username, String apiKey, String environment) {
        mUsername = username;
        mAPIKey = apiKey;
        mEnvironment = environment;

        // TODO: TOKEN_HOST based on environment?
    }

    void addSipCredentials(String username, String password, String host, int port, String transport) {
        mSipCredentials.add(new SipCredentials(username, password, host, port, transport));
    }

    @Override
    public void getToken(ClientTokenRequest request, final StreamObserver<ClientTokenResponse> response) {

        // TODO: http request to token server with mUsername and mAPIKey


        ClientTokenResponse tokenResponse = ClientTokenResponse.newBuilder()
                    .setToken(String.valueOf(System.currentTimeMillis()))
                    .setExpiration(System.currentTimeMillis() + 30000)
                    .build();
        response.onNext(tokenResponse);
        response.onCompleted();
    }

    @Override
    public void getSipCredentials(SipCredentialsRequest request, StreamObserver<SipCredentialsResponse> response) {
        List<SdkServerServiceOuterClass.SipCredentials> credentials = new ArrayList<>();
        for(SipCredentials item: mSipCredentials) {
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
        public String username, password;
        public String host;
        public int port;
        public String transport;

        SipCredentials(String username, String password, String host, int port, String transport) {
            this.username = username;
            this.password = password;
            this.host = host;
            this.port = port;
            this.transport = transport;
        }
    }
}