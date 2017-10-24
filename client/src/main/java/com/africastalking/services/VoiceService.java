package com.africastalking.services;


import android.content.Context;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.util.Log;

import com.africastalking.AfricasTalkingException;
import com.africastalking.utils.Callback;
import com.africastalking.utils.Logger;
import com.africastalking.models.voice.QueueStatus;
import com.africastalking.proto.SdkServerServiceGrpc;
import com.africastalking.proto.SdkServerServiceGrpc.SdkServerServiceBlockingStub;
import com.africastalking.proto.SdkServerServiceOuterClass.*;
import com.africastalking.utils.voice.CallController;
import com.africastalking.utils.voice.CallInfo;
import com.africastalking.utils.voice.CallListener;
import com.africastalking.utils.voice.SipStack;
import com.africastalking.utils.voice.RegistrationListener;

import io.grpc.ManagedChannel;
import retrofit2.Response;

import java.io.IOException;
import java.util.List;

public final class VoiceService extends Service implements CallController {

    private static final String TAG = VoiceService.class.getName();

    static VoiceService sInstance;
    private VoiceServiceInterface mVoiceAPIInterface;

    private static SipStack mSipStack;

    private Logger mLogger = new Logger() {
        @Override
        public void log(String message, Object... args) {
            Log.d(TAG, String.format(message, args));
        }
    };

    VoiceService() throws IOException {
        super();
        initService();
    }

    VoiceService(Context context, RegistrationListener registrationListener) throws IOException {
        super();
        initService();
        initSipStack(context, registrationListener);
    }

    public static VoiceService newInstance(Context context, RegistrationListener registrationListener) throws IOException {
        if (sInstance == null) {
            sInstance = new VoiceService(context, registrationListener);
        }
        return sInstance;
    }

    public static VoiceService getsInstance() {
        return sInstance;
    }

    @Override
    protected VoiceService getInstance() throws IOException {
        if (sInstance == null) {
            throw new IOException("VoiceService not initialized");
        }
        return sInstance;
    }

    @Override
    protected boolean isInitialized() {
        return sInstance != null;
    }


    @Override
    protected void initService() {
        String baseUrl = "https://voice." + (isSandbox ? SANDBOX_DOMAIN : PRODUCTION_DOMAIN) + "/";
        mVoiceAPIInterface = retrofitBuilder.baseUrl(baseUrl).build().create(VoiceServiceInterface.class);
    }

    private void initSipStack(final Context context, final RegistrationListener registrationListener) {
        try {

            AsyncTask<Void, Void, List<SipCredentials>> task = new AsyncTask<Void, Void, List<SipCredentials>>() {

                @Override
                protected void onPostExecute(List<SipCredentials> sipCredentials) {
                    if (sipCredentials != null && sipCredentials.size() > 0) {

                        Log.d(TAG, "Initializing PJSIP...");

                        // TODO: Find a way to select credentials in case of many
                        final SipCredentials credentials = sipCredentials.get(0);

                        try {
                            mSipStack = SipStack.newInstance(context, registrationListener, credentials);
                        } catch (Exception e) {
                            registrationListener.onError(e);
                        }

                    } else {
                        registrationListener.onError(new Exception("Invalid SIP Credentials"));
                    }

                }

                @Override
                protected List<SipCredentials> doInBackground(Void[] objects) {
                    try {
                        Log.d(TAG, "Fetching SIP credentials");
                        ManagedChannel channel = com.africastalking.services.Service.getChannel(HOST, PORT);
                        SdkServerServiceBlockingStub stub = addClientIdentification(SdkServerServiceGrpc.newBlockingStub(channel));
                        SipCredentialsRequest req = SipCredentialsRequest.newBuilder().build();
                        return stub.getSipCredentials(req).getCredentialsList();
                    } catch (Exception ex) {
                        Log.e(TAG, ex.getMessage() + "");
                    }
                    return null;
                }
            };

            task.execute();

        } catch (Exception e) {
            Log.e(TAG, e.getMessage() + "");
        }
    }

    @Override
    public void destroyService() {
        if (mSipStack != null) {
            mSipStack.destroy();
        }
        sInstance = null;
    }


    /**
     * Upload media file. This media file will be played when called upon by one of our mVoiceAPIInterface actions.
     *
     * @param url
     * @return
     * @throws IOException
     */
    public String mediaUpload(String url) throws IOException {
        Response<String> response = mVoiceAPIInterface.mediaUpload(username, url).execute();
        return response.body();
    }

    public void mediaUpload(String url, Callback<String> callback) {
        mVoiceAPIInterface.mediaUpload(username, url).enqueue(makeCallback(callback));
    }

    /**
     * Get queue status
     *
     * @param phoneNumbers
     * @return
     * @throws IOException
     */
    public List<QueueStatus> queueStatus(String phoneNumbers) throws IOException {
        Response<List<QueueStatus>> response = mVoiceAPIInterface.queueStatus(username, phoneNumbers).execute();
        return response.body();
    }

    public void queueStatus(String phoneNumbers, Callback<List<QueueStatus>> callback) {
        mVoiceAPIInterface.queueStatus(username, phoneNumbers).enqueue(makeCallback(callback));
    }


    @Override
    public void registerLogger(Logger logger) {
        mLogger = logger;
        if (mSipStack != null) {
            mSipStack.registerLogger(mLogger);
        }
    }

    @Override
    public void unregisterLogger(Logger logger) {
        mLogger = new Logger() {
            @Override
            public void log(String message, Object... args) {
                Log.d(TAG, String.format(message, args));
            }
        };
        if (mSipStack != null) {
            mSipStack.unregisterLogger(mLogger);
        }
    }

    @Override
    public void registerCallListener(CallListener listener) {
        if (mSipStack != null) {
            mSipStack.registerCallListener(listener);
            return;
        }
        Log.w(TAG, "Failed to register call listener");
    }

    @Override
    public void unregisterCallListener(CallListener listener) {
        if (mSipStack != null) {
            mSipStack.unregisterCallListener(listener);
            Log.w(TAG, "Unregistered call listener");
        }
    }

    /**
     * Start a call
     *
     * @param destination
     * @throws AfricasTalkingException
     */
    @Override
    public void makeCall(String destination) throws AfricasTalkingException {
        if (mSipStack != null && mSipStack.isReady()) {
            mSipStack.makeCall(destination);
        } else {
            throw new AfricasTalkingException("Voice service NOT ready!");
        }
    }

    /**
     * Pick up an incoming call
     *
     * @throws AfricasTalkingException
     */
    @Override
    public void pickCall() throws AfricasTalkingException {
        if (mSipStack != null && mSipStack.isReady()) {
            mSipStack.pickCall();
        } else {
            throw new AfricasTalkingException("Voice service NOT ready!");
        }
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
     *
     * @param speaker
     */
    @Override
    public void setSpeakerMode(Context context, boolean speaker) {
        AudioManager am = ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE));
        am.setSpeakerphoneOn(speaker);
    }


    /**
     * Hold call
     *
     * @throws AfricasTalkingException
     */
    @Override
    public void holdCall() throws AfricasTalkingException {
        mSipStack.holdCall();
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
     *
     * @throws AfricasTalkingException
     */
    @Override
    public void resumeCall() throws AfricasTalkingException {
        mSipStack.resumeCall();
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

    @Override
    public CallInfo getCallInfo() {
        if (mSipStack != null) {
            return mSipStack.getCallInfo();
        }
        return new CallInfo("unknown");
    }

}
