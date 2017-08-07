package com.africastalking.android.ui;

import android.net.sip.SipAudioCall;
import android.net.sip.SipException;
import android.net.sip.SipProfile;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.africastalking.VoiceService;
import com.africastalking.android.R;

public class VoiceIncomingActivity extends AppCompatActivity {

    private VoiceService voice;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_incoming);
        ButterKnife.bind(this);

        voice = VoiceService.getInstance();
    }

    @OnClick(R.id.btnPickUp)
    public void onPickUp() {

        try {
            voice.pickCall(new SipAudioCall.Listener() {
                @Override
                public void onError(SipAudioCall call, int errorCode, String errorMessage) {
                    super.onError(call, errorCode, errorMessage);
                    Log.e("Error making call", errorMessage + "(" + errorCode + ")");
                }

                @Override
                public void onRinging(SipAudioCall call, SipProfile caller) {
                    Log.i("Ringing", "Ring ring");
                    super.onRinging(call, caller);
                }

                @Override
                public void onCallEstablished(SipAudioCall call) {
                    Log.e("Starting call", call.getPeerProfile().getProfileName() + "");
                    call.startAudio();
                    if(call.isMuted()) {
                        call.toggleMute();
                    }
                }
                @Override
                public void onCallEnded(SipAudioCall call) {
                    Log.i("Call Ended", "Ring ring");
                    try {
                        call.endCall();
                    } catch (SipException e) {
                        Log.d("Error ending call", e.getMessage());
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
            voice.endCall();
        } catch (SipException e) {
            e.printStackTrace();
        }
    }

}
