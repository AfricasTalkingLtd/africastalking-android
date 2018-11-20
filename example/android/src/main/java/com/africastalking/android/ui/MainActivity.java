package com.africastalking.android.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RelativeLayout;

import com.africastalking.AfricasTalking;
import com.africastalking.android.BuildConfig;
import com.africastalking.android.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

import com.africastalking.android.ui.airtime.AirtimeActivity;
import com.africastalking.android.ui.payment.PaymentActivity;
import com.africastalking.android.ui.sms.SmsActivity;
import com.africastalking.android.ui.voice.VoiceActivity;
import com.africastalking.utils.Logger;
import com.jraska.console.Console;
import com.jraska.console.timber.ConsoleTree;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    static {
        AfricasTalking.setClientId("zFTF4GTJS6n3bryppQRXP7zg"); // or some cookie or some token received after login?
        Timber.plant(new ConsoleTree.Builder().build());
    }


    @BindView(R.id.airtime_layout)
    RelativeLayout airtime;

    @BindView(R.id.payment_layout)
    RelativeLayout payment;

    @BindView(R.id.sms_layout)
    RelativeLayout sms;

    @BindView(R.id.voice_layout)
    RelativeLayout voice;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //Initialize the SDK
        Timber.i("Initializing SDK...");
        AfricasTalking.initialize(
                BuildConfig.RPC_HOST,
                BuildConfig.RPC_PORT, true);
        AfricasTalking.setLogger(new Logger() {
            @Override
            public void log(String message, Object... args) {
                Timber.d(message, args);
            }
        });
    }

    @OnClick(R.id.airtime_layout)
    void airtime() {
        startActivity(new Intent(this, AirtimeActivity.class));
    }

    @OnClick(R.id.payment_layout)
    void payment() {
        startActivity(new Intent(this, PaymentActivity.class));
    }

    @OnClick(R.id.sms_layout)
    void sms() {
        startActivity(new Intent(this, SmsActivity.class));
    }

    @OnClick(R.id.voice_layout)
    void voice() {
        startActivity(new Intent(this, VoiceActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Console.clear();
    }
}
