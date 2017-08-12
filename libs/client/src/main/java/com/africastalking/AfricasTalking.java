package com.africastalking;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.africastalking.voice.VoiceBackgroundService;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;


public final class AfricasTalking {

    private static final String TAG = AfricasTalking.class.getName();

    static String USERNAME;
    static String HOST;
    static int PORT = 35897;

    private static final List<String> PERMISSION_LIST = Arrays.asList(
            Manifest.permission.INTERNET,
            Manifest.permission.USE_SIP,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.MODIFY_AUDIO_SETTINGS,
            Manifest.permission.ACCESS_WIFI_STATE
    );

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
    public static void bindVoiceBackgroundService(final Activity context, final ServiceConnection connection, final String sipUsername, final String sipStack) {

        // Permissions
        Dexter.withActivity(context)
                .withPermissions(PERMISSION_LIST)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if (!report.areAllPermissionsGranted()) {
                            Log.e(TAG, "The following permissions are required: \n" + TextUtils.join("\n", PERMISSION_LIST));
                            connection.onServiceDisconnected(context.getComponentName());
                            return;
                        }

                        Intent intent = new Intent(context, VoiceBackgroundService.class);
                        intent.putExtra(VoiceBackgroundService.EXTRA_USERNAME, sipUsername);
                        intent.putExtra(VoiceBackgroundService.EXTRA_HOST, AfricasTalking.HOST);
                        intent.putExtra(VoiceBackgroundService.EXTRA_PORT, AfricasTalking.PORT);
                        intent.putExtra(VoiceBackgroundService.EXTRA_SIP_STACK, sipStack);

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

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }
                })
                .withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Log.e(TAG, error.name());
                        Log.e(TAG, "The following permissions are required: " + TextUtils.join("\n", PERMISSION_LIST));
                        connection.onServiceDisconnected(context.getComponentName());
                        return;
                    }
                })
                .onSameThread()
                .check();
    }

    /**
     * Bind to voice SIP service
     * @param context
     * @param connection
     */
    public static void bindVoiceBackgroundService(Activity context, ServiceConnection connection) {
        bindVoiceBackgroundService(context, connection, null, null);
    }

    /**
     * Unbind from voice SIP service
     * @param context
     * @param connection
     */
    public static void unbindVoiceBackgroundService(Activity context, ServiceConnection connection) {
        try {
            context.unbindService(connection);
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage() + "");
        }
    }
}
