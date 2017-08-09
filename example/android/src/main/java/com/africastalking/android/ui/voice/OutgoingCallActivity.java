package com.africastalking.android.ui.voice;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.net.sip.SipAudioCall;
import android.net.sip.SipException;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.africastalking.AfricasTalking;
import com.africastalking.VoiceBackgroundService;
import com.africastalking.VoiceBackgroundService.VoiceServiceBinder;
import com.africastalking.android.R;

public class OutgoingCallActivity extends AppCompatActivity {

    private VoiceBackgroundService mService;

    @BindView(R.id.call_btn)
    ImageButton callBtn;

    @BindView(R.id.cancel_btn)
    ImageButton resetBtn;

    @BindView(R.id.display)
    EditText display;


    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            VoiceServiceBinder binder = (VoiceServiceBinder) service;
            mService = binder.getService();
            mService.setRegistrationListener(new VoiceBackgroundService.VoiceListener() {
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
                            Toast.makeText(OutgoingCallActivity.this, "Ready to make calls!", Toast.LENGTH_SHORT).show();
                            callBtn.setEnabled(true);
                        }
                    });
                }
            });

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mService = null;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice);

        ButterKnife.bind(this);
        callBtn.setEnabled(false);

        try {
            AfricasTalking.initialize("aksalj", "192.168.0.28"); // blocking
            AfricasTalking.bindVoiceBackgroundService(this, mConnection);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @OnClick(R.id.call_btn)
    public void makeCall() {
        try {
            if (mService == null) {
                return;
            }

            mService.makeCall(display.getText().toString(), new SipAudioCall.Listener() {
                @Override
                public void onError(SipAudioCall call, int errorCode, String errorMessage) {
                    Log.e("Error making call", errorMessage + "(" + errorCode + ")");
                }

                @Override
                public void onRingingBack(SipAudioCall call) {
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
            if (mService != null) {
                mService.endCall();
            }
        } catch (SipException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {
        AfricasTalking.unbindVoiceBackgroundService(this, mConnection);
        super.onDestroy();
    }
}
