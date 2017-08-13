package com.africastalking.services;


import com.africastalking.AfricasTalking;
import com.africastalking.Callback;
import com.africastalking.Environment;
import com.africastalking.models.QueueStatus;
import com.africastalking.proto.SdkServerServiceOuterClass;
import retrofit2.Response;

import java.io.IOException;
import java.util.List;

public final class VoiceService extends Service {

    private static VoiceService sInstance;
    private VoiceServiceInterface voice;


    public VoiceService() throws IOException {
        super();
        initService();
    }


    @Override
    protected void fetchToken(String host, int port) throws IOException {
        fetchServiceToken(host, port, SdkServerServiceOuterClass.ClientTokenRequest.Capability.VOICE);
    }

    @Override
    protected VoiceService getInstance() throws IOException {
        if (sInstance == null) {
            sInstance = new VoiceService();
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
        String baseUrl = "https://voice."+ (ENV == Environment.SANDBOX ? SANDBOX_DOMAIN : PRODUCTION_DOMAIN) + "/";
        voice =  retrofitBuilder.baseUrl(baseUrl).build().create(VoiceServiceInterface.class) ;
    }

    @Override
    protected void destroyService() {
        sInstance = null;
    }



    /**
     * Upload media file. This media file will be played when called upon by one of our voice actions.
     * @param url
     * @return
     * @throws IOException
     */
    public String mediaUpload(String url) throws IOException {
        Response<String> response = voice.mediaUpload(USERNAME, url).execute();
        return response.body();
    }

    public void mediaUpload(String url, Callback<String> callback) {
        voice.mediaUpload(USERNAME, url).enqueue(makeCallback(callback));
    }

    /**
     * Get queue status
     * @param phoneNumbers
     * @return
     * @throws IOException
     */
    public List<QueueStatus> queueStatus(String phoneNumbers) throws IOException {
        Response<List<QueueStatus>> response = voice.queueStatus(USERNAME, phoneNumbers).execute();
        return response.body();
    }

    public void queueStatus(String phoneNumbers, Callback<List<QueueStatus>> callback) {
        voice.queueStatus(USERNAME, phoneNumbers).enqueue(makeCallback(callback));
    }
}
