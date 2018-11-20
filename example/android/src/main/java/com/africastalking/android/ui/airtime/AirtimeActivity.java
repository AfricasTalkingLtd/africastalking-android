package com.africastalking.android.ui.airtime;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;

import com.africastalking.AfricasTalking;
import com.africastalking.android.R;
import com.africastalking.android.ui.BaseActivity;
import com.africastalking.models.account.ApplicationResponse;
import com.africastalking.models.airtime.AirtimeResponse;
import com.africastalking.services.AirtimeService;

import timber.log.Timber;

public class AirtimeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_airtime);


        @SuppressLint("StaticFieldLeak") AsyncTask<Void, String, Void> task = new AsyncTask<Void, String, Void>() {

            @Override
            protected Void doInBackground(Void... params) {

                try {
                    Timber.i("Getting airtime service");
                    AirtimeService airtime = AfricasTalking.getAirtimeService();

                    Timber.i("Sending KES 100 to 0718769882");
                    AirtimeResponse res = airtime.send("0718769882", "KES", 100 + ((int) Math.random() * 500));
                    Timber.i("Sent a total of " + res.totalAmount);

                } catch (Exception e) {
                    Timber.e(e, "IOException");
                }

                return null;
            }
        };

        task.execute();



    }
}
