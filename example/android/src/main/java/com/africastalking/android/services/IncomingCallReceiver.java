package com.africastalking.android.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.africastalking.android.ui.VoiceIncomingActivity;


public class IncomingCallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startActivity(new Intent(context, VoiceIncomingActivity.class));
    }
}
