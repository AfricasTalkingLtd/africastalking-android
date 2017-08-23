package com.africastalking.services.voice;

import android.util.Log;

import com.africastalking.proto.SdkServerServiceOuterClass;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Copyright (c) 2017 Salama AB
 * All rights reserved
 * Contact: aksalj@aksalj.me
 * Website: http://www.aksalj.me
 * <p>
 * Project : dev-mvp
 * File : SipStack
 * Date : 8/12/17 10:33 AM
 * Description :
 */
public abstract class SipStack implements CallController {

    private boolean mSipReady = false;
    protected SdkServerServiceOuterClass.SipCredentials mCredentials;

    SipStack(SdkServerServiceOuterClass.SipCredentials credentials) {
        this.mCredentials = credentials;
    }

    public boolean isReady() {
        return mSipReady;
    }

    void setReady(boolean isReady) {
        mSipReady = isReady;
    }

    public abstract void destroy();

    static boolean isSipUri(String uri) {
        String expression = "/^(sip:)?(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$/";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(uri);
        return matcher.matches();
    }

    static boolean isBehindNAT() {
        String address = determineLocalIp();
        try {
            //       10.x.x.x | 192.168.x.x | 172.16.x.x .. 172.19.x.x
            byte[] d = InetAddress.getByName(address).getAddress();
            if ((d[0] == 10) ||
                    (((0x000000FF & d[0]) == 172) &&
                            ((0x000000F0 & d[1]) == 16)) ||
                    (((0x000000FF & d[0]) == 192) &&
                            ((0x000000FF & d[1]) == 168))) {
                return true;
            }
        } catch (UnknownHostException e) {
            Log.e("isBehindAT()" + address, e.getMessage() + "");
        }
        return false;
    }

    static String determineLocalIp() {
        try {
            DatagramSocket s = new DatagramSocket();
            s.connect(InetAddress.getByName("192.168.1.1"), 80);
            return s.getLocalAddress().getHostAddress();
        } catch (IOException e) {
            Log.e("determineLocalIp()", e.getMessage() + "");
            // dont do anything; there should be a connectivity change going
            return null;
        }
    }
}
