package com.africastalking.android.ui;

import android.net.sip.SipAudioCall;
import android.net.sip.SipException;
import android.net.sip.SipProfile;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.africastalking.AfricasTalking;
import com.africastalking.VoiceService;
import com.africastalking.android.R;

public class VoiceIncomingActivity extends AppCompatActivity {

    private VoiceService voice;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_incoming);
        ButterKnife.bind(this);

        voice = AfricasTalking.getVoiceService();
    }

    @OnClick(R.id.btnPickUp)
    public void onPickUp() {

        try {
            voice.pickCall(new SipAudioCall.Listener() {
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
            voice.endCall();
        } catch (SipException e) {
            e.printStackTrace();
        }
    }

}
