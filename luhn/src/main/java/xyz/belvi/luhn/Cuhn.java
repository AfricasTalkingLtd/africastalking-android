package xyz.belvi.luhn;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Arrays;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import xyz.belvi.luhn.cardValidator.models.LuhnCard;
import xyz.belvi.luhn.customTextInputLayout.inputLayouts.CardTextInputLayout;
import xyz.belvi.luhn.customTextInputLayout.inputLayouts.PinTextInputLayout;
import xyz.belvi.luhn.customTextInputLayout.textWatchers.CardNumberTextWatcher;
import xyz.belvi.luhn.customTextInputLayout.textWatchers.CvvTextWatcher;
import xyz.belvi.luhn.customTextInputLayout.textWatchers.ExpiringDateTextWatcher;
import xyz.belvi.luhn.customTextInputLayout.textWatchers.PinTextWatcher;
import xyz.belvi.luhn.interfaces.LuhnVerifier;
import xyz.belvi.luhn.screens.CardVerificationProgressScreen;

import static xyz.belvi.luhn.Luhn.CARD_IO;
import static xyz.belvi.luhn.Luhn.STYLE_KEY;

public final class Cuhn extends BaseActivity implements LuhnVerifier {

    private CardTextInputLayout cvvInputLayout, expiryInputLayout, cardNumber;
    private PinTextInputLayout pinInputLayout;

    private int expMonth, expYear;
    private String cardPan, cardName, cvv, expDate, pin;

