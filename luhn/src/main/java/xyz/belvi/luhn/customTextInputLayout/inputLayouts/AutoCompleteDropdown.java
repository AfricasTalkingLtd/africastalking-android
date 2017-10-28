package xyz.belvi.luhn.customTextInputLayout.inputLayouts;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;


public class AutoCompleteDropdown extends AppCompatAutoCompleteTextView {
    private static final int MAX_CLICK_DURATION = 200;
    private long startClickTime;
    private boolean isPopup;
    private int mPosition = ListView.INVALID_POSITION;

    public AutoCompleteDropdown(Context context) {
        super(context);
    }

    public AutoCompleteDropdown(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoCompleteDropdown(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean enoughToFilter() {
        return true;
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction,
                                  Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (focused) {
            performFiltering("", 0);
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getWindowToken(), 0);
            setKeyListener(null);
            dismissDropDown();
        } else {
            isPopup = false;
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP: {
                if (isPopup) {
                    dismissDropDown();
                } else {
                    requestFocus();
                    showDropDown();
                }
                break;
            }
        }

        return super.onTouchEvent(event);
    }

    @Override
    public void showDropDown() {
        super.showDropDown();
        isPopup = true;
    }

    @Override
    public void dismissDropDown() {
        super.dismissDropDown();
        isPopup = false;
    }

    public int getPosition() {
        return mPosition;
    }
}