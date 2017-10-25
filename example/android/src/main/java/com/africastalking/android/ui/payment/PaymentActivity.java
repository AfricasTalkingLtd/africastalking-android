package com.africastalking.android.ui.payment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.africastalking.AfricasTalking;
import com.africastalking.models.payment.checkout.CardCheckoutRequest;
import com.africastalking.models.payment.checkout.CheckoutRequest;
import com.africastalking.models.payment.checkout.MobileCheckoutRequest;
import com.africastalking.services.CardCheckout;
import com.africastalking.utils.Callback;
import com.africastalking.utils.Logger;
import com.africastalking.android.BuildConfig;
import com.africastalking.android.R;
import com.africastalking.android.ui.BaseActivity;
import com.africastalking.models.payment.checkout.CheckoutResponse;
import com.africastalking.services.PaymentService;

import timber.log.Timber;

public class PaymentActivity extends BaseActivity {

    PaymentService payment;

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
                            BuildConfig.RPC_HOST,
                            BuildConfig.RPC_PORT, true);
                    AfricasTalking.setLogger(new Logger() {
                        @Override
                        public void log(String message, Object... args) {
                            Timber.d(message, args);
                        }
                    });

                    Timber.i("Getting payment service");
                    payment = AfricasTalking.getPaymentService();

                    Timber.i("Checking out KES 100 from 0718769882");
                    MobileCheckoutRequest request = new MobileCheckoutRequest();
                    request.productName = "TestProduct";
                    request.phoneNumber = "0718769882";
                    request.currencyCode = "KES";
                    request.amount = 100;
                    CheckoutResponse res = payment.checkout(request);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.payment_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mnuCardCheckout) {
            if (payment != null) {

                new CardCheckout(payment).startCheckout(new CardCheckoutRequest(), this, new Callback<CheckoutResponse>() {
                    @Override
                    public void onSuccess(CheckoutResponse data) {
                        Log.e("PaymentActivity", data.toString());
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        throwable.printStackTrace();
                        Log.e("PaymentActivity", throwable.getMessage() + "");
                    }
                });
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
