package com.africastalking.android.ui.voice;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.africastalking.AfricasTalking;
import com.africastalking.Callback;
import com.africastalking.Environment;
import com.africastalking.Logger;
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
import timber.log.Timber;

public class VoiceActivity extends ServiceActivity {

    private VoiceService mService;

    @BindView(R.id.dialButton)
    Button callBtn;

    @BindView(R.id.phone_number)
    EditText display;

    private Logger mLogger = new Logger() {
        @Override
        public void log(String message, Object... args) {
            Timber.d(message, args);
        }
    };

    private RegistrationListener mRegListener = new RegistrationListener() {
        @Override
        public void onError(Throwable error) {
            Timber.e("Registration Error: " + error.getMessage());
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
            Timber.i("(re)Starting Registration...");
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
            Timber.i("(re)Starting Complete!");

            mService.registerCallListener(mCallListener);
            mService.registerLogger(mLogger);

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
            Timber.w("Callee Busy: " + call.getDisplayName());
        }

        @Override
        public void onError(CallInfo call, final int errorCode, final String errorMessage) {

            Timber.e("Call Error(" + errorCode + "): " + errorMessage);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(VoiceActivity.this, errorMessage + "(" + errorCode + ")", Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onRinging(final CallInfo callInfo) {
            Timber.i("Ringing: " + callInfo.getDisplayName());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(VoiceActivity.this, "Ringing " + callInfo.getDisplayName(), Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onRingingBack(final CallInfo call) {
            Timber.i("Ring Back: " + call.getDisplayName());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(VoiceActivity.this, "Ringing Back " + call.getDisplayName(), Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onCallEstablished(CallInfo call) {
            Timber.i("Starting call: " + call.getDisplayName());

            mService.startAudio();
            mService.setSpeakerMode(VoiceActivity.this, false);

            // show in-call ui
            Intent i = new Intent(VoiceActivity.this, InCallActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }

        @Override
        public void onCallEnded(CallInfo call) {
            Timber.w("Call Ended: " + call.getDisplayName());
        }

        @Override
        public void onIncomingCall(CallInfo callInfo) {
            Timber.i("onIncomingCall: " + callInfo.getDisplayName());
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

        try {
            Timber.i("Setting up pjsip....");
            AfricasTalking.initialize(
                    BuildConfig.RPC_USERNAME,
                    BuildConfig.RPC_HOST,
                    BuildConfig.RPC_PORT,
                    Environment.SANDBOX); // blocking
            AfricasTalking.initializeVoiceService(this, mRegListener, new Callback<VoiceService>() {
                @Override
                public void onSuccess(VoiceService service) {
                    mService = service;
                }

                @Override
                public void onFailure(Throwable throwable) {
                    Timber.e(throwable.getMessage());
                }
            });
        } catch (Exception ex) {
            Timber.e(ex.getMessage());
        }

    }

    @OnClick(R.id.dialButton)
    public void makeCall() {
        try {
            if (mService == null) {
                return;
            }
            String number = display.getText().toString();
            Timber.i("Dialing " + number);
            mService.makeCall(number);
        } catch (Exception e) {
            Timber.e(e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        if (mService != null) {
            mService.unregisterCallListener(mCallListener);
            mService.unregisterLogger(mLogger);
            mService.destroyService();
        }
        super.onDestroy();
    }
}
