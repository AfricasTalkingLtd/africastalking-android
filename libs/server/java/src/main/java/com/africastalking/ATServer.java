package com.africastalking;

import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.Metadata;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.ServerInterceptors;
import io.grpc.Status;
import io.grpc.ServerCall.Listener;

import java.io.File;
import java.io.IOException;
import static io.grpc.Metadata.ASCII_STRING_MARSHALLER;

public final class ATServer {

    private static int DEFAULT_PORT = 35897;

    private static final Metadata.Key<String> CLIENT_ID_HEADER_KEY = Metadata.Key.of("X-Client-Id", ASCII_STRING_MARSHALLER);
    static final Context.Key<String> CLIENT_ID_CONTEXT_KEY = Context.key("X-Client-Id");


    private Server mGrpc;
    private SdkServerService mSdkService;
    private ClientVerifier mClientVerifier = null;
    ATServer(String username, String apiKey, String environment) {
        mSdkService = new SdkServerService(username, apiKey, environment);

    }
    public void addSipCredentials(String username, String password, String host, int port) {
        mSdkService.addSipCredentials(username, password, host, port);
    }

    public void addSipCredentials(String username, String password, String host) {
        this.addSipCredentials(username, password, host, 5060);
    }

    public void setClientVerifier(ClientVerifier verifier) {
        if (verifier == null) throw new NullPointerException("Client verifier cannot be null");
        mClientVerifier = verifier;
    }

    public void start(File certChainFile, File privateKeyFile, int port) throws IOException {
        if (mClientVerifier == null) throw new NullPointerException("call setClientVerifier() before start()");
        mGrpc = ServerBuilder.forPort(port)
                .useTransportSecurity(certChainFile, privateKeyFile)
                .addService(ServerInterceptors.intercept(mSdkService, new ClientIdInterceptor(this.mClientVerifier)))
                .build();
        mGrpc.start();
    }

    public void start(File certChainFile, File privateKeyFile) throws IOException {
        this.start(certChainFile, privateKeyFile, DEFAULT_PORT);
    }

    static class ClientIdInterceptor implements ServerInterceptor {
        static final Listener NOOP_LISTENER = new Listener() {};
        
        ClientVerifier verifier;

        ClientIdInterceptor(ClientVerifier verifier) {
            this.verifier = verifier;
        }

        @Override
        public <ReqT, RespT> Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
            String clientId = headers.get(CLIENT_ID_HEADER_KEY);
            if (clientId == null || !verifier.isValid(clientId)) {
                call.close(Status.UNAUTHENTICATED.withDescription("Invalid or unknown client"), headers);
                return NOOP_LISTENER;
            }
            Context context = Context.current().withValue(CLIENT_ID_CONTEXT_KEY, clientId);
            return Contexts.interceptCall(context, call, headers, next);
        }
    }
}