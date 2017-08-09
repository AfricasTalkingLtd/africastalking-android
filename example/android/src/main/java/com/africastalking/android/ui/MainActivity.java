package com.africastalking.android.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RelativeLayout;

import com.africastalking.android.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.africastalking.android.ui.voice.OutgoingCallActivity;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.account_layout)
    RelativeLayout account;
    @BindView(R.id.airtime_layout)RelativeLayout airtime;
    @BindView(R.id.payment_layout)RelativeLayout payment;
    @BindView(R.id.sms_layout)RelativeLayout sms;
    @BindView(R.id.voice_layout)RelativeLayout voice;

    @OnClick(R.id.account_layout) void account() {
        startActivity(new Intent(this, AccountActivity.class));
    }

    @OnClick(R.id.airtime_layout) void airtime() {
        startActivity(new Intent(this, AirtimeActivity.class));
    }

    @OnClick(R.id.payment_layout) void payment() {
        startActivity(new Intent(this, PaymentActivity.class));
    }

    @OnClick(R.id.sms_layout) void sms() {
        startActivity(new Intent(this, SmsActivity.class));
    }

    @OnClick(R.id.voice_layout) void voice() {
        startActivity(new Intent(this, OutgoingCallActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


    }
}
