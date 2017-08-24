package com.africastalking.services.voice;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.africastalking.AfricasTalkingException;
import com.africastalking.BuildConfig;
import com.africastalking.proto.SdkServerServiceOuterClass.SipCredentials;
import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.birbit.android.jobqueue.config.Configuration;

import org.pjsip.pjsua2.Account;
import org.pjsip.pjsua2.AccountConfig;
import org.pjsip.pjsua2.AccountNatConfig;
import org.pjsip.pjsua2.AudDevManager;
import org.pjsip.pjsua2.AudioMedia;
import org.pjsip.pjsua2.AuthCredInfo;
import org.pjsip.pjsua2.Call;
import org.pjsip.pjsua2.CallMediaInfo;
import org.pjsip.pjsua2.CallMediaInfoVector;
import org.pjsip.pjsua2.CallOpParam;
import org.pjsip.pjsua2.CallSendRequestParam;
import org.pjsip.pjsua2.CallSetting;
import org.pjsip.pjsua2.Endpoint;
import org.pjsip.pjsua2.EpConfig;
import org.pjsip.pjsua2.LogEntry;
import org.pjsip.pjsua2.LogWriter;
import org.pjsip.pjsua2.Media;
import org.pjsip.pjsua2.OnCallMediaStateParam;
import org.pjsip.pjsua2.OnCallStateParam;
import org.pjsip.pjsua2.OnIncomingCallParam;
import org.pjsip.pjsua2.OnIncomingSubscribeParam;
import org.pjsip.pjsua2.OnInstantMessageParam;
import org.pjsip.pjsua2.OnInstantMessageStatusParam;
import org.pjsip.pjsua2.OnMwiInfoParam;
import org.pjsip.pjsua2.OnNatCheckStunServersCompleteParam;
import org.pjsip.pjsua2.OnNatDetectionCompleteParam;
import org.pjsip.pjsua2.OnRegStartedParam;
import org.pjsip.pjsua2.OnRegStateParam;
import org.pjsip.pjsua2.OnSelectAccountParam;
import org.pjsip.pjsua2.OnTransportStateParam;
import org.pjsip.pjsua2.OnTypingIndicationParam;
import org.pjsip.pjsua2.SipEvent;
import org.pjsip.pjsua2.SipTxOption;
import org.pjsip.pjsua2.StringVector;
import org.pjsip.pjsua2.TransportConfig;
import org.pjsip.pjsua2.UaConfig;
import org.pjsip.pjsua2.pj_log_decoration;
import org.pjsip.pjsua2.pj_qos_type;
import org.pjsip.pjsua2.pjmedia_type;
import org.pjsip.pjsua2.pjsip_inv_state;
import org.pjsip.pjsua2.pjsip_status_code;
import org.pjsip.pjsua2.pjsip_transport_type_e;
import org.pjsip.pjsua2.pjsua2;
import org.pjsip.pjsua2.pjsua_call_flag;
import org.pjsip.pjsua2.pjsua_call_media_status;
import org.pjsip.pjsua2.pjsua_state;
import org.pjsip.pjsua2.pjsua_stun_use;

import java.util.HashSet;
import java.util.Set;

/**
 * Copyright (c) 2017 Salama AB
 * All rights reserved
 * Contact: aksalj@aksalj.me
 * Website: http://www.aksalj.me
 * <p>
 * Project : dev-mvp
 * File : PJSipStack
 * Date : 8/12/17 10:35 AM
 * Description :
 */
public class PJSipStack extends SipStack {

    private static final String TAG = PJSipStack.class.getName();
    private static final String AGENT_NAME = "Africa's Talking/" + BuildConfig.VERSION_NAME + "-" + BuildConfig.VERSION_CODE ;
    private static final String PJSUA_LIBRARY = "pjsua2";
    private static final int LOG_LEVEL = 6;

    private static PJSipStack sInstance = null;

    private static Set<CallListener> mCallListeners = new HashSet<>();

    private static Endpoint sEndPoint = null;

    private Account mAccount = null;
    private TransportConfig mSipTransportConfig = null;

    private JobManager mJobManager;
    private LogWriter mLogWriter;


