package com.africastalking.voice;


/**
 * Copyright (c) 2017 Salama AB
 * All rights reserved
 * Contact: aksalj@aksalj.me
 * Website: http://www.aksalj.me
 * <p>
 * Project : dev-mvp
 * File : CallListener
 * Date : 8/12/17 10:59 AM
 * Description :
 */
public class CallListener {

    public void onReadyToCall(CallInfo callInfo) {
        /* no op */
    }

    public void onCalling(CallInfo callInfo) {
        /* no op */
    }

    public void onRinging(CallInfo callInfo, String caller) {
        /* no op */
    }

    public void onRingingBack(CallInfo callInfo) {
        /* no op */
    }

    public void onCallEstablished(CallInfo callInfo) {
        /* no op */
    }

    public void onCallEnded(CallInfo callInfo) {
        /* no op */
    }

    public void onCallBusy(CallInfo callInfo) {
        /* no op */
    }

    public void onCallHeld(CallInfo callInfo) {
        /* no op */
    }

    public void onError(CallInfo callInfo, int errorCode, String errorMessage) {
        /* no op */
    }
}
