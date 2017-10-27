package xyz.belvi.luhn;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import java.util.Arrays;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import xyz.belvi.luhn.customTextInputLayout.inputLayouts.CardTextInputLayout;
import xyz.belvi.luhn.screens.CardVerificationProgressScreen;

public abstract class BaseActivity extends AppCompatActivity {
    private ViewTreeObserver.OnGlobalLayoutListener keyboardLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            int navigationBarHeight = 0;
            int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                navigationBarHeight = getResources().getDimensionPixelSize(resourceId);
            }

            // status bar height
            int statusBarHeight = 0;
            resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                statusBarHeight = getResources().getDimensionPixelSize(resourceId);
            }

            // display window size for the app layout
            Rect rect = new Rect();
            getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);

            // screen height - (user app height + status + nav) ..... if non-zero, then there is a soft keyboard
            int keyboardHeight = rootLayout.getRootView().getHeight() - (statusBarHeight + navigationBarHeight + rect.height());

            if (keyboardHeight <= 0) {
                onHideKeyboard();
            } else {
                onShowKeyboard(keyboardHeight);
            }
        }
    };

    private boolean keyboardListenersAttached;
    private ViewGroup rootLayout;
    protected boolean retrievePin;
    protected CardVerificationProgressScreen progressScreen;
    protected LinearLayout llBottomSheet;
    protected BottomSheetBehavior bottomSheetBehavior;
    protected CardTextInputLayout otpInputLayout;
    protected boolean OTP_MODE;

    protected void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    protected void showKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, 0);
        }
    }


    protected void onShowKeyboard(int keyboardHeight) {
        setButtonMargin(findViewById(R.id.btn_proceed), 0, 0, 0, 0);
    }

    protected void onHideKeyboard() {
        setButtonMargin(findViewById(R.id.btn_proceed), 16, 16, 16, 16);
    }

    protected abstract void initViews();

    protected void initStyle(int style) {
        TypedArray ta = obtainStyledAttributes(style, R.styleable.luhnStyle);
        String fontName = ta.getString(R.styleable.luhnStyle_luhn_typeface);
        String title = ta.getString(R.styleable.luhnStyle_luhn_title);
        includeCalligraphy(fontName);
        initViews();
        retrievePin = ta.getBoolean(R.styleable.luhnStyle_luhn_show_pin, false);
        ((AppCompatTextView) findViewById(R.id.toolbar_title)).setText(TextUtils.isEmpty(title) ? "Checkout" : title);
        findViewById(R.id.btn_proceed).setBackground(ta.getDrawable(R.styleable.luhnStyle_luhn_btn_verify_selector));
        findViewById(R.id.toolbar).setBackgroundColor(ta.getColor(R.styleable.luhnStyle_luhn_show_toolbar_color, ContextCompat.getColor(this, R.color.ln_colorPrimary)));
    }

    protected void includeCalligraphy(String font) {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(font)
                .setFontAttrId(R.attr.fontPath)
                .build());
    }

    protected void attachKeyboardListeners(@IdRes int rootLayoutId) {
        if (keyboardListenersAttached) {
            return;
        }

        rootLayout = (ViewGroup) findViewById(rootLayoutId);
        rootLayout.getViewTreeObserver().addOnGlobalLayoutListener(keyboardLayoutListener);

        keyboardListenersAttached = true;
    }

    protected void showInfo(@StringRes int header, @StringRes int desc, @DrawableRes int drawable, boolean error) {
        showInfo(getString(header), getString(desc), ContextCompat.getDrawable(this, drawable), false);
    }


    protected void showInfo(String header, String desc, @Nullable Drawable drawable, boolean error) {
        hideKeyboard();
        AppCompatTextView infoHeader = (AppCompatTextView) llBottomSheet.findViewById(R.id.info_header);
        AppCompatTextView infoDesc = (AppCompatTextView) llBottomSheet.findViewById(R.id.info_desc);
        AppCompatImageView infoImg = (AppCompatImageView) llBottomSheet.findViewById(R.id.info_img);
        if (error) {
            llBottomSheet.findViewById(R.id.info_img).setVisibility(View.GONE);
            ((AppCompatButton) findViewById(R.id.ok_dimiss)).setText("Close");
        } else {
            llBottomSheet.findViewById(R.id.info_img).setVisibility(View.VISIBLE);
            ((AppCompatButton) findViewById(R.id.ok_dimiss)).setText("Ok");
        }

        infoHeader.setText(header);
        infoDesc.setText(desc);
        if (drawable != null)
            infoImg.setImageDrawable(drawable);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }


    protected void dismiss(View v) {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    protected void setButtonMargin(View view, int left, int top, int right, int bottom) {
        android.support.v7.widget.LinearLayoutCompat.LayoutParams params = (android.support.v7.widget.LinearLayoutCompat.LayoutParams) view.getLayoutParams();
        params.setMargins(left, top, right, bottom); //left, top, right, bottom
        view.setLayoutParams(params);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (keyboardListenersAttached) {
            rootLayout.getViewTreeObserver().removeGlobalOnLayoutListener(keyboardLayoutListener);
        }
    }
}