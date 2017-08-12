package com.africastalking.voice;

import android.content.Intent;
import android.util.Log;

import com.africastalking.AfricasTalkingException;
import com.africastalking.proto.SdkServerServiceOuterClass.SipCredentials;

import org.pjsip.pjsua2.Account;
import org.pjsip.pjsua2.AccountConfig;
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
import org.pjsip.pjsua2.OnCallMediaStateParam;
import org.pjsip.pjsua2.OnCallStateParam;
import org.pjsip.pjsua2.OnIncomingCallParam;
import org.pjsip.pjsua2.OnRegStartedParam;
import org.pjsip.pjsua2.OnRegStateParam;
import org.pjsip.pjsua2.SipTxOption;
import org.pjsip.pjsua2.TransportConfig;
import org.pjsip.pjsua2.pj_qos_type;
import org.pjsip.pjsua2.pjmedia_type;
import org.pjsip.pjsua2.pjsip_inv_state;
import org.pjsip.pjsua2.pjsip_status_code;
import org.pjsip.pjsua2.pjsip_transport_type_e;
import org.pjsip.pjsua2.pjsua2;
import org.pjsip.pjsua2.pjsua_call_flag;
import org.pjsip.pjsua2.pjsua_call_media_status;

import static com.africastalking.voice.VoiceBackgroundService.INCOMING_CALL;

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
        sEndPoint = new Endpoint();

        // Register
        sEndPoint.libCreate();
        EpConfig config = new EpConfig();
        config.getUaConfig().setUserAgent(AGENT_NAME);
        config.getMedConfig().setHasIoqueue(true);
        config.getMedConfig().setClockRate(16000);
        config.getMedConfig().setQuality(10);
        config.getMedConfig().setEcOptions(1);
        config.getMedConfig().setEcTailLen(200);
        config.getMedConfig().setThreadCnt(2);
        sEndPoint.libInit(config);

        TransportConfig transport = new TransportConfig();
        transport.setPort(credentials.getPort());

        TransportConfig udpTransport = new TransportConfig();
        udpTransport.setQosType(pj_qos_type.PJ_QOS_TYPE_VOICE);
        TransportConfig tcpTransport = new TransportConfig();
        tcpTransport.setQosType(pj_qos_type.PJ_QOS_TYPE_VOICE);

        sEndPoint.transportCreate(pjsip_transport_type_e.PJSIP_TRANSPORT_UDP, udpTransport);
        sEndPoint.transportCreate(pjsip_transport_type_e.PJSIP_TRANSPORT_TCP, tcpTransport);

        sEndPoint.libStart();

        AccountConfig accfg = new AccountConfig();
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
            String recipient = "sip:" + destination + "@" + mCredentials.getHost();
            Log.d(TAG, "Calling " + recipient);
            call.makeCall(recipient, new CallOpParam());
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage() + "");
        }
    }

    @Override
    public void pickCall(int timeout, CallListener listener) throws AfricasTalkingException {

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
            return call != null && call.getInfo().getLastStatusCode() == pjsip_status_code.PJSIP_SC_OK;
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
                org.pjsip.pjsua2.CallInfo callInfo = getInfo();
                pjsip_inv_state callState = callInfo.getState();

                if(callState == pjsip_inv_state.PJSIP_INV_STATE_CALLING || callState == pjsip_inv_state.PJSIP_INV_STATE_CONNECTING) {

                    Log.d(TAG, "Calling/Connecting: " + callInfo.getLastStatusCode().toString());

                    if (mCallListener != null) {
                        mCallListener.onReadyToCall(makeCallInfo(callInfo));
                    }
                }
                else if(callState == pjsip_inv_state.PJSIP_INV_STATE_CONFIRMED) {
                    // TODO: RingBack, Hold?

                    pjsip_status_code code = callInfo.getLastStatusCode();


                    Log.d(TAG, "Confirmed: " + code.toString());

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
                        pjsip_status_code code = callInfo.getLastStatusCode();

                        Log.d(TAG, "Disconnected: " + code.toString());

                        if (code == pjsip_status_code.PJSIP_SC_BUSY_HERE || code == pjsip_status_code.PJSIP_SC_BUSY_EVERYWHERE){
                            if (mCallListener != null) {
                                mCallListener.onCallBusy(makeCallInfo(callInfo));
                            }
                        }
                        // ... more error status codes

                        if (code == pjsip_status_code.PJSIP_SC_NOT_FOUND ||
                                code == pjsip_status_code.PJSIP_SC_TEMPORARILY_UNAVAILABLE ||
                                code == pjsip_status_code.PJSIP_SC_FORBIDDEN) {
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
                    mCallListener.onError(makeCallInfo(null), 0, e.getMessage() + "");
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
                    Log.d(TAG, "holding call with ID " + getId());
                    setHold(param);
                    localHold = true;
                } else {
                    // http://lists.pjsip.org/pipermail/pjsip_lists.pjsip.org/2015-March/018246.html
                    Log.d(TAG, "un-holding call with ID " + getId());
                    CallSetting opt = param.getOpt();
                    opt.setAudioCount(1);
                    opt.setVideoCount(0);
                    opt.setFlag(pjsua_call_flag.PJSUA_CALL_UNHOLD.swigValue());
                    reinvite(param);
                    localHold = false;
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
