package com.africastalking.services;


import android.content.Context;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.util.Log;

import com.africastalking.AfricasTalkingException;
import com.africastalking.Callback;
import com.africastalking.Environment;
import com.africastalking.models.QueueStatus;
import com.africastalking.proto.SdkServerServiceGrpc;
import com.africastalking.proto.SdkServerServiceOuterClass;
import com.africastalking.services.voice.CallController;
import com.africastalking.services.voice.CallInfo;
import com.africastalking.services.voice.CallListener;
import com.africastalking.services.voice.PJSipStack;
import com.africastalking.services.voice.RegistrationListener;
import com.africastalking.services.voice.SipStack;

import io.grpc.ManagedChannel;
import retrofit2.Response;

import java.io.IOException;
import java.util.List;

public final class VoiceService extends Service implements CallController {

    private static final String TAG = VoiceService.class.getName();

    public static final String INCOMING_CALL = "com.africastalking.mVoiceAPIInterface.INCOMING_CALL";
    static VoiceService sInstance;
    private VoiceServiceInterface mVoiceAPIInterface;

    private static SipStack mSipStack;


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
    protected void fetchToken(String host, int port) throws IOException {
        fetchServiceToken(host, port, SdkServerServiceOuterClass.ClientTokenRequest.Capability.VOICE);
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
        String baseUrl = "https://mVoiceAPIInterface."+ (ENV == Environment.SANDBOX ? SANDBOX_DOMAIN : PRODUCTION_DOMAIN) + "/";
        mVoiceAPIInterface =  retrofitBuilder.baseUrl(baseUrl).build().create(VoiceServiceInterface.class) ;
    }

    private void initSipStack(final Context context, final RegistrationListener registrationListener) {
        try {

            AsyncTask<Void, Void, List<SdkServerServiceOuterClass.SipCredentials>> task = new AsyncTask<Void, Void, List<SdkServerServiceOuterClass.SipCredentials>> () {

                @Override
                protected void onPostExecute(List<SdkServerServiceOuterClass.SipCredentials> sipCredentials) {
                    if (sipCredentials != null && sipCredentials.size() > 0) {

                        Log.d(TAG, "Initializing PJSIP...");

                        // TODO: Find a way to select credentials in case of many
                        SdkServerServiceOuterClass.SipCredentials credentials = sipCredentials.get(0);

                        try {
                            mSipStack = PJSipStack.newInstance(context, registrationListener, credentials);
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage() + "");
                            e.printStackTrace();
                        }
                    } else {
                        Log.e(TAG, "Invalid SIP Credentials");
                    }

                }

                @Override
                protected List<SdkServerServiceOuterClass.SipCredentials> doInBackground(Void[] objects) {
                    try {
                        Log.d(TAG, "Fetching SIP credentials");
                        ManagedChannel channel = com.africastalking.services.Service.getChannel(HOST, PORT);
                        SdkServerServiceGrpc.SdkServerServiceBlockingStub stub = SdkServerServiceGrpc.newBlockingStub(channel);
                        SdkServerServiceOuterClass.SipCredentialsRequest req = SdkServerServiceOuterClass.SipCredentialsRequest.newBuilder().build();
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

    @Override
    public void destroyService() {
        mSipStack.destroy();
        sInstance = null;
    }



    /**
     * Upload media file. This media file will be played when called upon by one of our mVoiceAPIInterface actions.
     * @param url
     * @return
     * @throws IOException
     */
    public String mediaUpload(String url) throws IOException {
        Response<String> response = mVoiceAPIInterface.mediaUpload(USERNAME, url).execute();
        return response.body();
    }

    public void mediaUpload(String url, Callback<String> callback) {
        mVoiceAPIInterface.mediaUpload(USERNAME, url).enqueue(makeCallback(callback));
    }

    /**
     * Get queue status
     * @param phoneNumbers
     * @return
     * @throws IOException
     */
    public List<QueueStatus> queueStatus(String phoneNumbers) throws IOException {
        Response<List<QueueStatus>> response = mVoiceAPIInterface.queueStatus(USERNAME, phoneNumbers).execute();
        return response.body();
    }

    public void queueStatus(String phoneNumbers, Callback<List<QueueStatus>> callback) {
        mVoiceAPIInterface.queueStatus(USERNAME, phoneNumbers).enqueue(makeCallback(callback));
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
    public void setSpeakerMode(Context context, boolean speaker) {
        AudioManager am = ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE));
        am.setSpeakerphoneOn(speaker);
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

    @Override
    public CallInfo getCallInfo() {
        if (mSipStack != null) {
            return mSipStack.getCallInfo();
        }
        return new CallInfo("unknown");
    }

}
