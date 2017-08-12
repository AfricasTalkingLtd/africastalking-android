package com.africastalking.voice;

/**
 * Copyright (c) 2017 Salama AB
 * All rights reserved
 * Contact: aksalj@aksalj.me
 * Website: http://www.aksalj.me
 * <p>
 * Project : dev-mvp
 * File : SipStack
 * Date : 8/12/17 10:32 AM
 * Description :
 */
interface SipStack extends CallController {
    void destroy(VoiceBackgroundService context);
    boolean isReady();
}
