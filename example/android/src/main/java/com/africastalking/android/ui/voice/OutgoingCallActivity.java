package com.africastalking.android.ui.voice;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.sip.SipAudioCall;
import android.net.sip.SipException;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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


    @BindView(R.id.dialButton)
    Button callBtn;

    @BindView(R.id.phone_number)
    EditText display;


    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            VoiceServiceBinder binder = (VoiceServiceBinder) service;
            mService = binder.getService();
            mService.setRegistrationListener(new VoiceBackgroundService.VoiceListener() {
                @Override
                public void onFailedRegistration(Throwable error) {
                    Log.e("onError", error.getMessage() + "");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(OutgoingCallActivity.this, "Registration error!", Toast.LENGTH_SHORT).show();
                            callBtn.setEnabled(false);
                        }
                    });
                }

                @Override
                public void onStartRegistration() {
                    Log.i("onRegistration", "Registration starting...");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(OutgoingCallActivity.this, "Registration starting...", Toast.LENGTH_SHORT).show();
                            callBtn.setEnabled(false);
                        }
                    });
                }

                @Override
                public void onCompleteRegistration() {
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
        setContentView(R.layout.activity_voice_dialpad);

        ButterKnife.bind(this);
        callBtn.setEnabled(false);

        try {
            AfricasTalking.initialize("aksalj", "192.168.0.2"); // blocking
            AfricasTalking.bindVoiceBackgroundService(this, mConnection);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void onDigit(View sender) {
        display.setText(display.getText().toString() + ((Button)sender).getText());
    }

    @OnClick(R.id.dialButton)
    public void makeCall() {
        try {
            if (mService == null) {
                return;
            }

            mService.makeCall(display.getText().toString(), new SipAudioCall.Listener() {
                @Override
                public void onCallBusy(SipAudioCall call) {
                    Log.e("Callee Busy", "");
                }

                @Override
                public void onError(SipAudioCall call, final int errorCode, final String errorMessage) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(OutgoingCallActivity.this, errorMessage + "(" + errorCode + ")", Toast.LENGTH_LONG).show();
                        }
                    });
                    Log.e("Error making call", errorMessage + "(" + errorCode + ")");
                }

                @Override
                public void onRingingBack(SipAudioCall call) {
                    Log.e("Ringing", "Ring back");
                }

                @Override
                public void onCallEstablished(SipAudioCall call) {
                    Log.e("Starting call", "");

                    call.startAudio();
                    call.setSpeakerMode(false);

                    // show in-call ui
                    Intent i = new Intent(OutgoingCallActivity.this, IncomingCallActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
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


    @Override
    protected void onDestroy() {
        AfricasTalking.unbindVoiceBackgroundService(this, mConnection);
        super.onDestroy();
    }
}
