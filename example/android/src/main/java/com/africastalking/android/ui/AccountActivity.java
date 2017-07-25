package com.africastalking.android.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.africastalking.AfricasTalking;
import com.africastalking.android.R;

public class AccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        AfricasTalking.initialize("134.213.52.79", 8082);
    }
}
