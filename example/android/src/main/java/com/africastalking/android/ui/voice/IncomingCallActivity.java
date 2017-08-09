package com.africastalking.android.ui.voice;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.sip.SipAudioCall;
import android.net.sip.SipException;
import android.net.sip.SipProfile;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.africastalking.VoiceBackgroundService;
import com.africastalking.VoiceBackgroundService.VoiceServiceBinder;
import com.africastalking.android.R;

public class IncomingCallActivity extends AppCompatActivity {

    private VoiceBackgroundService mService;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            VoiceServiceBinder binder = (VoiceServiceBinder) service;
            mService = binder.getService();

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mService = null;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_incoming);
        ButterKnife.bind(this);

        bindService(new Intent(this, VoiceBackgroundService.class), mConnection, 0);
    }

    @OnClick(R.id.btnPickUp)
    public void onPickUp() {

        if (mService == null) {
            Log.e("Service Not Bound!", "");
            return;
        }

        try {
            mService.pickCall(new SipAudioCall.Listener() {
                @Override
                public void onError(SipAudioCall call, int errorCode, String errorMessage) {
                    Log.e("Error making call", errorMessage + "(" + errorCode + ")");
                }

                @Override
                public void onRinging(SipAudioCall call, SipProfile caller) {
                    Log.e("Ringing", "");
                }

                @Override
                public void onCallEstablished(SipAudioCall call) {
                    Log.e("Starting call", "");
                    call.startAudio();
                    call.setSpeakerMode(false);
                }
                @Override
                public void onCallEnded(SipAudioCall call) {
                    Log.e("Call Ended", "");
                    try {
                        call.endCall();
                    } catch (SipException e) {
                        Log.e("Error ending call", e.getMessage() + "");
                    }
                }
            });
        } catch (SipException e) {
            e.printStackTrace();
        }

    }

    @OnClick(R.id.btnHangUp)
    public void onHangUp() {
        try {

            if (mService == null) {
                Log.e("Service Not Bound!", "");
                return;
            }

            mService.endCall();
        } catch (SipException e) {
            e.printStackTrace();
        }
    }

}
