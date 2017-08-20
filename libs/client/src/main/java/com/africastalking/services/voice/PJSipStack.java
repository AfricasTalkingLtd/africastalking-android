package com.africastalking.services.voice;

import android.content.Intent;
import android.util.Log;

import com.africastalking.AfricasTalkingException;
import com.africastalking.proto.SdkServerServiceOuterClass.SipCredentials;

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
import org.pjsip.pjsua2.Media;
import org.pjsip.pjsua2.MediaConfig;
import org.pjsip.pjsua2.OnCallMediaStateParam;
import org.pjsip.pjsua2.OnCallStateParam;
import org.pjsip.pjsua2.OnIncomingCallParam;
import org.pjsip.pjsua2.OnNatCheckStunServersCompleteParam;
import org.pjsip.pjsua2.OnNatDetectionCompleteParam;
import org.pjsip.pjsua2.OnRegStartedParam;
import org.pjsip.pjsua2.OnRegStateParam;
import org.pjsip.pjsua2.OnSelectAccountParam;
import org.pjsip.pjsua2.OnTransportStateParam;
import org.pjsip.pjsua2.SWIGTYPE_p_void;
import org.pjsip.pjsua2.SipEvent;
import org.pjsip.pjsua2.SipTxOption;
import org.pjsip.pjsua2.StringVector;
import org.pjsip.pjsua2.TransportConfig;
import org.pjsip.pjsua2.UaConfig;
import org.pjsip.pjsua2.pj_qos_type;
import org.pjsip.pjsua2.pjmedia_type;
import org.pjsip.pjsua2.pjsip_inv_state;
import org.pjsip.pjsua2.pjsip_status_code;
import org.pjsip.pjsua2.pjsip_transport_type_e;
import org.pjsip.pjsua2.pjsua2;
import org.pjsip.pjsua2.pjsua_call_flag;
import org.pjsip.pjsua2.pjsua_call_media_status;
import org.pjsip.pjsua2.pjsua_stun_use;

import static com.africastalking.services.voice.VoiceBackgroundService.INCOMING_CALL;

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
class PJSipStack extends BaseSipStack {

    private static final String TAG = PJSipStack.class.getName();
    private static final String AGENT_NAME = "AfricasTalking";

    private static CallListener mCallListener;

    private static Endpoint sEndPoint = null;
    private Account mAccount = null;

