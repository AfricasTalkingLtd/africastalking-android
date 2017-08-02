package com.africastalking;

import android.content.Context;

import java.io.IOException;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public final class AfricasTalking {

    private static String HOST;
    private static int PORT = 35897; // FIXME

    private static String tokenString;

    private static AccountService account;
    private static AirtimeService airtime;
    private static PaymentsService payments;
    private static SMSService sms;
    private static Token token;

    private static ManagedChannel CHANNEL;
    static Environment ENV = Environment.SANDBOX;
    static Boolean LOGGING = false;
    static Logger LOGGER = new BaseLogger();
    static CallType CALLTYPE = CallType.MOCK;
    public static CallService CALLSERVICE;


    public static void initialize(String host) {
        HOST = host;
        CHANNEL = getChannel();
        tokenString = getToken();
    }

    public static void initialize(String host, int port) {
        HOST = host;
        PORT = port;
        CHANNEL = getChannel();
        tokenString = getToken();
    }

    public static void initialize(String host, int port, boolean token) {
        HOST = host;
        PORT = port;
        CHANNEL = getChannel();
        if(token)
            tokenString = getToken();
    }

    public static void destroy() {
        HOST = null;
        PORT = 0;
        CHANNEL = null;
        tokenString = null;
        account = null;
        airtime = null;
        payments = null;
        sms = null;
        token = null;
    }

    public static ManagedChannel getChannel() {
        if (HOST == null || PORT == -1)
            throw new RuntimeException("call AfricasTalking.initialize(host, port, token) first");
        if (CHANNEL == null) {
            // TODO
            ManagedChannelBuilder<?> channelBuilder = ManagedChannelBuilder.forAddress(HOST, PORT).usePlaintext(true); // FIXME: Remove to Setup TLS
            CHANNEL = channelBuilder.build();
        }
        return CHANNEL;
    }

    public static SMSService getSmsService() {
        CALLSERVICE = CallService.SMS;
        if (sms == null) {
            sms = new SMSService();
            return sms;
        }
        return sms;
    }

    public static AirtimeService getAirtimeService() {
        CALLSERVICE = CallService.AIRTIME;
        if (airtime == null) {
            airtime = new AirtimeService();
        }
        return airtime;
    }

    public static PaymentsService getPaymentsService() {
        CALLSERVICE = CallService.PAYMENT;
        if (payments == null) {
            payments = new PaymentsService();
        }
        return payments;
    }

    public static AccountService getAccount() {
        CALLSERVICE = CallService.ACCOUNT;
        if (account == null) {
            account = new AccountService();
        }
        return account;
    }

    protected static String getToken() {
        if (token == null) {
            token = new Token();
            tokenString = token.getTokenString();
        } else {
            //TODO check if token not expired
            if (token.getExpiration() != 0) {

            }
        }
        return tokenString;
    }

    public static void setEnvironment(Environment env) {
        ENV = env;
    }

    private static void enableLogging(boolean enable) {
        LOGGING = enable;
    }

    public static void setLogger(Logger logger) {
        if (logger != null) {
            enableLogging(true);
        }
        LOGGER = logger;
    }
}
