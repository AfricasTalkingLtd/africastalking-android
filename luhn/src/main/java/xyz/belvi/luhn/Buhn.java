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
import java.util.List;

import xyz.belvi.luhn.cardValidator.models.LuhnBank;
import xyz.belvi.luhn.customTextInputLayout.inputLayouts.AutoCompleteDropdown;
import xyz.belvi.luhn.customTextInputLayout.inputLayouts.BankTextInputLayout;
import xyz.belvi.luhn.customTextInputLayout.inputLayouts.CardTextInputLayout;
import xyz.belvi.luhn.customTextInputLayout.textWatchers.BankTextWatcher;
import xyz.belvi.luhn.customTextInputLayout.textWatchers.DobTextWatcher;
import xyz.belvi.luhn.interfaces.LuhnVerifier;
import xyz.belvi.luhn.screens.CardVerificationProgressScreen;

import static xyz.belvi.luhn.Luhn.STYLE_KEY;


public final class Buhn extends BaseActivity implements LuhnVerifier {

    AutoCompleteDropdown bankCode;
    BankTextInputLayout accountName, accountNumber, dateOfBirth;
    CardTextInputLayout otp;

    private static HashMap<Integer, String> banks = new HashMap<>();
    private static List<Integer> bankCodes = new ArrayList<>();
    private static ArrayList<Integer> banksWithDateOfBirth = new ArrayList<>();

    static {
        // FIXME: Get banks from caller

        banks.put(234002, "FCMB NG");
        banks.put(234002, "Zenith Nigeria");
        banks.put(234003, "Access Nigeria");
        banks.put(234007, "Providus Nigeria");
        banks.put(234010, "Sterling Nigeria");

        banksWithDateOfBirth.add(234002);

        bankCodes = new ArrayList<>(banks.keySet());

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.lh_activity_add_bank);
        initStyle(getIntent().getIntExtra(STYLE_KEY, R.style.LuhnStyle), "Bank Checkout");
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
        dateOfBirth = (BankTextInputLayout) findViewById(R.id.account_dob_layout);
        otp = (CardTextInputLayout) findViewById(R.id.ctil_otp_input);


        BankTextWatcher accountNameWatcher = new BankTextWatcher(
                accountName, 2, "Enter a valid account name",
                new Runnable() {
                    @Override
                    public void run() {
                        enableNextBtn();
                    }
                });
        BankTextWatcher accountNumberWatcher = new BankTextWatcher(
                accountNumber, 5, "Enter a valid account number",
                new Runnable() {
                    @Override
                    public void run() {
                        enableNextBtn();
                    }
                });
        DobTextWatcher dobWatcher = new DobTextWatcher(
                dateOfBirth,
                new Runnable() {
                    @Override
                    public void run() {
                        enableNextBtn();
                    }
                });

        accountName.getEditText().addTextChangedListener(accountNameWatcher);
        accountNumber.getEditText().addTextChangedListener(accountNumberWatcher);
        dateOfBirth.getEditText().addTextChangedListener(dobWatcher);



        // Read banks
        bankCode = (AutoCompleteDropdown) findViewById(R.id.bank_code);
        bankCode.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, new ArrayList<String>(banks.values())));


        TextWatcher watchSelection = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                dateOfBirth.setVisibility(isDobEnabled() ? View.VISIBLE : View.GONE);
                enableNextBtn();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        bankCode.addTextChangedListener(watchSelection);

        findViewById(R.id.btn_proceed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                if (Luhn.sLuhnCallback != null)
                    if (OTP_MODE)
                        Luhn.sLuhnCallback.otpRetrieved(Buhn.this, Buhn.this, otp.getEditText().getText().toString());
                    else {
                        String name = bankCode.getText().toString();
                        Integer code = -1;

                        for (Integer c : bankCodes) {
                            if (banks.get(c).contentEquals(name)) {
                                code = c;
                                break;
                            }
                        }

                        LuhnBank bank = new LuhnBank(
                                accountName.getEditText().getText().toString(),
                                accountNumber.getEditText().getText().toString(),
                                code,
                                dateOfBirth.getEditText().getText().toString());
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
        if (OTP_MODE) {
            findViewById(R.id.btn_proceed).setEnabled(otpInputLayout.hasValidInput());
        } else {
            boolean moveNext = accountName.hasValidInput() &&
                    accountNumber.hasValidInput() &&
                    bankCode.getText().length() > 0;
            if (isDobEnabled()) {
                moveNext = moveNext && dateOfBirth.hasValidInput();
            }
            findViewById(R.id.btn_proceed).setEnabled(moveNext);
        }
    }

    private boolean isDobEnabled() {
        for (Integer c : bankCodes) {
            if (banks.get(c).contentEquals(bankCode.getText().toString())) {
                return banksWithDateOfBirth.contains(c);
            }
        }
        return false;
    }

    private void disableAllFields() {
        BankTextInputLayout allFields[] = { accountNumber, accountName, dateOfBirth };
        AutoCompleteDropdown allSpinners[] = { bankCode };
        for (BankTextInputLayout field : allFields) {
            field.setEnabled(false);
        }
        for (AutoCompleteDropdown spinner : allSpinners) {
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
