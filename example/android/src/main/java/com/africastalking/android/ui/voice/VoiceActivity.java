package com.africastalking.android.ui.voice;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.africastalking.AfricasTalking;
import com.africastalking.Callback;
import com.africastalking.Environment;
import com.africastalking.android.BuildConfig;
import com.africastalking.android.R;
import com.africastalking.android.ui.ServiceActivity;
import com.africastalking.services.VoiceService;
import com.africastalking.services.voice.CallInfo;
import com.africastalking.services.voice.CallListener;
import com.africastalking.services.voice.RegistrationListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VoiceActivity extends ServiceActivity {

    private VoiceService mService;


    @BindView(R.id.dialButton)
    Button callBtn;

    @BindView(R.id.phone_number)
    EditText display;

    private RegistrationListener mRegListener = new RegistrationListener() {
        @Override
        public void onError(Throwable error) {
            Log.e("onError", error.getMessage() + "");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(VoiceActivity.this, "Registration error!", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(VoiceActivity.this, "Registration starting...", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(VoiceActivity.this, "Ready to make calls!", Toast.LENGTH_SHORT).show();
                    callBtn.setEnabled(true);
                }
            });
        }
    };

    private CallListener mCallListener = new CallListener() {
        @Override
        public void onCallBusy(CallInfo call) {
            Log.e("Callee Busy", call.getDisplayName());
        }

        @Override
        public void onError(CallInfo call, final int errorCode, final String errorMessage) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(VoiceActivity.this, errorMessage + "(" + errorCode + ")", Toast.LENGTH_LONG).show();
                }
            });
            Log.e("Error making call", errorMessage + "(" + errorCode + ")");
        }

        @Override
        public void onRinging(final CallInfo callInfo) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(VoiceActivity.this, "Ringing " + callInfo.getDisplayName(), Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onRingingBack(final CallInfo call) {
            Log.e("Ringing", "Ring back");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(VoiceActivity.this, "Ringing Back " + call.getDisplayName(), Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onCallEstablished(CallInfo call) {
            Log.e("Starting call", call.getRemoteUri());

            mService.startAudio();
            mService.setSpeakerMode(VoiceActivity.this, false);

            // show in-call ui
            Intent i = new Intent(VoiceActivity.this, InCallActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }

        @Override
        public void onCallEnded(CallInfo call) {
            Log.e("Call Ended", "");
        }

        @Override
        public void onIncomingCall(CallInfo callInfo) {
            Log.i("onIncomingCall", callInfo.getDisplayName() + " is calling...");
            Intent i = new Intent(VoiceActivity.this, InCallActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
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

            mService.makeCall(display.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void onPJSip(View view) {
        try {
            view.setEnabled(false);
            Toast.makeText(VoiceActivity.this, "Setting up PJSIP", Toast.LENGTH_SHORT).show();
            AfricasTalking.initialize(
                    BuildConfig.RPC_USERNAME,
                    BuildConfig.RPC_HOST,
                    BuildConfig.RPC_PORT,
                    Environment.SANDBOX); // blocking
            AfricasTalking.initializeVoiceService(this, mRegListener, new Callback<VoiceService>() {
                @Override
                public void onSuccess(VoiceService service) {
                    mService = service;
                    mService.registerCallListener(mCallListener);
                }

                @Override
                public void onFailure(Throwable throwable) {
                    Toast.makeText(VoiceActivity.this, throwable.getMessage() + "", Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        if (mService != null) {
            mService.unregisterCallListener(mCallListener);
            mService.destroyService();
        }
        super.onDestroy();
    }
}
