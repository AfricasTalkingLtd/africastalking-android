package xyz.belvi.luhn;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import xyz.belvi.luhn.cardValidator.models.LuhnBank;
import xyz.belvi.luhn.customTextInputLayout.inputLayouts.AutoCompleteDropdown;
import xyz.belvi.luhn.customTextInputLayout.inputLayouts.BankTextInputLayout;
import xyz.belvi.luhn.customTextInputLayout.inputLayouts.CardTextInputLayout;
import xyz.belvi.luhn.interfaces.LuhnVerifier;
import xyz.belvi.luhn.screens.CardVerificationProgressScreen;

import static xyz.belvi.luhn.Luhn.STYLE_KEY;


public final class Buhn extends BaseActivity implements LuhnVerifier {

    AutoCompleteDropdown bankName, countryName;
    BankTextInputLayout accountName, accountNumber;
    CardTextInputLayout otp;

    private static ArrayList<String> banks = new ArrayList<>();
    private static HashMap<String, String> countries = new HashMap<>();

    static {
        banks.add("Access Bank");
        banks.add("Fidelity");
        banks.add("GT Bank");
        banks.add("Zenith Bank");
        banks.add("Wema Bank");
        banks.add("Comercica");

        countries.put("NG", "Nigeria");
        countries.put("KE", "Kenya");
    }


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

        accountName = (BankTextInputLayout) findViewById(R.id.account_name_layout);
        accountNumber = (BankTextInputLayout) findViewById(R.id.account_number_layout);
        otp = (CardTextInputLayout) findViewById(R.id.ctil_otp_input);


        BankTextInputLayout.BankTextWatcher accountNameWatcher = new BankTextInputLayout.BankTextWatcher(
                accountName, 2, "Enter a valid account name",
                new Runnable() {
                    @Override
                    public void run() {
                        enableNextBtn();
                    }
                });
        BankTextInputLayout.BankTextWatcher accountNumberWatcher = new BankTextInputLayout.BankTextWatcher(
                accountNumber, 5, "Enter a valid account number",
                new Runnable() {
                    @Override
                    public void run() {
                        enableNextBtn();
                    }
                });

        accountName.getEditText().addTextChangedListener(accountNameWatcher);
        accountNumber.getEditText().addTextChangedListener(accountNumberWatcher);


        // TODO: Load banks based on Country
        bankName = (AutoCompleteDropdown) findViewById(R.id.bank_name);
        bankName.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, banks));

        countryName = (AutoCompleteDropdown) findViewById(R.id.country_name);
        countryName.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, countries.values().toArray(new String[countries.size()])));




        TextWatcher watchSelection = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enableNextBtn();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        bankName.addTextChangedListener(watchSelection);
        countryName.addTextChangedListener(watchSelection);

        findViewById(R.id.btn_proceed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                if (Luhn.sLuhnCallback != null)
                    if (OTP_MODE)
                        Luhn.sLuhnCallback.otpRetrieved(Buhn.this, Buhn.this, otp.getEditText().getText().toString());
                    else {
                        String countryCode = null;
                        String country = countryName.getText().toString();
                        for (String key : countries.keySet()) {
                            if (countries.get(key).contentEquals(country)) {
                                countryCode = key;
                                break;
                            }
                        }

                        LuhnBank bank = new LuhnBank(
                                accountName.getEditText().getText().toString(),
                                accountNumber.getEditText().getText().toString(),
                                bankName.getText().toString(),
                                countryCode);
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
        disableAllFields();
        initOtp(otpLength);
        enableNextBtn();
        Toast.makeText(this, "Enter OTP", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void enableNextBtn() {
        if (OTP_MODE)
            findViewById(R.id.btn_proceed).setEnabled(otpInputLayout.hasValidInput());
        else
            findViewById(R.id.btn_proceed).setEnabled(accountName.hasValidInput() &&
                    accountNumber.hasValidInput() &&
                    bankName.getText().length() > 0 &&
                    countryName.getText().length() > 0
            );
    }

    private void disableAllFields() {
        BankTextInputLayout allFields[] = {accountNumber, accountName };
        AutoCompleteDropdown allSpinners[] = { bankName, countryName };
        for (BankTextInputLayout field : allFields) {
            field.setEnabled(false);
        }
        for (AutoCompleteDropdown spinner: allSpinners) {
            spinner.setEnabled(false);
        }
        Arrays.fill(allFields, null);
        Arrays.fill(allSpinners, null);
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
