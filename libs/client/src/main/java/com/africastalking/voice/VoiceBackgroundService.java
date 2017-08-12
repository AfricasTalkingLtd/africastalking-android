package com.africastalking.voice;

import android.app.Service;
import android.content.Intent;
import android.net.sip.SipException;
import android.net.sip.SipManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.africastalking.AfricasTalkingException;
import com.africastalking.proto.SdkServerServiceGrpc;
import com.africastalking.proto.SdkServerServiceGrpc.SdkServerServiceBlockingStub;
import com.africastalking.proto.SdkServerServiceOuterClass.SipCredentials;
import com.africastalking.proto.SdkServerServiceOuterClass.SipCredentialsRequest;
import io.grpc.ManagedChannel;

import java.text.ParseException;
import java.util.List;


public final class VoiceBackgroundService extends Service implements CallController {

    private static final String TAG = VoiceBackgroundService.class.getName();

    public static final String EXTRA_HOST = "host";
    public static final String EXTRA_PORT = "port";
    public static final String EXTRA_USERNAME = "username";

    public static final String INCOMING_CALL = "com.africastalking.voice.INCOMING_CALL";

    static RegistrationListener mRegistrationListener;

    private static SipStack mSipStack;
    private VoiceServiceBinder mBinder = new VoiceServiceBinder();


    public final class VoiceServiceBinder extends Binder {
        public VoiceBackgroundService getService() {
            return VoiceBackgroundService.this;
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        if (mSipStack == null || !mSipStack.isReady()) {
            try {
                String username = null;
                String host = null;
                int port = -1;
                if (intent != null) {
                    username = intent.getStringExtra(EXTRA_USERNAME);
                    host = intent.getStringExtra(EXTRA_HOST);
                    port = intent.getIntExtra(EXTRA_PORT, port);
                }

                if (host == null) {
                    Log.e(TAG, "No SDK host, shutting down...");
                    return START_NOT_STICKY;
                }


                final String hostname = host;
                final int portNumber = port;
                final String sipUsername = username;

                AsyncTask<Void, Void, List<SipCredentials>> task = new AsyncTask<Void, Void, List<SipCredentials>> () {

                    @Override
                    protected void onPostExecute(List<SipCredentials> sipCredentials) {
                        if (sipCredentials != null) {
                            SipCredentials credentials = sipCredentials.get(0);

                            if (sipUsername != null) {
                                for(SipCredentials cred:sipCredentials) {
                                    if (cred.getUsername().contentEquals(sipUsername)){
                                        credentials = cred;
                                        break;
                                    }
                                }
                                throw new RuntimeException("Invalid username: " + sipUsername);
                            }


                            try {

                                boolean useAndroidSip = isAndroidSipAvailable(); // HUH: Use PJSIP by default?
                                if (useAndroidSip) {
                                    mSipStack = AndroidSipStack.newInstance(VoiceBackgroundService.this, credentials);
                                } else {
                                    mSipStack = PJSipStack.newInstance(VoiceBackgroundService.this, credentials);
                                }

                            } catch (ParseException | SipException e) {
                                Log.e(TAG, e.getMessage() + "");
                                e.printStackTrace();
                            }
                        }

                    }

                    @Override
                    protected List<SipCredentials> doInBackground(Void[] objects) {
                        try {
                            ManagedChannel channel = com.africastalking.Service.getChannel(hostname, portNumber);
                            SdkServerServiceBlockingStub stub = SdkServerServiceGrpc.newBlockingStub(channel);
                            SipCredentialsRequest req = SipCredentialsRequest.newBuilder().build();
                            return stub.getSipCredentials(req).getCredentialsList();
                        } catch (Exception ex) {
                            Log.e(TAG, ex.getMessage() + "");
                            ex.printStackTrace();
                        }
                        return null;
                    }
                };

                task.execute();

            } catch (Exception e) {
                Log.e(TAG, e.getMessage() + "");
            }
        }


        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    private Boolean isAndroidSipAvailable() {
        return SipManager.isApiSupported(this) && SipManager.isVoipSupported(this);
    }


    @Override
    public void onDestroy() {
        mSipStack.destroy(this);
        super.onDestroy();
    }


    /**
     * Set the registration listener
     * @param listener
     */
    public void setRegistrationListener(RegistrationListener listener) {
        if (listener != null) {
            mRegistrationListener = listener;
            if (mSipStack != null && mSipStack.isReady()) {
                mRegistrationListener.onCompleteRegistration();
            }
        }
    }

    @Override
    public void setCallListener(CallListener listener) {
        if (mSipStack != null) {
            mSipStack.setCallListener(listener);
        }
    }

    /**
     * Start a call
     * @param listener
     * @param destination
     * @param timeout
     * @throws AfricasTalkingException
     */
    @Override
    public void makeCall(String destination, int timeout, CallListener listener) throws AfricasTalkingException {
        if (mSipStack != null && mSipStack.isReady()) {
            mSipStack.makeCall(destination, timeout, listener);
        } else {
            throw new AfricasTalkingException("Voice service NOT ready!");
        }
    }

    public void makeCall(String destination, CallListener listener) throws AfricasTalkingException {
        makeCall(destination, 30, listener);
    }


    /**
     * Pick up an incoming call
     * @param listener
     * @param timeout
     * @throws AfricasTalkingException
     */
    @Override
    public void pickCall(int timeout, CallListener listener) throws AfricasTalkingException {
        if (mSipStack != null && mSipStack.isReady()) {
            mSipStack.pickCall(timeout, listener);
        } else {
            throw new AfricasTalkingException("Voice service NOT ready!");
        }
    }

    public void pickCall(CallListener listener) throws AfricasTalkingException {
        pickCall(30, listener);
    }

    /**
     * Toggle call mute mode
     */
    @Override
    public void toggleMute() {
        mSipStack.toggleMute();
    }


    /**
     * Set call in speaker or ear-peace mode
     * @param speaker
     */
    @Override
    public void setSpeakerMode(boolean speaker) {
        mSipStack.setSpeakerMode(speaker);
    }


    /**
     * Hold call
     * @param timeout
     * @throws AfricasTalkingException
     */
    @Override
    public void holdCall(int timeout) throws AfricasTalkingException {
        mSipStack.holdCall(timeout);
    }

    public void holdCall() throws AfricasTalkingException {
        mSipStack.holdCall(30);
    }


    /**
     * Start audio on call
     */
    @Override
    public void startAudio() {
        mSipStack.startAudio();
    }

    /**
     * Resume a held call
     * @param timeout
     * @throws AfricasTalkingException
     */
    @Override
    public void resumeCall(int timeout) throws AfricasTalkingException {
        mSipStack.resumeCall(timeout);
    }

    public void resumeCall() throws AfricasTalkingException {
        mSipStack.resumeCall(30);
    }


    @Override
    public void endCall() throws AfricasTalkingException {
        mSipStack.endCall();
    }

    @Override
    public void sendDtmf(char character) {
        mSipStack.sendDtmf(character);
    }

    @Override
    public boolean isCallInProgress() {
        return mSipStack.isCallInProgress();
    }
}
