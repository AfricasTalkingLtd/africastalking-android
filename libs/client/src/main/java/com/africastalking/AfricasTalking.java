package com.africastalking;

import android.Manifest;
import android.app.Activity;
import android.text.TextUtils;

import com.africastalking.services.AccountService;
import com.africastalking.services.AirtimeService;
import com.africastalking.services.PaymentService;
import com.africastalking.services.Service;
import com.africastalking.services.SmsService;
import com.africastalking.services.VoiceService;
import com.africastalking.services.voice.RegistrationListener;
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

    private static final List<String> PERMISSION_LIST = Arrays.asList(
            Manifest.permission.INTERNET,
            Manifest.permission.USE_SIP,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.MODIFY_AUDIO_SETTINGS,
            Manifest.permission.ACCESS_WIFI_STATE
    );


    public static void initialize(String username, String host, int port, Environment environment) throws IOException {
        Service.HOST = host;
        Service.PORT = port;
        Service.USERNAME = username;
        Service.ENV = environment;
    }

    public static void initialize(String username, String host, int port) throws IOException {
        initialize(username, host, port, Environment.PRODUCTION);
    }

    public static void initialize(String username, String host, Environment environment) throws IOException {
        initialize(username, host, Service.PORT, environment);
    }

    public static void initialize(String username, String host) throws IOException {
        initialize(username, host, Service.PORT, Environment.PRODUCTION);
    }

    public static void setEnvironment(Environment env) {
        Service.ENV = env;
    }

    private static void enableLogging(boolean enable) {
        Service.LOGGING = enable;
    }

    public static void setLogger(Logger logger) {
        if (logger != null) {
            enableLogging(true);
        }
        Service.LOGGER = logger;
    }

    public static SmsService getSmsService() throws IOException {
        return Service.newInstance("sms");
    }

    public static AirtimeService getAirtimeService() throws IOException {
        return Service.newInstance("airtime");
    }

    public static PaymentService getPaymentService() throws IOException {
        return Service.newInstance("payment");
    }

    public static AccountService getAccountService() throws IOException {
        return Service.newInstance("account");
    }

    public static VoiceService getVoiceService() throws IOException {
        VoiceService service = VoiceService.getsInstance();
        if (service == null){
            throw new IOException("Voice service was not initialized; call AfircasTalking.initializeVoiceService() first");
        }
        return service;
    }


    /**
     * Initialize voice service
     * @param context
     * @param registrationListener
     * @param callback
     * @throws Exception
     */
    public static void initializeVoiceService(final Activity context, final RegistrationListener registrationListener, final Callback<VoiceService> callback) throws Exception {

        if (callback == null) {
            throw new Exception("callback cannot be null");
        }

        if (registrationListener == null) {
            throw new Exception("registrationListener cannot be null");
        }

        // Permissions
        Dexter.withActivity(context)
                .withPermissions(PERMISSION_LIST)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if (!report.areAllPermissionsGranted()) {
                            callback.onFailure(new Exception("The following permissions are required: \n" + TextUtils.join("\n", PERMISSION_LIST)));
                            return;
                        }

                        try {
                            VoiceService service = VoiceService.newInstance(context.getApplicationContext(), registrationListener);
                            callback.onSuccess(service);
                        } catch (IOException e) {
                            callback.onFailure(e);;
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }
                })
                .withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        callback.onFailure(new Exception("The following permissions are required: \n" + TextUtils.join("\n", PERMISSION_LIST)));
                    }
                })
                .onSameThread()
                .check();
    }
}