    PJSipStack(Context context, final RegistrationListener registrationListener, final SipCredentials credentials) throws Exception {
        super(credentials);

        System.loadLibrary(PJSUA_LIBRARY);

        // Register
        sEndPoint = new Endpoint() {

            @Override
            public void onSelectAccount(OnSelectAccountParam prm) {
                super.onSelectAccount(prm);
                Log.d(TAG, "onSelectAccount: \n" + prm.getRdata().getWholeMsg());

            }

            @Override
            public void onNatCheckStunServersComplete(OnNatCheckStunServersCompleteParam prm) {
                super.onNatCheckStunServersComplete(prm);
                Log.wtf(TAG, "onNatCheckStunServersComplete: " + prm.getAddr() + " -> " + prm.getName() + " -> " + prm.getStatus());
            }

            @Override
            public void onNatDetectionComplete(OnNatDetectionCompleteParam prm) {
                super.onNatDetectionComplete(prm);
                Log.wtf(TAG, "onNatDetectionComplete: " + prm.getNatTypeName() + " -> " + prm.getReason() + " -> " + prm.getStatus());
            }

            @Override
            public void onTransportState(OnTransportStateParam prm) {
                super.onTransportState(prm);
                Log.wtf(TAG, "onTransportState: " + prm.getState().toString());
            }

        };
        sEndPoint.libCreate();
        EpConfig config = new EpConfig();

        // logging
        mJobManager = new JobManager(new Configuration.Builder(context).build());
        mLogWriter = new LogWriter(){
            @Override
            public void write(LogEntry entry) {
                if (entry != null) {
                    mJobManager.addJobInBackground(new LogJob(entry.getMsg()));
                }
            }
        };
        config.getLogConfig().setMsgLogging(LOG_LEVEL);
        config.getLogConfig().setLevel(LOG_LEVEL);
        config.getLogConfig().setConsoleLevel(LOG_LEVEL);
        config.getLogConfig().setWriter(mLogWriter);
        config.getLogConfig().setDecor(config.getLogConfig().getDecor() & 
             ~(pj_log_decoration.PJ_LOG_HAS_CR.swigValue() | 
             pj_log_decoration.PJ_LOG_HAS_NEWLINE.swigValue()));


        // user-agent
        UaConfig uaConfig = config.getUaConfig();
        uaConfig.setUserAgent(AGENT_NAME);
        StringVector stunServer = new StringVector();
        stunServer.add("stun.l.google.com:19302");
        stunServer.add("stun.pjsip.org");
        stunServer.add("media4-angani-ke-host.africastalking.com:443");
        uaConfig.setStunServer(stunServer);
        // uaConfig.setThreadCnt(1);

        sEndPoint.libInit(config);

        mSipTransportConfig = new TransportConfig();
        // mSipTransportConfig.setPort(credentials.getPort());
        mSipTransportConfig.setQosType(pj_qos_type.PJ_QOS_TYPE_VOICE);
        
        sEndPoint.transportCreate(pjsip_transport_type_e.PJSIP_TRANSPORT_UDP, mSipTransportConfig);

        sEndPoint.libStart();

        Log.d(TAG,  "Loading account...");
        loadAccount(registrationListener, credentials);

        sInstance = this;
    }

    public static PJSipStack newInstance(Context context, RegistrationListener registrationListener, SipCredentials credentials) throws Exception {
        if (sInstance != null && sInstance.isReady()) {
            sInstance.loadAccount(registrationListener, credentials);
            return sInstance;
        }
        return new PJSipStack(context, registrationListener, credentials);
    }

    public void libRegisterThread(String threadName) throws Exception {
        if (sEndPoint != null && sEndPoint.libGetState() == pjsua_state.PJSUA_STATE_RUNNING) {
            sEndPoint.libRegisterThread(threadName);
        }
        throw new Exception("Failed to register thread: " + threadName);
    }

