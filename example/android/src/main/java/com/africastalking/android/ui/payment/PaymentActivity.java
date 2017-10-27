package com.africastalking.android.ui.payment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.africastalking.AfricasTalking;
import com.africastalking.models.payment.checkout.BankCheckoutRequest;
import com.africastalking.models.payment.checkout.CardCheckoutRequest;
import com.africastalking.models.payment.checkout.CheckoutRequest;
import com.africastalking.models.payment.checkout.MobileCheckoutRequest;
import com.africastalking.ui.Checkout;
import com.africastalking.utils.Callback;
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
                    Timber.i("Getting payment service");
                    payment = AfricasTalking.getPaymentService();

                    Timber.i("Checking out KES 100 from 0718769882");
                    MobileCheckoutRequest request = new MobileCheckoutRequest("TestProduct", "KES", 100, "0718769882");
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
        int itemId = item.getItemId();
        if (itemId == R.id.mnuCardCheckout || itemId == R.id.mnuBankCheckout) {

            CheckoutRequest request = itemId == R.id.mnuCardCheckout ?
                    new CardCheckoutRequest("TestProduct", "NGN", 6000) :
                    new BankCheckoutRequest("TestProduct", "NGN", 5000);

            if (payment != null) {
                Checkout checkout = new Checkout(payment);
                checkout.start(this, request, new Callback<CheckoutResponse>() {
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
