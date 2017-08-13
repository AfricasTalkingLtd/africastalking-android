package com.africastalking.services.voice;

import com.africastalking.proto.SdkServerServiceOuterClass;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Copyright (c) 2017 Salama AB
 * All rights reserved
 * Contact: aksalj@aksalj.me
 * Website: http://www.aksalj.me
 * <p>
 * Project : dev-mvp
 * File : BaseSipStack
 * Date : 8/12/17 10:33 AM
 * Description :
 */
abstract class BaseSipStack implements SipStack {

    private boolean mSipReady = false;
    protected SdkServerServiceOuterClass.SipCredentials mCredentials;

    BaseSipStack(SdkServerServiceOuterClass.SipCredentials credentials) {
        this.mCredentials = credentials;
    }

    @Override
    public boolean isReady() {
        return mSipReady;
    }

    protected void setReady(boolean isReady) {
        mSipReady = isReady;
    }

    static boolean isSipUri(String uri) {
        String expression = "/^(sip:)?(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$/";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(uri);
        return matcher.matches();
    }
}