    private final int CARDIO_REQUEST_ID = 555;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.lh_activity_add_card);
        initStyle(getIntent().getIntExtra(STYLE_KEY, R.style.LuhnStyle));
        attachKeyboardListeners(R.id.root_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            return;
        }
        super.onBackPressed();
        Luhn.sLuhnCallback.onFinished(false);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CARDIO_REQUEST_ID) {
            if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);

                cardNumber.getEditText().setText(scanResult.getFormattedCardNumber());
                if (scanResult.isExpiryValid()) {
                    String month = String.valueOf(scanResult.expiryMonth).length() == 1 ? "0" + scanResult.expiryMonth : String.valueOf(scanResult.expiryMonth);
                    String year = String.valueOf(scanResult.expiryYear).substring(2);
                    expiryInputLayout.getEditText().setText(month + "/" + year);
                }

                if (scanResult.cvv != null) {
                    // Never log or display a CVV
                    cvvInputLayout.getEditText().setText(scanResult.cvv);
                }

            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
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
    public void startProgress() {
        progressScreen = new CardVerificationProgressScreen();
        progressScreen.show(getSupportFragmentManager(), "");
    }

    @Override
    public void dismissProgress() {
        progressScreen.dismissAllowingStateLoss();
    }


    @Override
    protected void initViews() {
        initCardField();
        initExpiryDateField();
        initCvvField();
        initPin();
        findViewById(R.id.btn_proceed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                if (Luhn.sLuhnCallback != null)
                    if (OTP_MODE)
                        Luhn.sLuhnCallback.otpRetrieved(Cuhn.this, Cuhn.this, otp);
                    else {
                        cardPan = cardPan.replace(" ", "");
                        Luhn.sLuhnCallback.cardDetailsRetrieved(Cuhn.this, new LuhnCard(cardPan, cardName, expDate, cvv, pin, expMonth, expYear), Cuhn.this);
                    }
            }
        });
    }




    private void initCardField() {
        cardNumber = (CardTextInputLayout) findViewById(R.id.cti_card_number_input);
        cardNumber.post(new Runnable() {
            @Override
            public void run() {
                cardNumber.getPasswordToggleView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!cardNumber.getEditText().getText().toString().isEmpty()) {
                            cardNumber.getEditText().setText("");
                        } else {
                            onScanPress(v);
                        }
                    }
                });
                cardNumber.getEditText().addTextChangedListener(new CardNumberTextWatcher(cardNumber) {
                    @Override
                    public void onValidated(boolean moveToNext, String cardNumberPan, String cardNameValue) {
                        cardPan = cardNumberPan;
                        cardName = cardNameValue;

                        if (moveToNext) {
                            findViewById(R.id.tiet_exp_input).requestFocus();
                        }
                        enableNextBtn();
                    }
                });
            }
        });
    }


    private void initExpiryDateField() {

        expiryInputLayout = (CardTextInputLayout) findViewById(R.id.ctil_expiry_input);

        expiryInputLayout.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    expiryInputLayout.setHint("Exp. Date");
                } else {
                    if (expiryInputLayout.getEditText().getText().toString().isEmpty())
                        expiryInputLayout.setHint("MM/YY");
                    else {
                        if (ExpiringDateTextWatcher.isValidExpiringDate(expiryInputLayout.getEditText().getText().toString())) {
                            expiryInputLayout.setError("");
                        } else {
                            expiryInputLayout.setError("Enter a valid expiration date");
                        }
                        expiryInputLayout.setHint("Exp. Date");
                    }
                }
            }
        });

        expiryInputLayout.post(new Runnable() {
            @Override
            public void run() {
                expiryInputLayout.getPasswordToggleView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        expiryInputLayout.requestFocus();
                        showInfo(R.string.exp_header, R.string.exp_details, R.drawable.payment_bank_card_expiration_info, false);

                    }
                });
                expiryInputLayout.getEditText().addTextChangedListener(new ExpiringDateTextWatcher(expiryInputLayout) {
                    @Override
                    protected void onValidated(boolean moveToNext, String expDateValue, int expMonthValue, int expYearValue) {
                        expDate = expDateValue;
                        expMonth = expMonthValue;
                        expYear = expYearValue;
                        if (moveToNext) {
                            findViewById(R.id.tiet_cvv_input).requestFocus();
                            expiryInputLayout.setError("");
                        }
                        enableNextBtn();
                    }
                });
            }

        });

    }

    private void initCvvField() {
        cvvInputLayout = (CardTextInputLayout) findViewById(R.id.ctil_cvv_input);

        cvvInputLayout.post(new Runnable() {
            @Override
            public void run() {
                cvvInputLayout.getPasswordToggleView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cvvInputLayout.requestFocus();
                        showInfo(R.string.cvv_header, R.string.cvv_desc, R.drawable.payment_bank_card_cvv_info, false);

                    }
                });
                cvvInputLayout.getEditText().addTextChangedListener(new CvvTextWatcher(cvvInputLayout) {
                    @Override
                    public void onValidated(boolean moveToNext, String cvvValue) {
                        cvv = cvvValue;
                        if (moveToNext && retrievePin)
                            findViewById(R.id.tiet_pin_input).requestFocus();
                        enableNextBtn();
                    }
                });
            }
        });

    }

    private void initPin() {
        pinInputLayout = (PinTextInputLayout) findViewById(R.id.ctil_pin_input);
        pinInputLayout.post(new Runnable() {
            @Override
            public void run() {
                pinInputLayout.setVisibility(retrievePin ? View.VISIBLE : View.GONE);
                pinInputLayout.getPasswordToggleView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pinInputLayout.requestFocus();
                        showInfo(R.string.pin_header, R.string.pin_details, R.drawable.payment_bank_pin, false);

                    }
                });
                pinInputLayout.getEditText().addTextChangedListener(new PinTextWatcher(pinInputLayout) {
                    @Override
                    public void onValidated(boolean moveToNext, String pinValue) {
                        pin = pinValue;
                        enableNextBtn();
                    }
                });
            }
        });

    }


    public void onScanPress(View v) {

        Intent scanIntent = new Intent(this, CardIOActivity.class);
        if (getIntent().hasExtra(CARD_IO)) {
            scanIntent.putExtras(getIntent().getBundleExtra(CARD_IO));
        } else {

            // customize these values to suit your needs.
            scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true); // default: false
            scanIntent.putExtra(CardIOActivity.EXTRA_SCAN_EXPIRY, true); // default: false
            scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, true); // default: false
            scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false); // default: false
            scanIntent.putExtra(CardIOActivity.EXTRA_USE_CARDIO_LOGO, true); // default: false
            scanIntent.putExtra(CardIOActivity.EXTRA_HIDE_CARDIO_LOGO, true); // default: false
            scanIntent.putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY, true); // default: false
        }
        // MY_SCAN_REQUEST_CODE is arbitrary and is only used within this activity.
        startActivityForResult(scanIntent, CARDIO_REQUEST_ID);
    }


    @Override
    protected void enableNextBtn() {
        if (OTP_MODE)
            findViewById(R.id.btn_proceed).setEnabled(otpInputLayout.hasValidInput());
        else if (retrievePin)
            findViewById(R.id.btn_proceed).setEnabled(cvvInputLayout.hasValidInput() && expiryInputLayout.hasValidInput() && cardNumber.hasValidInput() && pinInputLayout.hasValidInput());
        else
            findViewById(R.id.btn_proceed).setEnabled(cvvInputLayout.hasValidInput() && expiryInputLayout.hasValidInput() && cardNumber.hasValidInput());
    }


    private void disableAllFields() {
        TextInputLayout allFields[] = {pinInputLayout, cvvInputLayout, cardNumber, expiryInputLayout};
        for (TextInputLayout field : allFields) {
            field.setEnabled(false);
            field.setErrorEnabled(false);
        }
        Arrays.fill(allFields, null);
    }
}
