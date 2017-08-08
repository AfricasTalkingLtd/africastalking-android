package com.africastalking.android.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.africastalking.AfricasTalking;
import com.africastalking.android.R;

import java.io.IOException;

public class AccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        try {
            AfricasTalking.initialize("aksalj", "134.213.52.79");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
