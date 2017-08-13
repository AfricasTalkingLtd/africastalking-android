package com.africastalking.services.voice;

import android.net.sip.SipAudioCall;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Copyright (c) 2017 Salama AB
 * All rights reserved
 * Contact: aksalj@aksalj.me
 * Website: http://www.aksalj.me
 * <p>
 * Project : dev-mvp
 * File : CallInfo
 * Date : 8/12/17 11:35 AM
 * Description :
 */
public class CallInfo {

    private final Pattern displayNameAndRemoteUriPattern = Pattern.compile("^\"([^\"]+).*?sip:(.*?)>$");
    private final Pattern remoteUriPattern = Pattern.compile("^.*?sip:(.*?)>?$");

    private static final String UNKNOWN = "Unknown";

    private String displayName;
    private String remoteUri;

    CallInfo(String remoteUriString) {

        if (remoteUriString == null || remoteUriString.isEmpty()) {
            displayName = remoteUri = UNKNOWN;
            return;
        }

        Matcher completeInfo = displayNameAndRemoteUriPattern.matcher(remoteUriString);
        if (completeInfo.matches()) {
            displayName = completeInfo.group(1);
            remoteUri = completeInfo.group(2);

        } else {
            Matcher remoteUriInfo = remoteUriPattern.matcher(remoteUriString);
            if (remoteUriInfo.matches()) {
                displayName = remoteUri = remoteUriInfo.group(1);
            } else {
                displayName = remoteUri = UNKNOWN;
            }
        }
    }

    CallInfo(SipAudioCall call) {
        this(call.getPeerProfile().getUriString());
    }

    CallInfo(final org.pjsip.pjsua2.CallInfo callInfo) {
        this(callInfo.getRemoteUri());
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getRemoteUri() {
        return remoteUri;
    }

}
