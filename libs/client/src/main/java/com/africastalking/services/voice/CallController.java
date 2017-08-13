package com.africastalking.services.voice;

import com.africastalking.AfricasTalkingException;

/**
 * Copyright (c) 2017 Salama AB
 * All rights reserved
 * Contact: aksalj@aksalj.me
 * Website: http://www.aksalj.me
 * <p>
 * Project : dev-mvp
 * File : CallController
 * Date : 8/12/17 11:07 AM
 * Description :
 */
interface CallController {

    void setCallListener(CallListener listener);

    void makeCall(String destination, int timeout, CallListener listener) throws AfricasTalkingException;
    void pickCall(int timeout, CallListener listener) throws AfricasTalkingException;
    void holdCall(int timeout) throws AfricasTalkingException;
    void resumeCall(int timeout) throws AfricasTalkingException;
    void endCall() throws AfricasTalkingException;
    void sendDtmf(char character);

    CallInfo getCallInfo();
    boolean isCallInProgress();

    void startAudio();
    void toggleMute();
    void setSpeakerMode(boolean speaker);
}
