package com.africastalking.android.ui.voice;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.africastalking.AfricasTalking;
import com.africastalking.AfricasTalkingException;
import com.africastalking.voice.CallInfo;
import com.africastalking.voice.CallListener;
import com.africastalking.voice.VoiceBackgroundService;
import com.africastalking.voice.VoiceBackgroundService.VoiceServiceBinder;
import com.africastalking.android.R;

public class IncomingCallActivity extends AppCompatActivity {


    @BindView(R.id.title)
    TextView title;

    @BindView(R.id.btnPickUp)
    Button pickUp;

    private VoiceBackgroundService mService;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            VoiceServiceBinder binder = (VoiceServiceBinder) service;
            mService = binder.getService();

            if (mService.isCallInProgress()) {
                title.setText("Call In Progress");
                pickUp.setVisibility(View.GONE);
                mService.setCallListener(new CallListener() {
                    @Override
                    public void onCallEnded(CallInfo callInfo) {
                        finish();
                    }
                });
            } else {
                title.setText("Incoming Call");
                pickUp.setVisibility(View.VISIBLE);
            }
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
            mService.pickCall(new CallListener() {
                @Override
                public void onError(CallInfo call, int errorCode, String errorMessage) {
                    Log.e("Error making call", errorMessage + "(" + errorCode + ")");
                }

                @Override
                public void onRinging(CallInfo call, String caller) {
                    Log.e("Ringing", caller + "");
                }

                @Override
                public void onCallEstablished(CallInfo call) {
                    Log.e("Starting call", "");
                    mService.startAudio();
                    mService.setSpeakerMode(false);
                }
                @Override
                public void onCallEnded(CallInfo call) {
                    Log.e("Call Ended", "");
                    finish();
                }
            });
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
            finish();
        } catch (AfricasTalkingException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        AfricasTalking.unbindVoiceBackgroundService(this, mConnection);
        super.onDestroy();
    }
}