    PJSipStack(final VoiceBackgroundService context, SipCredentials credentials) throws Exception {
        super(credentials);

        Log.d(TAG, "Initializing PJSIP...");

        System.loadLibrary("pjsua2");


        // Register
        sEndPoint = new Endpoint() {

            @Override
            public void onSelectAccount(OnSelectAccountParam prm) {
                super.onSelectAccount(prm);
                Log.wtf(TAG, "onSelectAccount: " + prm.getRdata().getWholeMsg());

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
        UaConfig uaConfig = new UaConfig();
        uaConfig.setUserAgent(AGENT_NAME);

        // Stun server
        StringVector stunServer = new StringVector();
        stunServer.add("stun.l.google.com:19302");
        stunServer.add("stun1.l.google.com:19302");
        stunServer.add("stun2.l.google.com:19302");
        stunServer.add("stun3.l.google.com:19302");
        stunServer.add("stun4.l.google.com:19302");
        uaConfig.setStunServer(stunServer);
        config.setUaConfig(uaConfig);

        MediaConfig medConfig = new MediaConfig();
        medConfig.setHasIoqueue(true);
        medConfig.setClockRate(16000);
        medConfig.setQuality(10);
        medConfig.setEcOptions(1);
        medConfig.setEcTailLen(200);
        medConfig.setThreadCnt(2);
        config.setMedConfig(medConfig);

        sEndPoint.libInit(config);

        TransportConfig transport = new TransportConfig();
        transport.setPort(credentials.getPort());

        TransportConfig transportConfig = new TransportConfig();
        transportConfig.setQosType(pj_qos_type.PJ_QOS_TYPE_VOICE);
        if (credentials.getTransport().contentEquals("tcp")) {
            sEndPoint.transportCreate(pjsip_transport_type_e.PJSIP_TRANSPORT_TCP, transportConfig);
        } else {
            sEndPoint.transportCreate(pjsip_transport_type_e.PJSIP_TRANSPORT_UDP, transportConfig);
        }

        sEndPoint.libStart();

        final AccountConfig accfg = new AccountConfig();

        // TODO: FIX NAT issues
        AccountNatConfig natcfg = new AccountNatConfig();
        natcfg.setIceEnabled(true);
        natcfg.setTurnEnabled(false);
        natcfg.setSipStunUse(pjsua_stun_use.PJSUA_STUN_USE_DEFAULT);
        natcfg.setMediaStunUse(pjsua_stun_use.PJSUA_STUN_USE_DEFAULT);
        accfg.setNatConfig(natcfg);

        accfg.setIdUri("sip:" + credentials.getUsername() + "@" + credentials.getHost());
        accfg.getRegConfig().setRegistrarUri("sip:" + credentials.getHost());

        AuthCredInfo credInfo = new AuthCredInfo("digest", "*", credentials.getUsername(), 0, credentials.getPassword());
        accfg.getSipConfig().getAuthCreds().add(credInfo);

        mAccount = new Account() {
            @Override
            public void onRegStarted(OnRegStartedParam prm) {
                super.onRegStarted(prm);
                Log.d(TAG, "Registration Started");
                setReady(false);
                if (VoiceBackgroundService.mRegistrationListener != null) {
                    VoiceBackgroundService.mRegistrationListener.onStartRegistration();
                }
            }

            @Override
            public void onRegState(OnRegStateParam prm) {
                super.onRegState(prm);

                pjsip_status_code code = prm.getCode();

                boolean registered =  code == pjsip_status_code.PJSIP_SC_OK;
                setReady(registered);

                if (VoiceBackgroundService.mRegistrationListener != null) {
                    if (registered) {
                        Log.d(TAG, "Registration Complete");
                        VoiceBackgroundService.mRegistrationListener.onCompleteRegistration();
                    } else {
                        Log.d(TAG, "Registration Failed");
                        VoiceBackgroundService.mRegistrationListener.onFailedRegistration(new Error(prm.getReason()));
                    }
                }
            }

            @Override
            public void onIncomingCall(OnIncomingCallParam prm) {
                try {
                    SipCall call = new SipCall(mAccount, prm.getCallId());
                    CallOpParam callOpParam = new CallOpParam();
                    callOpParam.setStatusCode(pjsip_status_code.PJSIP_SC_RINGING);
                    call.answer(callOpParam);

                    if (mCallListener != null) {
                        mCallListener.onRinging(new CallInfo(call.getInfo()));
                    }

                    // Notify UI
                    context.sendBroadcast(new Intent(INCOMING_CALL)); // Slow?????

                } catch (Exception ex) {
                    Log.e(TAG, ex.getMessage() + "");
                }

            }
        };
        mAccount.create(accfg);
    }

    public static PJSipStack newInstance(VoiceBackgroundService context, SipCredentials credentials) throws Exception {
        return new PJSipStack(context, credentials);
    }

    @Override
    public void destroy(VoiceBackgroundService context) {
        SipCall.destroy();
    }

    @Override
    public void setCallListener(CallListener listener) {
        mCallListener = listener;
    }

    @Override
    public void makeCall(final String destination, final int timeout, final CallListener listener) {
        try {
            setCallListener(listener);
            SipCall call = new SipCall(mAccount);
            String recipient = ("sip:" + destination + "@" + mCredentials.getHost());
            // isSipUri(destination)
            call.makeCall(recipient, new CallOpParam());
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage() + "");
        }
    }

    @Override
    public void pickCall(int timeout, CallListener listener) throws AfricasTalkingException {

        if (isCallInProgress()) throw new AfricasTalkingException("A call is already in progress");

        setCallListener(listener);
        SipCall call = SipCall.getCurrentCall();

        if (call != null) {
            try {
                CallOpParam param = new CallOpParam();
                param.setStatusCode(pjsip_status_code.PJSIP_SC_OK);
                call.answer(param);
            } catch (Exception e) {
                throw new AfricasTalkingException(e);
            }
        }
    }

    @Override
    public void holdCall(int timeout) throws AfricasTalkingException {
        SipCall call = SipCall.getCurrentCall();
        if (call == null) {
            return;
        }
        call.setHold(true);
    }

    @Override
    public void resumeCall(int timeout) throws AfricasTalkingException {
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
                SipCall.destroy();
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
                txo.setContentType(" application/dtmf-relay");
                txo.setMsgBody("Signal=" + character + "\n" + "Duration=160");
                prm.setTxOption(txo);
                call.sendRequest(prm);
            } catch (Exception e) {
                e.printStackTrace();
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
    public void setSpeakerMode(boolean speaker) { }


    private static class SipCall extends Call {

        private static SipCall activeCall = null;

        boolean localHold = false;
        boolean localMute = false;

        SipCall(Account account) {
            super(account);
            activeCall = this;
        }

        SipCall(Account account, int call_id) {
            super(account, call_id);
            activeCall = this;
        }

        static SipCall getCurrentCall() {
            return activeCall;
        }

        @Override
        public void onCallState(OnCallStateParam prm) {

            try {
                SipEvent evt = prm.getE();
                Log.d(TAG + " -> Event", evt.getType().toString());

                org.pjsip.pjsua2.CallInfo callInfo = getInfo();
                pjsip_inv_state callState = callInfo.getState();
                pjsip_status_code code = null;
                try {
                    code = callInfo.getLastStatusCode();
                } catch (Exception ex) { }

                if(callState == pjsip_inv_state.PJSIP_INV_STATE_CALLING) {

                    Log.d(TAG + " -> Session", "Calling: " + callInfo.getRemoteUri());

                    if (mCallListener != null) {
                        mCallListener.onCalling(makeCallInfo(callInfo));
                    }
                }
                else if (callState == pjsip_inv_state.PJSIP_INV_STATE_CONNECTING) {
                    Log.d(TAG + " -> Session", "Connecting: " + callInfo.getRemoteUri());

                    if (mCallListener != null) {
                        mCallListener.onCalling(makeCallInfo(callInfo));
                    }
                }
                else if (callState == pjsip_inv_state.PJSIP_INV_STATE_EARLY) {
                    Log.d(TAG + " -> Session", "Early: " + code);

                    if (code == pjsip_status_code.PJSIP_SC_RINGING) {
                        if (mCallListener != null) {
                            mCallListener.onRinging(makeCallInfo(callInfo));
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
                        if (mCallListener != null) {
                            mCallListener.onCallEstablished(makeCallInfo(callInfo));
                        }
                    }

                    if (code == pjsip_status_code.PJSIP_SC_RINGING) {
                        if (mCallListener != null) {
                            mCallListener.onRinging(makeCallInfo(callInfo));
                        }
                    }

                    if (code == pjsip_status_code.PJSIP_SC_NOT_FOUND) {
                        mCallListener.onError(makeCallInfo(callInfo), 404, "Not Found");
                    }

                    // ... more statuses

                }
                else if(callState == pjsip_inv_state.PJSIP_INV_STATE_DISCONNECTED) {

                    try {

                        Log.d(TAG + " -> Session", "Disconnected: " + code);

                        if (mCallListener == null) {
                            Log.e(TAG + " -> Session", "Disconnected: " + "No call listener!");
                        }

                        if (code == pjsip_status_code.PJSIP_SC_BUSY_HERE || code == pjsip_status_code.PJSIP_SC_BUSY_EVERYWHERE){
                            if (mCallListener != null) {
                                mCallListener.onCallBusy(makeCallInfo(callInfo));
                            }
                        }
                        // ... more error status codes

                        if (code == pjsip_status_code.PJSIP_SC_NOT_FOUND ||
                            code == pjsip_status_code.PJSIP_SC_TEMPORARILY_UNAVAILABLE ||
                            code == pjsip_status_code.PJSIP_SC_FORBIDDEN ||
                            code == pjsip_status_code.PJSIP_SC_SERVICE_UNAVAILABLE ||
                            code == pjsip_status_code.PJSIP_SC_REQUEST_TIMEOUT ||
                            code == pjsip_status_code.PJSIP_SC_BAD_REQUEST) {
                            if (mCallListener != null) {
                                mCallListener.onError(makeCallInfo(callInfo), code.swigValue(), callInfo.getLastReason());
                            }
                        }

                        if (mCallListener != null) {
                            mCallListener.onCallEnded(makeCallInfo(callInfo));
                        }
                    }catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    activeCall = null;
                    this.delete();

                }
            }
            catch(Exception e) {
                if (mCallListener != null) {
                    mCallListener.onError(new CallInfo("unknown"), 0, e.getMessage() + "");
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
                    if (mCallListener != null) {
                        mCallListener.onCallHeld(makeCallInfo(getInfo()));
                    }
                } else {
                    CallSetting opt = param.getOpt();
                    opt.setAudioCount(1);
                    opt.setVideoCount(0);
                    opt.setFlag(pjsua_call_flag.PJSUA_CALL_UNHOLD.swigValue());
                    reinvite(param);
                    localHold = false;
                    if (mCallListener != null) {
                        mCallListener.onCallEstablished(makeCallInfo(getInfo()));
                    }
                }
            } catch (Exception exc) {
                String operation = hold ? "hold" : "unhold";
                Log.e(TAG, "Error while trying to " + operation + " call", exc);
            }
        }


        static void destroy() {
            if (activeCall != null) {
                activeCall.delete();
                activeCall = null;
            }
        }
    }

}
