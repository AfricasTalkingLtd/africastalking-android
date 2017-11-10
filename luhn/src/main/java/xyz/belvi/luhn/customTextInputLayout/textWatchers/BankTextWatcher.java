package xyz.belvi.luhn.customTextInputLayout.textWatchers;

import android.text.Editable;
import android.text.TextWatcher;

import java.util.Timer;
import java.util.TimerTask;

import xyz.belvi.luhn.customTextInputLayout.inputLayouts.BankTextInputLayout;


public class BankTextWatcher implements TextWatcher {

    static final long DEBOUNCE_DELAY = 500;
    Timer timer = new Timer();

    int minLength = 1;
    String message;
    Runnable action;
    BankTextInputLayout instance;

    public BankTextWatcher(BankTextInputLayout inputLayout, int minLength, String errorMessage, Runnable action) {
        this.minLength = minLength;
        this.message = errorMessage;
        this.action = action;
        this.instance = inputLayout;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) { }

    @Override
    public void afterTextChanged(final Editable text) {
        timer.cancel();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                instance.post(new Runnable() {
                    @Override
                    public void run() {
                        if (text.length() >= minLength) {
                            instance.setHasValidInput(true);
                            instance.setError("");
                        } else {
                            instance.setError(message);
                            instance.setHasValidInput(false);
                        }
                        action.run();
                    }
                });
            }
        }, DEBOUNCE_DELAY);

    }
}
