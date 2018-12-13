package com.africastalking;

import android.Manifest;
import android.app.Activity;
import android.text.TextUtils;

import com.africastalking.services.ApplicationService;
import com.africastalking.services.AirtimeService;
import com.africastalking.services.PaymentService;
import com.africastalking.services.Service;
import com.africastalking.services.SmsService;
import com.africastalking.services.TokenService;
import com.africastalking.services.VoiceService;
import com.africastalking.utils.Callback;
import com.africastalking.utils.Logger;
import com.africastalking.utils.voice.RegistrationListener;
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

    private static String sClientId = null;

    @Deprecated
    public static String hostOverride = null;

    private static final List<String> PERMISSION_LIST = Arrays.asList(
            Manifest.permission.INTERNET,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.MODIFY_AUDIO_SETTINGS,
            Manifest.permission.ACCESS_NETWORK_STATE
    );


    public static void initialize(String host, int port, boolean disableTLS) {
        Service.HOST = host;
        Service.PORT = port;
        Service.DISABLE_TLS = disableTLS;
    }

    public static void initialize(String host, int port) {
        initialize(host, port, false);
    }

    public static void initialize(String host) {
        initialize(host, Service.PORT, false);
    }

    public static void setClientId(String clientId) {
        sClientId = clientId;
    }

    public static String getClientId() {
        return sClientId;
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

    /*
    public static AccountService getAccountService() throws IOException {
        return Service.newInstance("application");
    } */

    public static ApplicationService getApplicationService() throws IOException {
        return Service.newInstance("application");
    }

    public static TokenService getTokenService() throws IOException {
        return Service.newInstance("token");
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
                            callback.onFailure(e);
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
