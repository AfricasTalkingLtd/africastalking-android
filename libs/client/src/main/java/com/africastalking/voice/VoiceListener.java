package com.africastalking.voice;

/**
 * Copyright (c) 2017 Salama AB
 * All rights reserved
 * Contact: aksalj@aksalj.me
 * Website: http://www.aksalj.me
 * <p>
 * Project : dev-mvp
 * File : VoiceListener
 * Date : 8/12/17 10:57 AM
 * Description :
 */
public interface VoiceListener {
    void onFailedRegistration(Throwable error);
    void onStartRegistration();
    void onCompleteRegistration();
}
