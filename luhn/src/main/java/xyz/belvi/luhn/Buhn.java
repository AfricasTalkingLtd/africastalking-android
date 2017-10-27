package xyz.belvi.luhn;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import xyz.belvi.luhn.cardValidator.models.LuhnBank;
import xyz.belvi.luhn.customTextInputLayout.inputLayouts.AutoCompleteDropdown;
import xyz.belvi.luhn.interfaces.LuhnVerifier;
import xyz.belvi.luhn.screens.CardVerificationProgressScreen;

import static xyz.belvi.luhn.Luhn.STYLE_KEY;


public class Buhn extends BaseActivity implements LuhnVerifier {

    AutoCompleteDropdown bankName, countryName;
    TextInputEditText accountName, accountNumber, otp;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.lh_activity_add_bank);
        initStyle(getIntent().getIntExtra(STYLE_KEY, R.style.LuhnStyle));
        attachKeyboardListeners(R.id.root_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Luhn.sLuhnCallback.onFinished(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void initViews() {

        accountName = (TextInputEditText) findViewById(R.id.account_name);
        accountNumber = (TextInputEditText) findViewById(R.id.account_number);
        otp = (TextInputEditText) findViewById(R.id.tiet_pin_input);


        bankName = (AutoCompleteDropdown) findViewById(R.id.bank_name);
        bankName.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.banks)));


        countryName = (AutoCompleteDropdown) findViewById(R.id.country_name);
        countryName.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.countries)));

        findViewById(R.id.btn_proceed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                if (Luhn.sLuhnCallback != null)
                    if (OTP_MODE)
                        Luhn.sLuhnCallback.otpRetrieved(Buhn.this, Buhn.this, otp.getText().toString());
                    else {
                        LuhnBank bank = new LuhnBank(
                                accountName.getText().toString(),
                                accountNumber.getTag().toString(),
                                bankName.getText().toString(),
                                countryName.getText().toString());
                        Luhn.sLuhnCallback.bankDetailsRetrieved(Buhn.this, bank, Buhn.this);
                    }
            }
        });
    }


    @Override
    public void onDetailsVerified(boolean isSuccessFul, String errorTitle, String errorMessage) {
        dismissProgress();
        if (isSuccessFul) {
            finish();
            if (Luhn.sLuhnCallback != null)
                Luhn.sLuhnCallback.onFinished(isSuccessFul);
        } else {
            showInfo(errorTitle, errorMessage, null, true);
        }
    }


    @Override
    public void requestOTP(int otpLength) {
        dismissProgress();
        //disableAllFields();
        //initOtp(otpLength);
        //enableNextBtn();
        Toast.makeText(this, "Enter OTP", Toast.LENGTH_LONG).show();
    }

    @Override
    public void startProgress() {
        progressScreen = new CardVerificationProgressScreen();
        progressScreen.show(getSupportFragmentManager(), "");
    }

    @Override
    public void dismissProgress() {
        progressScreen.dismissAllowingStateLoss();
    }

}
