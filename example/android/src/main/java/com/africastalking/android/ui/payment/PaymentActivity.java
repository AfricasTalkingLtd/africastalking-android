package com.africastalking.android.ui.payment;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.africastalking.AfricasTalking;
import com.africastalking.android.BuildConfig;
import com.africastalking.models.payment.Bank;
import com.africastalking.models.payment.BankTransferResponse;
import com.africastalking.models.payment.checkout.BankCheckoutRequest;
import com.africastalking.models.payment.checkout.BankCheckoutRequest.BankAccount;
import com.africastalking.models.payment.checkout.BankCode;
import com.africastalking.models.payment.checkout.CardCheckoutRequest;
import com.africastalking.models.payment.checkout.CheckoutRequest;
import com.africastalking.models.payment.checkout.MobileCheckoutRequest;
import com.africastalking.ui.Checkout;
import com.africastalking.utils.Callback;
import com.africastalking.android.R;
import com.africastalking.models.payment.checkout.CheckoutResponse;
import com.africastalking.services.PaymentService;
import com.africastalking.android.ui.BaseActivity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import timber.log.Timber;

public class PaymentActivity extends BaseActivity {

    PaymentService payment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        @SuppressLint("StaticFieldLeak") AsyncTask<Void, String, Void> task = new AsyncTask<Void, String, Void>() {

            @Override
            protected Void doInBackground(Void... params) {

                try {
                    Timber.i("Getting payment service");
                    payment = AfricasTalking.getPaymentService();

                    Timber.i("Checking out KES 100 from 0718769882");
                    MobileCheckoutRequest request = new MobileCheckoutRequest(BuildConfig.PRODUCT_NAME, "KES 100", "0718769882");
                    CheckoutResponse res = payment.checkout(request);

                    Timber.i(res.transactionId);
                    Timber.i(res.status);
                    Timber.i(res.description);

                    Timber.i("Bank transfer of NGN 5000");
                    List<Bank> recipients = Arrays.asList(new Bank(new BankAccount("Fake", "23123434", BankCode.GTBank_NG), "NGN 5000", "Desc", new HashMap<String, String>()));
                    BankTransferResponse response = payment.bankTransfer(BuildConfig.PRODUCT_NAME, recipients);
                    BankTransferResponse.BankEntry entry = response.entries.get(0);
                    Timber.i(entry.transactionId);
                    Timber.i(entry.status);
                    Timber.i(entry.transactionFee);


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
                    new CardCheckoutRequest(BuildConfig.PRODUCT_NAME, "NGN 100", "Some desc") :
                    new BankCheckoutRequest(BuildConfig.PRODUCT_NAME, "NGN 100", "Some desc");

            if (payment != null) {
                Checkout checkout = new Checkout(payment);
                checkout.start(this, request, new Callback<CheckoutResponse>() {
                    @Override
                    public void onSuccess(CheckoutResponse data) {
                        Timber.i("Payment {\n%s\n%s\n%s\n}", data.transactionId, data.status, data.description);
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        throwable.printStackTrace();
                        Timber.e(throwable.getMessage());
                    }
                });
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
