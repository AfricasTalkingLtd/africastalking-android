package com.africastalking.android.ui.sms;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;

import com.africastalking.AfricasTalking;
import com.africastalking.android.R;
import com.africastalking.android.ui.BaseActivity;
import com.africastalking.models.sms.Recipient;
import com.africastalking.models.sms.SendMessageResponse;
import com.africastalking.services.SmsService;

import java.util.List;

import timber.log.Timber;

public class SmsActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        @SuppressLint("StaticFieldLeak") AsyncTask<Void, String, Void> task = new AsyncTask<Void, String, Void>() {

            @Override
            protected Void doInBackground(Void... params) {

                try {
                    Timber.i("Getting sms service");
                    SmsService sms = AfricasTalking.getSmsService();

                    Timber.i("Sending hello to 0718769882");
                    List<Recipient> res = sms.send("hello", new String[]{"0718769882"});

                    Timber.i(res.get(0).messageId);
                    Timber.i(res.get(0).status);

                } catch (Exception e) {
                    Timber.e(e, "IOException");
                }

                return null;
            }
        };

        task.execute();

    }
}
