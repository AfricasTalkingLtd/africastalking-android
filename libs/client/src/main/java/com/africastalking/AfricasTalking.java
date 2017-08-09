package com.africastalking;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;

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
        USERNAME = username;
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

    public static VoiceService getVoiceService() throws Exception {
        if (voice == null) {
            voice = new VoiceService();
        }
        return voice;
    }

    /**
     * Bind to voice SIP service, setting the preferred SIP username
     * @param context
     * @param connection
     * @param sipUsername
     */
    public static void bindVoiceBackgroundService(final Context context, final ServiceConnection connection, String sipUsername) {
        Intent intent = new Intent(context, VoiceBackgroundService.class);
        intent.putExtra(VoiceBackgroundService.EXTRA_USERNAME, sipUsername);
        intent.putExtra(VoiceBackgroundService.EXTRA_HOST, AfricasTalking.HOST);
        intent.putExtra(VoiceBackgroundService.EXTRA_PORT, AfricasTalking.PORT);

        // (re)-start
        context.startService(intent);


        // then bind after a while
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                context.bindService(new Intent(context, VoiceBackgroundService.class), connection, 0);
            }
        }, 300);
    }

    /**
     * Bind to voice SIP service
     * @param context
     * @param connection
     */
    public static void bindVoiceBackgroundService(Context context, ServiceConnection connection) {
        bindVoiceBackgroundService(context, connection, null);
    }

    /**
     * Unbind from voice SIP service
     * @param context
     * @param connection
     */
    public static void unbindVoiceBackgroundService(Context context, ServiceConnection connection) {
        context.unbindService(connection);
    }
}