    private void loadAccount(final RegistrationListener registrationListener, SipCredentials credentials) throws Exception {

        final AccountConfig accfg = new AccountConfig();

        AccountNatConfig natcfg = accfg.getNatConfig();
        natcfg.setIceEnabled(true);

        accfg.setIdUri("sip:" + credentials.getUsername() + "@" + credentials.getHost());
        accfg.getRegConfig().setRegistrarUri("sip:" + credentials.getHost() + ":" +credentials.getPort());

        AuthCredInfo credInfo = new AuthCredInfo("digest", "*", credentials.getUsername(), 0, credentials.getPassword());
        accfg.getSipConfig().getAuthCreds().add(credInfo);

        if (mAccount != null) {
            mAccount.delete();
        }

        mAccount = new Account() {
            @Override
            public void onRegStarted(OnRegStartedParam prm) {
                super.onRegStarted(prm);
                Log.d(TAG, "Registration Started");
                setReady(false);
                if (registrationListener != null) {
                    registrationListener.onStarting();
                }
            }

            @Override
            public void onRegState(OnRegStateParam prm) {
                super.onRegState(prm);

                pjsip_status_code code = prm.getCode();

                boolean registered =  code == pjsip_status_code.PJSIP_SC_OK;
                setReady(registered);

                if (registrationListener != null) {
                    if (registered) {
                        Log.d(TAG, "Registration complete; Ready to make calls!");
                        registrationListener.onComplete();
                    } else {
                        Log.d(TAG, "Registration Failed");
                        registrationListener.onError(new Exception(prm.getReason()));
                    }
                }
            }

            @Override
            public void onIncomingCall(OnIncomingCallParam prm) {
                try {
                    SipCall call = SipCall.newInstance(mAccount, prm.getCallId());
                    CallOpParam callOpParam = new CallOpParam();
                    callOpParam.setStatusCode(pjsip_status_code.PJSIP_SC_RINGING);
                    call.answer(callOpParam);

                    CallInfo callInfo = new CallInfo(call.getInfo());
                    for (CallListener listener : mCallListeners) {
                        listener.onIncomingCall(callInfo);
                    }

                } catch (Exception ex) {
                    Log.e(TAG, ex.getMessage() + "");
                    for (CallListener listener : mCallListeners) {
                        listener.onError(null, 500, ex.getMessage());
                    }
                }
            }

            @Override
            public void onIncomingSubscribe(OnIncomingSubscribeParam prm) {
                super.onIncomingSubscribe(prm);
                Log.d(TAG, "onIncomingSubscribe: \n" + prm.getRdata().getWholeMsg());
            }

            @Override
            public void onInstantMessage(OnInstantMessageParam prm) {
                super.onInstantMessage(prm);
                Log.d(TAG, "onInstantMessage: \n" + prm.getRdata().getWholeMsg());
            }

            @Override
            public void onInstantMessageStatus(OnInstantMessageStatusParam prm) {
                super.onInstantMessageStatus(prm);
                Log.d(TAG, "onInstantMessageStatus: \n" + prm.getRdata().getWholeMsg());
            }

            @Override
            public void onTypingIndication(OnTypingIndicationParam prm) {
                super.onTypingIndication(prm);
                Log.d(TAG, "onTypingIndication: \n" + prm.getRdata().getWholeMsg());
            }

            @Override
            public void onMwiInfo(OnMwiInfoParam prm) {
                super.onMwiInfo(prm);
                Log.d(TAG, "onMwiInfo: \n" + prm.getRdata().getWholeMsg());
            }
        };
        mAccount.create(accfg);
    }

    @Override
    public void destroy() {
        try {
            if (mAccount != null) {
                mAccount.delete();
            }
            sEndPoint.libDestroy();
            sEndPoint.delete();
            setReady(false);
        } catch(Exception ex) {
            Log.e(TAG, ex.getMessage() + "");
        }
    }

    @Override
    public void registerCallListener(CallListener listener) {
        mCallListeners.add(listener);
    }

    @Override
    public void unregisterCallListener(CallListener listener) {
        mCallListeners.remove(listener);
    }

