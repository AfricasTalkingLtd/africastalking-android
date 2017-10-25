package com.africastalking.android.ui.voice;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.africastalking.AfricasTalking;
import com.africastalking.AfricasTalkingException;
import com.africastalking.android.R;
import com.africastalking.ui.VoiceService;
import com.africastalking.utils.voice.CallInfo;
import com.africastalking.utils.voice.CallListener;

import java.io.IOException;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InCallActivity extends AppCompatActivity {


    @BindView(R.id.title)
    TextView title;

    @BindView(R.id.btnPickUp)
    Button pickUp;

    @BindView(R.id.btnHold)
    Button hold;

    private boolean held = false;
    private boolean speaker = false;

    private VoiceService mService;

    private Random random = new Random();


    CallListener mCallListener = new CallListener() {
        @Override
        public void onError(CallInfo call, int errorCode, String errorMessage) {
            Log.e("Error making call", errorMessage + "(" + errorCode + ")");
        }

        @Override
        public void onRinging(final CallInfo call) {
            Log.e("Ringing", call.getDisplayName());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    title.setText("Ringing " + call.getDisplayName() + "...");
                }
            });
        }

        @Override
        public void onCallEstablished(final CallInfo call) {
            Log.e("Starting call", "");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    title.setText(call.getDisplayName());

                    hold.setVisibility(View.VISIBLE);
                    pickUp.setVisibility(View.GONE);
                }
            });
            mService.startAudio();
            mService.setSpeakerMode(InCallActivity.this, false);
        }
        @Override
        public void onCallEnded(CallInfo call) {
            Log.e("Call Ended", "");
            finish();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_incoming);
        ButterKnife.bind(this);

        try {
            mService = AfricasTalking.getVoiceService();
            CallInfo info = mService.getCallInfo();
            if (mService.isCallInProgress()) {
                title.setText(info.getDisplayName());
                pickUp.setVisibility(View.GONE);
                hold.setVisibility(View.VISIBLE);
            } else {
                title.setText(info.getDisplayName() + " Calling...");
                pickUp.setVisibility(View.VISIBLE);
            }

            mService.registerCallListener(mCallListener);
        } catch (IOException e) {
            e.printStackTrace();
            finish();
        }

    }

    @OnClick(R.id.btnPickUp)
    public void onPickUp() {

        if (mService == null) {
            Log.e("onPickup", "Service Not Initialized!");
            return;
        }

        try {
            mService.pickCall();
        } catch (AfricasTalkingException e) {
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
        } catch (AfricasTalkingException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.btnHold)
    public void onHold() {
        try {

            if (mService == null) {
                Log.e("Service Not Bound!", "");
                return;
            }
            if (held) {
                mService.resumeCall();
                held = false;
            } else {
                mService.holdCall();
                held = true;
            }
        } catch (AfricasTalkingException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.btnDtmf)
    public void onDtmf() {
        final String digits = "0123456789";
        mService.sendDtmf(digits.charAt(random.nextInt(digits.length())));
    }

    @OnClick(R.id.btnSpeaker)
    public void onSpeaker() {
        mService.setSpeakerMode(this, !speaker);
    }

    @OnClick(R.id.btnMute)
    public void onToggleMute() {
        mService.toggleMute();
    }

    @Override
    protected void onDestroy() {
        if (mService != null) {
            mService.unregisterCallListener(mCallListener);
        }
        super.onDestroy();
    }
}
