package com.africastalking.voice;

import com.africastalking.AfricasTalkingException;
import com.africastalking.proto.SdkServerServiceOuterClass.*;

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

    PJSipStack(SipCredentials credentials) {
        super(credentials);
    }

    public static PJSipStack newInstance(VoiceBackgroundService context, SipCredentials credentials) {
        return new PJSipStack(credentials);
    }

    @Override
    public void destroy(VoiceBackgroundService context) {

    }

    @Override
    public void setCallListener(CallListener listener) {

    }

    @Override
    public void makeCall(String destination, int timeout, CallListener listener) throws AfricasTalkingException {

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
}
