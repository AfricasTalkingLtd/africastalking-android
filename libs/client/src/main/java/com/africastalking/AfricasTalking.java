package com.africastalking;

import android.content.Context;

import java.io.IOException;


public final class AfricasTalking {

    static String USERNAME;
    static String HOST;
    static int PORT = 35897;


    private static AccountService account;
    private static AirtimeService airtime;
    private static PaymentsService payments;
    private static SMSService sms;
    private static VoiceService voice;

    static Environment ENV = Environment.PRODUCTION;
    static Boolean LOGGING = false;
    static Logger LOGGER = new BaseLogger();

    public static void initialize(String username, String host, int port, Environment environment) throws IOException {
        HOST = host;
        PORT = port;
        ENV = environment;
    }

    public static void initialize(String username, String host, int port) throws IOException {
        initialize(username, host, port, Environment.PRODUCTION);
    }

    public static void initialize(String username, String host, Environment environment) throws IOException {
        initialize(username, host, PORT, environment);
    }

    public static void initialize(String username, String host) throws IOException {
        initialize(username, host, PORT, Environment.PRODUCTION);
    }


    public static void destroy() {
        HOST = null;
        PORT = 0;
        account = null;
        airtime = null;
        payments = null;
        sms = null;
    }

    public static SMSService getSmsService() throws IOException {
        if (sms == null) {
            sms = new SMSService();
            return sms;
        }
        return sms;
    }

    public static AirtimeService getAirtimeService() throws IOException {
        if (airtime == null) {
            airtime = new AirtimeService();
        }
        return airtime;
    }

    public static PaymentsService getPaymentsService() throws IOException {
        if (payments == null) {
            payments = new PaymentsService();
        }
        return payments;
    }

    public static AccountService getAccount() throws IOException {
        if (account == null) {
            account = new AccountService();
        }
        return account;
    }

    public static VoiceService getVoiceService(Context context, VoiceService.VoiceListener listener, String sipUsername) throws Exception {
        if (voice == null) {
            voice = new VoiceService(context.getApplicationContext(), listener, sipUsername);
        }
        return voice;
    }

    public static VoiceService getVoiceService() {
        if (voice == null) throw new RuntimeException("VoiceService is not initialized");
        return voice;
    }

    public static VoiceService getVoiceService(Context context, VoiceService.VoiceListener listener) throws Exception {
        return getVoiceService(context, listener, null);
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
