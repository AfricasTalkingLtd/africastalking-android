package com.africastalking.android.ui.payment;

import android.os.AsyncTask;
import android.os.Bundle;

import com.africastalking.AfricasTalking;
import com.africastalking.Environment;
import com.africastalking.Logger;
import com.africastalking.android.BuildConfig;
import com.africastalking.android.R;
import com.africastalking.android.ui.BaseActivity;
import com.africastalking.models.CheckoutResponse;
import com.africastalking.services.PaymentService;

import timber.log.Timber;

public class PaymentActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        AsyncTask<Void, String, Void> task = new AsyncTask<Void, String, Void>() {

            @Override
            protected Void doInBackground(Void... params) {

                try {
                    Timber.i("Initializing SDK...");
                    AfricasTalking.initialize(
                            BuildConfig.RPC_USERNAME,
                            BuildConfig.RPC_HOST,
                            BuildConfig.RPC_PORT,
                            Environment.SANDBOX);
                    AfricasTalking.setLogger(new Logger() {
                        @Override
                        public void log(String message, Object... args) {
                            Timber.d(message, args);
                        }
                    });

                    Timber.i("Getting payment service");
                    PaymentService payment = AfricasTalking.getPaymentService();

                    Timber.i("Checking out KES 100 from 0718769882");
                    CheckoutResponse res = payment.checkout("AliceTest", "0718769882", "KES", 100);

                    Timber.i(res.getTransactionId());
                    Timber.i(res.getStatus());
                    Timber.i(res.getDescription());

                } catch (Exception e) {
                    Timber.e(e, "IOException");
                }

                return null;
            }
        };

        task.execute();

    }
}
