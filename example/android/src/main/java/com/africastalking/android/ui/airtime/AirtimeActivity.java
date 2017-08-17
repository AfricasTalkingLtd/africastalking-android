package com.africastalking.android.ui.airtime;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.util.AsyncListUtil;

import com.africastalking.AfricasTalking;
import com.africastalking.Environment;
import com.africastalking.Logger;
import com.africastalking.android.R;
import com.africastalking.models.AirtimeResponses;
import com.africastalking.services.AirtimeService;
import com.jraska.console.timber.ConsoleTree;

import java.io.IOException;

import timber.log.Timber;

public class AirtimeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_airtime);


        AsyncTask<Void, String, Void> task = new AsyncTask<Void, String, Void>() {

            @Override
            protected Void doInBackground(Void... params) {

                try {
                    Timber.i("Initializing SDK...");
                    AfricasTalking.initialize("sandbox", "192.168.0.2", Environment.SANDBOX);
                    AfricasTalking.setLogger(new Logger() {
                        @Override
                        public void log(String message, Object... args) {
                            Timber.d(message, args);
                        }
                    });

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
