package com.africastalking.voice;

import android.util.Log;

import com.africastalking.AfricasTalkingException;
import com.africastalking.proto.SdkServerServiceOuterClass.SipCredentials;

import org.pjsip.pjsua2.Account;
import org.pjsip.pjsua2.AccountConfig;
import org.pjsip.pjsua2.AudioMedia;
import org.pjsip.pjsua2.AuthCredInfo;
import org.pjsip.pjsua2.Call;
import org.pjsip.pjsua2.CallMediaInfo;
import org.pjsip.pjsua2.CallMediaInfoVector;
import org.pjsip.pjsua2.CallOpParam;
import org.pjsip.pjsua2.CallSendRequestParam;
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
import org.pjsip.pjsua2.pjmedia_type;
import org.pjsip.pjsua2.pjsip_inv_state;
import org.pjsip.pjsua2.pjsip_status_code;
import org.pjsip.pjsua2.pjsip_transport_type_e;
import org.pjsip.pjsua2.pjsua2;
import org.pjsip.pjsua2.pjsua_call_media_status;

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

    private static final String TAG = AndroidSipStack.class.getName();


    private static CallListener mCallListener;

    private static Endpoint sEndPoint = null;
    private Account mAccount = null;

    PJSipStack(VoiceBackgroundService context, SipCredentials credentials) throws Exception {
        super(credentials);


        Log.d(TAG, "Initializing PJSIP...");

        System.loadLibrary("pjsua2");
        sEndPoint = new Endpoint();

        // Register
        sEndPoint.libCreate();
        EpConfig config = new EpConfig();
        sEndPoint.libInit(config);

        TransportConfig transport = new TransportConfig();
        transport.setPort(credentials.getPort());

        if (credentials.getTransport().contentEquals("udp")) {
            sEndPoint.transportCreate(pjsip_transport_type_e.PJSIP_TRANSPORT_UDP, transport);
        } else {
            sEndPoint.transportCreate(pjsip_transport_type_e.PJSIP_TRANSPORT_TCP, transport);
        }

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
                Log.d(TAG, "Registration In Progress...");
                setReady(false);
                if (VoiceBackgroundService.mRegistrationListener != null) {
                    VoiceBackgroundService.mRegistrationListener.onStartRegistration();
                }
            }

            @Override
            public void onRegState(OnRegStateParam prm) {
                super.onRegState(prm);

                boolean registered = prm.getCode() == pjsip_status_code.PJSIP_SC_OK;
                setReady(registered);

                if (VoiceBackgroundService.mRegistrationListener != null) {
                    if (registered) {
                        VoiceBackgroundService.mRegistrationListener.onCompleteRegistration();
                    } else {
                        VoiceBackgroundService.mRegistrationListener.onFailedRegistration(new Error(prm.getReason()));
                    }
                }
            }

            @Override
            public void onIncomingCall(OnIncomingCallParam prm) {
                super.onIncomingCall(prm);
                // TODO: prep call and notify listener, then wait to answer
            }
        };
        mAccount.create(accfg);

    }

    public static PJSipStack newInstance(VoiceBackgroundService context, SipCredentials credentials) throws Exception {
        return new PJSipStack(context, credentials);
    }

    @Override
    public void destroy(VoiceBackgroundService context) {

    }

    @Override
    public void setCallListener(CallListener listener) {

    }

    @Override
    public void makeCall(String destination, int timeout, CallListener listener) throws AfricasTalkingException {
        try {
            SipCall call = new SipCall(mAccount, -1);
            CallOpParam prm = new CallOpParam();
            String recipient = "sip:" + destination + "@" + mCredentials.getHost();
            Log.d(TAG, "Calling " + recipient);
             call.makeCall(recipient, prm);
        } catch(Exception e) {
            throw new AfricasTalkingException(e.getCause());
        }
    }

    @Override
    public void pickCall(int timeout, CallListener listener) throws AfricasTalkingException {

    }

    @Override
    public void holdCall(int timeout) throws AfricasTalkingException {

    }

    @Override
    public void resumeCall(int timeout) throws AfricasTalkingException {

    }

    @Override
    public void endCall() throws AfricasTalkingException {

    }

    @Override
    public void sendDtmf(char character) {
        SipCall call = SipCall.getCurrentCall();
        if (call != null && call.isActive()) {
            try {
                SipCall.sendDTMF(character);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean isCallInProgress() {
        return false;
    }

    @Override
    public void startAudio() {

    }

    @Override
    public void toggleMute() {

    }

    @Override
    public void setSpeakerMode(boolean speaker) {

    }


    static class SipCall extends Call {

        private static SipCall sCurrentCall = null;

        public SipCall(Account account, int call_id) {
            super(account, call_id);
        }

        public static SipCall getCurrentCall() {
            return sCurrentCall;
        }

        @Override
        public void onCallState(OnCallStateParam prm) {

            try {
                org.pjsip.pjsua2.CallInfo callInfo = getInfo();
                pjsip_inv_state callState = callInfo.getState();
                sCurrentCall = this;

                final String callee = callInfo.getRemoteContact().replaceAll("@.+","").replaceAll("<sip:","");

                if(callState == pjsip_inv_state.PJSIP_INV_STATE_CALLING || callState == pjsip_inv_state.PJSIP_INV_STATE_CONNECTING) {
                    mCallListener.onReadyToCall(makeCallInfo(callInfo));
                }
                else if(callState == pjsip_inv_state.PJSIP_INV_STATE_CONFIRMED) {
                    mCallListener.onCallEstablished(makeCallInfo(callInfo));
                }
                else if(callState == pjsip_inv_state.PJSIP_INV_STATE_DISCONNECTED) {

                    try {
                        pjsip_status_code code = callInfo.getLastStatusCode();
                        if (code == pjsip_status_code.PJSIP_SC_TEMPORARILY_UNAVAILABLE) {
                            mCallListener.onError(makeCallInfo(callInfo), code.swigValue(), callInfo.getLastReason());
                        } else if (code == pjsip_status_code.PJSIP_SC_BUSY_HERE || code == pjsip_status_code.PJSIP_SC_BUSY_EVERYWHERE){
                            mCallListener.onCallBusy(makeCallInfo(callInfo));
                            // ... more status codes
                        } else {
                            mCallListener.onCallEnded(makeCallInfo(callInfo));
                        }
                    }catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    sCurrentCall = null;
                    this.delete();

                }
            }
            catch(Exception e) {
                mCallListener.onError(makeCallInfo(null), 0, e.getMessage() + "");
                this.delete();
                sCurrentCall = null;
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
            CallInfo info = new CallInfo();
            return info;
        }

        static boolean hangUp()  {
            if(sCurrentCall != null) {
                try {
                    CallOpParam prm = new CallOpParam(true);
                    sCurrentCall.hangup(prm);
                }
                catch(Exception e ) {
                    sCurrentCall.delete();
                    sCurrentCall = null;
                }
                return true;
            }
            return false;
        }

        static void sendDTMF(char character) throws Exception {
            if(sCurrentCall != null) {
                CallSendRequestParam prm = new CallSendRequestParam();
                prm.setMethod("INFO");
                SipTxOption txo = new SipTxOption();
                txo.setContentType(" application/dtmf-relay");
                txo.setMsgBody("Signal=" + character + "\n" + "Duration=160");
                prm.setTxOption(txo);
                sCurrentCall.sendRequest(prm);
            }
        }

        static void destroy() {
            if (sCurrentCall != null) {
                sCurrentCall.delete();
                sCurrentCall = null;
            }
        }

    }

}
