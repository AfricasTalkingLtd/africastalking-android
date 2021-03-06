package com.africastalking.utils.voice;

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
public interface RegistrationListener {
    void onError(Throwable error);
    void onStarting();
    void onComplete();
}
