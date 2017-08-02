package com.africastalking.android.ui;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.sip.SipAudioCall;
import android.net.sip.SipException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.africastalking.AfricasTalking;
import com.africastalking.VoiceService;
import com.africastalking.android.IncomingCallReceiver;
import com.africastalking.android.R;

import butterknife.BindView;
import butterknife.OnClick;

public class VoiceActivity extends AppCompatActivity {
    VoiceService voiceService;
    IncomingCallReceiver callReceiver;

    @BindView(R.id.call_btn)Button callBtn;
    @BindView(R.id.cancel_btn)Button resetBtn;
    @BindView(R.id.display)EditText display;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice);

        try {
            voiceService = new VoiceService(this, "192.168.1.68", 35897);

            IntentFilter filter = new IntentFilter();
            filter.addAction("android.africastalking.INCOMING_CALL");
            callReceiver = new IncomingCallReceiver();
            this.registerReceiver(callReceiver, filter);
        }
        catch (Exception e){
            Toast.makeText(VoiceActivity.this, "An error occured" + e.getMessage(), Toast.LENGTH_SHORT);
        }

    }

    public void takeAudioCall(Intent intent, SipAudioCall.Listener listener) {
        voiceService.takeAudioCall(intent, listener);
    }

    @OnClick(R.id.call_btn)
    public void makeCall(){
        if(voiceService.isInitialized()){
            SipAudioCall.Listener listener = new SipAudioCall.Listener() {
              @Override
                public void onCallEstablished(SipAudioCall call) {
                  call.startAudio();
                  if(call.isMuted())
                      call.toggleMute();
              }
              @Override
              public void onCallEnded(SipAudioCall call) {
                  try {
                      call.endCall();
                  } catch (SipException e) {
                      Log.d("Error ending call", e.getMessage());
                  }
              }
            };
        }
    }

    @Override
    public void onDestroy() {
        AfricasTalking.destroy();
        super.onDestroy();
    }
}
