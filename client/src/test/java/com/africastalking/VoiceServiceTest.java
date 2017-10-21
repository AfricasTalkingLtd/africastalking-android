package com.africastalking;

import com.africastalking.services.VoiceService;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * Copyright (c) 2017 Salama AB
 * All rights reserved
 * Contact: aksalj@aksalj.me
 * Website: http://www.aksalj.me
 * <p>
 * Project : dev-mvp
 * File : VoiceServiceTest
 * Date : 8/14/17 8:51 AM
 * Description :
 */
public class VoiceServiceTest {

    VoiceService voice;

    @Before
    public void setup() throws IOException {
        AfricasTalking.initialize( "localhost");
        voice = AfricasTalking.getVoiceService();
    }

    @Test
    public void testMediaUpload() {

    }

    @Test
    public void testQueueStatus() {

    }
}
