package com.africastalking.android.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.sip.SipAudioCall;
import android.net.sip.SipException;
import android.net.sip.SipProfile;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import android.widget.Toast;
import butterknife.ButterKnife;
import com.africastalking.AfricasTalking;
import com.africastalking.VoiceService;
import com.africastalking.android.R;

import butterknife.BindView;
import butterknife.OnClick;

public class VoiceActivity extends AppCompatActivity implements VoiceService.VoiceListener {

    private VoiceService mVoiceService;

    @BindView(R.id.call_btn)
    Button callBtn;

    @BindView(R.id.cancel_btn)
    Button resetBtn;

    @BindView(R.id.display)
    EditText display;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice);

        ButterKnife.bind(this);


        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    AfricasTalking.initialize("aksalj", "192.168.0.28");
                    mVoiceService = AfricasTalking.getVoiceService(VoiceActivity.this, VoiceActivity.this);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        task.execute();
    }

    @OnClick(R.id.call_btn)
    public void makeCall() {
        try {
            mVoiceService.makeCall(display.getText().toString(), new SipAudioCall.Listener() {
                @Override
                public void onError(SipAudioCall call, int errorCode, String errorMessage) {
                    Log.e("Error making call", errorMessage + "(" + errorCode + ")");
                }

                @Override
                public void onRinging(SipAudioCall call, SipProfile caller) {
                    Log.e("Ringing", "Ring ring");
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.cancel_btn)
    public void endCall() {
        display.setText(null);
        try {
            mVoiceService.endCall();
        } catch (SipException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(Throwable error) {
        Log.e("onError", error.getMessage() + "");
    }

    @Override
    public void onRegistration() {
        Log.i("onRegistration", "Registration complete!");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(VoiceActivity.this, "Ready to make calls!", Toast.LENGTH_SHORT).show();
                callBtn.setEnabled(true);
            }
        });

    }
}
