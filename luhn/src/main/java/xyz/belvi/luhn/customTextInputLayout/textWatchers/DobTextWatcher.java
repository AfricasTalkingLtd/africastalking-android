package xyz.belvi.luhn.customTextInputLayout.textWatchers;

import android.text.Editable;
import android.widget.EditText;

import java.util.Timer;
import java.util.TimerTask;

import xyz.belvi.luhn.customTextInputLayout.inputLayouts.BankTextInputLayout;

/**
 * Created by aksalj on 10/11/2017.
 */

public class DobTextWatcher extends BankTextWatcher {

    private boolean edited = false;
    private final String divider = "-";
    private String finalText;

    public DobTextWatcher(BankTextInputLayout inputLayout, Runnable action) {
        super(inputLayout, 10, "Enter a valid date of birth (YYYY-MM-DD)", action);
    }

    @Override
    public void afterTextChanged(Editable editText) {
        timer.cancel();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                instance.post(new Runnable() {
                    @Override
                    public void run() {
                        if (finalText.matches("^[1-2]\\d{3}-(:?0[1-9]|1[0-2])-(:?0[1-9]|[1-2][0-9]|3[01])$")) {
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

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        EditText target = instance.getEditText();

        if (edited) {
            edited = false;
            return;
        }

        String working = getEditText();

        working = manageDateDivider(working, 4, start, before);
        working = manageDateDivider(working, 7, start, before);

        edited = true;
        finalText = working;
        target.setText(working);
        target.setSelection(target.getText().length());
    }

    private String manageDateDivider(String working, int position, int start, int before) {
        if (working.length() == position) {
            if (before <= position && start < position)
                return working + divider;
            else
                return working.substring(0, working.length() - 2);
        }
        return working;
    }

    private String getEditText() {
        String base = instance.getEditText().getText().toString();
        if (base.length() >= 10) {
            return base.substring(0, 10);
        }
        return base;
    }
}
