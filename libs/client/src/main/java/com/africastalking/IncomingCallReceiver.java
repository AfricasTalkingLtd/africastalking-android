package com.africastalking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.sip.SipAudioCall;

/**
 * Created by jay on 7/24/17.
 */

public class IncomingCallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SipAudioCall incomingCall = null;
//        VoiceActivity vActivity = (VoiceActivity) context;
//        incomingCall = vActivity.takeAudioCall(intent, listener);
    }
}
