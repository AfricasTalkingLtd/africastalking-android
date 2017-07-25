package com.africastalking.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.sip.SipAudioCall;
import android.net.sip.SipException;
import android.net.sip.SipProfile;
import android.util.Log;

import com.africastalking.VoiceService;
import com.africastalking.android.ui.VoiceActivity;

/**
 * Created by jay on 7/25/17.
 */

public class IncomingCallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SipAudioCall incomingCall = null;

        try {
            SipAudioCall.Listener listener = new SipAudioCall.Listener() {
                @Override
                public void onRinging(SipAudioCall call, SipProfile caller) {
                    try {
                        call.answerCall(30);
                    } catch (SipException e) {
                        Log.d("Error answering call", e.getMessage());
                    }
                }
            };
            VoiceActivity voiceActivity = (VoiceActivity)context;
            voiceActivity.takeAudioCall(intent, listener);
        } catch (Exception e) {
            Log.d("", e.getMessage());
        }
    }
}
