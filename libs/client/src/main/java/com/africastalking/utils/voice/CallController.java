package com.africastalking.utils.voice;

import android.content.Context;

import com.africastalking.AfricasTalkingException;
import com.africastalking.Logger;

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
public interface CallController {

    void registerCallListener(CallListener listener);
    void unregisterCallListener(CallListener listener);

    void registerLogger(Logger logger);
    void unregisterLogger(Logger logger);

    void makeCall(String destination) throws AfricasTalkingException;
    void pickCall() throws AfricasTalkingException;
    void holdCall() throws AfricasTalkingException;
    void resumeCall() throws AfricasTalkingException;
    void endCall() throws AfricasTalkingException;
    void sendDtmf(char character);

    CallInfo getCallInfo();
    boolean isCallInProgress();

    void startAudio();
    void toggleMute();
    void setSpeakerMode(Context context, boolean speaker);
}
