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
import android.widget.EditText;
import android.widget.Toast;

import com.africastalking.AfricasTalking;
import com.africastalking.Environment;
import com.africastalking.android.BuildConfig;
import com.africastalking.android.R;
import com.africastalking.android.ui.ServiceActivity;
import com.africastalking.services.voice.CallInfo;
import com.africastalking.services.voice.CallListener;
import com.africastalking.services.voice.RegistrationListener;
import com.africastalking.services.voice.VoiceBackgroundService;
import com.africastalking.services.voice.VoiceBackgroundService.VoiceServiceBinder;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OutgoingCallActivity extends ServiceActivity {

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
            mService.setRegistrationListener(new RegistrationListener() {
                @Override
                public void onError(Throwable error) {
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
                public void onStarting() {
                    Log.i("onStarting", "Registration starting... " + Thread.currentThread().getName());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(OutgoingCallActivity.this, "Registration starting...", Toast.LENGTH_SHORT).show();
                            callBtn.setEnabled(false);
                        }
                    });
                }

                @Override
                public void onComplete() {
                    Log.i("onComplete", "Registration complete!");
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

            mService.makeCall(display.getText().toString(), new CallListener() {
                @Override
                public void onCallBusy(CallInfo call) {
                    Log.e("Callee Busy", call.getDisplayName());
                }

                @Override
                public void onError(CallInfo call, final int errorCode, final String errorMessage) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(OutgoingCallActivity.this, errorMessage + "(" + errorCode + ")", Toast.LENGTH_LONG).show();
                        }
                    });
                    Log.e("Error making call", errorMessage + "(" + errorCode + ")");
                }

                @Override
                public void onRinging(final CallInfo callInfo) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(OutgoingCallActivity.this, "Ringing " + callInfo.getDisplayName(), Toast.LENGTH_LONG).show();
                        }
                    });
                }

                @Override
                public void onRingingBack(final CallInfo call) {
                    Log.e("Ringing", "Ring back");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(OutgoingCallActivity.this, "Ringing Back " + call.getDisplayName(), Toast.LENGTH_LONG).show();
                        }
                    });
                }

                @Override
                public void onCallEstablished(CallInfo call) {
                    Log.e("Starting call", call.getRemoteUri());

                    mService.startAudio();
                    mService.setSpeakerMode(false);

                    // show in-call ui
                    Intent i = new Intent(OutgoingCallActivity.this, IncomingCallActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                }

                @Override
                public void onCallEnded(CallInfo call) {
                    Log.e("Call Ended", "");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void onAndroidSip(View view) {
        try {
            view.setEnabled(false);
            Toast.makeText(OutgoingCallActivity.this, "Setting up Android SIP", Toast.LENGTH_SHORT).show();
            AfricasTalking.initialize(
                    BuildConfig.RPC_USERNAME,
                    BuildConfig.RPC_HOST,
                    BuildConfig.RPC_PORT,
                    Environment.SANDBOX); // blocking
            AfricasTalking.bindVoiceBackgroundService(this, mConnection, null, "android");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onPJSip(View view) {
        try {
            view.setEnabled(false);
            Toast.makeText(OutgoingCallActivity.this, "Setting up PJSIP", Toast.LENGTH_SHORT).show();
            AfricasTalking.initialize(
                    BuildConfig.RPC_USERNAME,
                    BuildConfig.RPC_HOST,
                    BuildConfig.RPC_PORT,
                    Environment.SANDBOX); // blocking
            AfricasTalking.bindVoiceBackgroundService(this, mConnection, null, "pjsip");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        AfricasTalking.unbindVoiceBackgroundService(this, mConnection);
        super.onDestroy();
    }
}
