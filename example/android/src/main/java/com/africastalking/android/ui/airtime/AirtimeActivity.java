package com.africastalking.android.ui.airtime;

import android.os.AsyncTask;
import android.os.Bundle;

import com.africastalking.AfricasTalking;
import com.africastalking.android.R;
import com.africastalking.android.ui.BaseActivity;
import com.africastalking.models.airtime.AirtimeResponses;
import com.africastalking.services.AirtimeService;

import timber.log.Timber;

public class AirtimeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_airtime);


        AsyncTask<Void, String, Void> task = new AsyncTask<Void, String, Void>() {

            @Override
            protected Void doInBackground(Void... params) {

                try {
                    Timber.i("Getting airtime service");
                    AirtimeService airtime = AfricasTalking.getAirtimeService();

                    Timber.i("Sending KES 100 to 0718769882");
                    AirtimeResponses res = airtime.send("0718769882", "KES", 100);
                    Timber.i("Sent a total of " + res.getTotalAmount());

                } catch (Exception e) {
                    Timber.e(e, "IOException");
                }

                return null;
            }
        };

        task.execute();



    }
}