    @Override
    public void makeCall(final String destination) {
        try {
            SipCall call = SipCall.newInstance(mAccount, -1);
            String recipient = isSipUri(destination) ? destination : ("sip:" + destination + "@" + mCredentials.getHost());
            call.makeCall(recipient, new CallOpParam());
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage() + "");
            for (CallListener listener : mCallListeners) {
                listener.onError(null, 500, ex.getMessage());
            }
        }
    }

    @Override
    public void pickCall() throws AfricasTalkingException {

        if (isCallInProgress()) throw new AfricasTalkingException("A call is already in progress");

        SipCall call = SipCall.getCurrentCall();

        if (call != null) {
            try {
                CallOpParam param = new CallOpParam();
                param.setStatusCode(pjsip_status_code.PJSIP_SC_OK);
                call.answer(param);
            } catch (Exception e) {
                for (CallListener listener : mCallListeners) {
                    listener.onError(null, 500, e.getMessage());
                }
            }
        }
    }

    @Override
    public void holdCall() throws AfricasTalkingException {
        SipCall call = SipCall.getCurrentCall();
        if (call == null) {
            return;
        }
        call.setHold(true);
    }

    @Override
    public void resumeCall() throws AfricasTalkingException {
        SipCall call = SipCall.getCurrentCall();
        if (call == null) {
            return;
        }
        call.setHold(false);
    }

    @Override
    public void endCall() throws AfricasTalkingException {
        SipCall call = SipCall.getCurrentCall();
        if (call != null) {
            try {
                CallOpParam param = new CallOpParam();
                param.setStatusCode(pjsip_status_code.PJSIP_SC_DECLINE);
                if (isCallInProgress()) {
                    call.hangup(param);
                } else { // decline incoming
                    call.answer(param);
                }
            } catch (Exception e) {
                throw new AfricasTalkingException(e);
            }
        }
    }

    @Override
    public void sendDtmf(char character) {
        SipCall call = SipCall.getCurrentCall();
        if (call != null && isCallInProgress()) {
            try {
                CallSendRequestParam prm = new CallSendRequestParam();
                prm.setMethod("INFO");
                SipTxOption txo = new SipTxOption();
                /*txo.setContentType("application/dtmf-relay");
                txo.setMsgBody("Signal=" + character + "\n" + "Duration=160");*/
                txo.setContentType("application/dtmf");
                txo.setMsgBody(String.valueOf(character));
                prm.setTxOption(txo);
                call.sendRequest(prm);
            } catch (Exception e) {
                Log.d(TAG, e.getMessage() + "");
                for (CallListener listener : mCallListeners) {
                    listener.onError(null, 500, e.getMessage());
                }
            }
        }
    }

    @Override
    public boolean isCallInProgress() {
        SipCall call = SipCall.getCurrentCall();
        try {
            return call != null && call.getInfo().getLastStatusCode() == pjsip_status_code.PJSIP_SC_OK; // FIXME!
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void startAudio() { }

    @Override
    public void toggleMute() {
        SipCall call = SipCall.getCurrentCall();
        if (call == null) {
            return;
        }
        call.toggleMute();
    }

    @Override
    public CallInfo getCallInfo() {
        SipCall call = SipCall.getCurrentCall();
        if (call != null) {
            try {
                return new CallInfo(call.getInfo());
            } catch (Exception e) {
                Log.e(TAG, e.getMessage() + "");
            }
        }
        return new CallInfo("unknown");
    }

    @Override
    public void setSpeakerMode(Context cxt, boolean speaker) { }


    private static class SipCall extends Call {

        private static SipCall activeCall = null;

        boolean localHold = false;
        boolean localMute = false;

        private SipCall(Account account) {
            super(account);
        }

        private SipCall(Account account, int call_id) {
            super(account, call_id);
        }

        static SipCall newInstance(Account account, int call_id) throws Exception {
            if (activeCall != null) {
                throw new Exception("An instance of SipCall already exists");
            }
            if (call_id != -1) {
                activeCall = new SipCall(account, call_id);
            } else {
                activeCall = new SipCall(account);
            }
            return activeCall;
        }

        static SipCall getCurrentCall() {
            if (activeCall != null && activeCall.isActive()) {
                return activeCall;
            }
            return null;
        }

        @Override
        public void onCallState(OnCallStateParam prm) {

            try {
                SipEvent evt = prm.getE();
                Log.d(TAG + " -> Event", evt.getType().toString());

                org.pjsip.pjsua2.CallInfo pjcallInfo = getInfo();
                pjsip_inv_state callState = pjcallInfo.getState();
                pjsip_status_code code = null;
                CallInfo callInfo = new CallInfo("Unknown");
                try {
                    callInfo = makeCallInfo(pjcallInfo);
                    code = pjcallInfo.getLastStatusCode();
                } catch (Exception ex) { }

                if(callState == pjsip_inv_state.PJSIP_INV_STATE_CALLING) {

                    Log.d(TAG + " -> Session", "Calling: " + pjcallInfo.getRemoteUri());

                    for (CallListener listener : mCallListeners) {
                        listener.onCalling(callInfo);
                    }
                }
                else if (callState == pjsip_inv_state.PJSIP_INV_STATE_CONNECTING) {
                    Log.d(TAG + " -> Session", "Connecting: " + pjcallInfo.getRemoteUri());

                    for (CallListener listener : mCallListeners) {
                        listener.onCalling(callInfo);
                    }
                }
                else if (callState == pjsip_inv_state.PJSIP_INV_STATE_EARLY) {
                    Log.d(TAG + " -> Session", "Early: " + code);

                    if (code == pjsip_status_code.PJSIP_SC_RINGING) {
                        for (CallListener listener : mCallListeners) {
                            listener.onRinging(callInfo);
                        }
                    }
                }
                else if (callState == pjsip_inv_state.PJSIP_INV_STATE_NULL) {
                    Log.d(TAG + " -> Session", "Null: " + code);
                }
                else if (callState == pjsip_inv_state.PJSIP_INV_STATE_INCOMING) {
                    Log.d(TAG + " -> Session", "Incoming: " + code);
                }
                else if(callState == pjsip_inv_state.PJSIP_INV_STATE_CONFIRMED) {
                    // TODO: RingBack, Hold?


                    Log.d(TAG + " -> Session", "Confirmed: " + code);

                    if (code == pjsip_status_code.PJSIP_SC_OK) {
                        for (CallListener listener : mCallListeners) {
                            listener.onCallEstablished(callInfo);
                        }
                    }

                    if (code == pjsip_status_code.PJSIP_SC_RINGING) {
                        for (CallListener listener : mCallListeners) {
                            listener.onRinging(callInfo);
                        }
                    }

                    if (code == pjsip_status_code.PJSIP_SC_NOT_FOUND) {
                        for (CallListener listener : mCallListeners) {
                            listener.onError(callInfo, 404, "Not Found");
                        }
                    }

                    // ... more statuses

                }
                else if(callState == pjsip_inv_state.PJSIP_INV_STATE_DISCONNECTED) {

                    try {

                        Log.d(TAG + " -> Session", "Disconnected: " + code);

                        if (code == pjsip_status_code.PJSIP_SC_BUSY_HERE || code == pjsip_status_code.PJSIP_SC_BUSY_EVERYWHERE){
                            for (CallListener listener : mCallListeners) {
                                listener.onCallBusy(callInfo);
                            }
                        } // ... more error status codes
                        else if (code == pjsip_status_code.PJSIP_SC_NOT_FOUND ||
                            code == pjsip_status_code.PJSIP_SC_TEMPORARILY_UNAVAILABLE ||
                            code == pjsip_status_code.PJSIP_SC_FORBIDDEN ||
                            code == pjsip_status_code.PJSIP_SC_SERVICE_UNAVAILABLE ||
                            code == pjsip_status_code.PJSIP_SC_REQUEST_TIMEOUT ||
                            code == pjsip_status_code.PJSIP_SC_BAD_REQUEST) {
                            for (CallListener listener : mCallListeners) {
                                listener.onError(callInfo, code.swigValue(), pjcallInfo.getLastReason());
                            }
                        } else {
                            // all else fail
                            for (CallListener listener : mCallListeners) {
                                listener.onCallEnded(callInfo);
                            }
                        }
                    }catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    activeCall = null;
                    this.delete();
                }
            }
            catch(Exception e) {
                for (CallListener listener : mCallListeners) {
                    listener.onError(new CallInfo("unknown"), 0, e.getMessage() + "");
                }
                this.delete();
                activeCall = null;
            }

        }

        @Override
        public void onCallMediaState(OnCallMediaStateParam prm) {
            org.pjsip.pjsua2.CallInfo ci;
            try {
                ci = getInfo();
            } catch (Exception e) {
                return;
            }

            CallMediaInfoVector cmiv = ci.getMedia();
            pjsua_call_media_status mediaState;
            long len = cmiv.size();

            for (int i = 0; i < len; i++) {
                CallMediaInfo cmi = cmiv.get(i);
                mediaState = cmi.getStatus();

                if (cmi.getType() == pjmedia_type.PJMEDIA_TYPE_AUDIO &&
                        (mediaState == pjsua_call_media_status.PJSUA_CALL_MEDIA_ACTIVE ||
                                mediaState == pjsua_call_media_status.PJSUA_CALL_MEDIA_REMOTE_HOLD))
                {
                    Media m = getMedia(i);
                    AudioMedia am = AudioMedia.typecastFromMedia(m);

                    try {
                        sEndPoint.audDevManager().getCaptureDevMedia().startTransmit(am);
                        am.startTransmit(sEndPoint.audDevManager().getPlaybackDevMedia());
                    } catch (Exception e) {
                        continue;
                    }
                } else if (cmi.getType() == pjmedia_type.PJMEDIA_TYPE_VIDEO &&
                        cmi.getStatus() ==
                                pjsua_call_media_status.PJSUA_CALL_MEDIA_ACTIVE &&
                        cmi.getVideoIncomingWindowId() != pjsua2.INVALID_ID)
                {
                    // TODO: Implement video in future?
                /*vidWin = new VideoWindow(cmi.getVideoIncomingWindowId());
                vidPrev = new VideoPreview(cmi.getVideoCapDev());*/
                }
            }
        }

        private CallInfo makeCallInfo(org.pjsip.pjsua2.CallInfo callInfo) {
            return new CallInfo(callInfo);
        }

        boolean toggleMute() {
            if (localMute) {
                setMute(false);
                return !localHold;
            }

            setMute(true);
            return localHold;
        }

        void setMute(boolean mute) {

            // return immediately if we are not changing the current state
            if ((localMute && mute) || (!localMute && !mute)) return;

            org.pjsip.pjsua2.CallInfo info;
            try {
                info = getInfo();
            } catch (Exception exc) {
                Log.e(TAG, "setMute: error while getting call info", exc);
                return;
            }

            for (int i = 0; i < info.getMedia().size(); i++) {
                Media media = getMedia(i);
                CallMediaInfo mediaInfo = info.getMedia().get(i);

                if (mediaInfo.getType() == pjmedia_type.PJMEDIA_TYPE_AUDIO
                        && media != null
                        && mediaInfo.getStatus() == pjsua_call_media_status.PJSUA_CALL_MEDIA_ACTIVE) {
                    AudioMedia audioMedia = AudioMedia.typecastFromMedia(media);

                    // connect or disconnect the captured audio
                    try {
                        AudDevManager mgr = sEndPoint.audDevManager();

                        if (mute) {
                            mgr.getCaptureDevMedia().stopTransmit(audioMedia);
                            localMute = true;
                        } else {
                            mgr.getCaptureDevMedia().startTransmit(audioMedia);
                            localMute = false;
                        }

                    } catch (Exception exc) {
                        Log.e(TAG, "setMute: error while connecting audio media to sound device", exc);
                    }
                }
            }
        }


        void setHold(boolean hold) {
            // return immediately if we are not changing the current state
            if ((localHold && hold) || (!localHold && !hold)) return;

            CallOpParam param = new CallOpParam();

            try {
                if (hold) {
                    setHold(param);
                    localHold = true;
                } else {
                    CallSetting opt = param.getOpt();
                    opt.setAudioCount(1);
                    opt.setVideoCount(0);
                    opt.setFlag(pjsua_call_flag.PJSUA_CALL_UNHOLD.swigValue());
                    reinvite(param);
                    localHold = false;
                    // TODO: Notify of call re-established?
                }
            } catch (Exception exc) {
                String operation = hold ? "hold" : "unhold";
                Log.e(TAG, "Error while trying to " + operation + " call", exc);
            }
        }
    }



    static class LogJob extends Job {

        String text;
        public LogJob(String text) {
            super(new Params(0).delayInMs(500));
            this.text = text;
        }

        @Override
        public void onAdded() {

        }

        @Override
        protected void onCancel(int cancelReason, @Nullable Throwable throwable) {

        }

        @Override
        protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
            return null;
        }

        @Override
        public void onRun() throws Throwable {
            Log.d(TAG, text + "");
        }
    }

}
