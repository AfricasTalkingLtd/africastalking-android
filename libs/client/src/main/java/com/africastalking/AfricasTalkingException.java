package com.africastalking;

import android.net.sip.SipException;

/**
 * Copyright (c) 2017 Salama AB
 * All rights reserved
 * Contact: aksalj@aksalj.me
 * Website: http://www.aksalj.me
 * <p>
 * Project : dev-mvp
 * File : AfricasTalkingException
 * Date : 8/12/17 11:11 AM
 * Description :
 */
public class AfricasTalkingException extends Exception {

    public AfricasTalkingException(String message) {
        super(message);
    }

    public AfricasTalkingException(SipException sip) {
        super(sip.getCause());
    }

}
