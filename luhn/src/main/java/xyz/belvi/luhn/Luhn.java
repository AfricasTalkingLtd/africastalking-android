package xyz.belvi.luhn;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Arrays;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import xyz.belvi.luhn.cardValidator.models.LuhnCard;
import xyz.belvi.luhn.customTextInputLayout.inputLayouts.CardTextInputLayout;
import xyz.belvi.luhn.customTextInputLayout.inputLayouts.PinTextInputLayout;
import xyz.belvi.luhn.customTextInputLayout.textWatchers.CardNumberTextWatcher;
import xyz.belvi.luhn.customTextInputLayout.textWatchers.CvvTextWatcher;
import xyz.belvi.luhn.customTextInputLayout.textWatchers.ExpiringDateTextWatcher;
import xyz.belvi.luhn.customTextInputLayout.textWatchers.OTPTextWatcher;
import xyz.belvi.luhn.customTextInputLayout.textWatchers.PinTextWatcher;
import xyz.belvi.luhn.interfaces.LuhnCallback;
import xyz.belvi.luhn.interfaces.LuhnVerifier;
import xyz.belvi.luhn.screens.CardVerificationProgressScreen;

public final class Luhn {

    static final String STYLE_KEY = "xyz.belvi.Luhn.STYLE_KEY";
    static final String CARD_IO = "xyz.belvi.Luhn.CARD_IO";
    static LuhnCallback sLuhnCallback;

    public enum LuhnType {
        BANK,
        CARD
    }

    public static void startLuhn(Context context, LuhnType type, LuhnCallback luhnCallback) {
        sLuhnCallback = luhnCallback;
        Intent intent;
        switch (type) {
            case CARD:
                intent = new Intent(context, Cuhn.class);
                break;
            case BANK:
                intent = new Intent(context, Buhn.class);
                break;
            default:
                throw new RuntimeException("Invalid LuhnType");
        }
        context.startActivity(intent);
    }

    public static void startLuhn(Context context, LuhnType type, @StyleRes int style, LuhnCallback luhnCallback) {
        sLuhnCallback = luhnCallback;
        Intent intent;
        switch (type) {
            case CARD:
                intent = new Intent(context, Cuhn.class);
                break;
            case BANK:
                intent = new Intent(context, Buhn.class);
                break;
            default:
                throw new RuntimeException("Invalid LuhnType");
        }
        intent.putExtra(STYLE_KEY, style);
        context.startActivity(intent);
    }

    public static void startLuhn(Context context, LuhnType type, Bundle cardIOBundle, LuhnCallback luhnCallback) {
        sLuhnCallback = luhnCallback;
        Intent intent;
        switch (type) {
            case CARD:
                intent = new Intent(context, Cuhn.class);
                break;
            case BANK:
                intent = new Intent(context, Buhn.class);
                break;
            default:
                throw new RuntimeException("Invalid LuhnType");
        }
        intent.putExtra(CARD_IO, cardIOBundle);
        context.startActivity(intent);
    }

    public static void startLuhn(Context context, LuhnType type, @StyleRes int style, Bundle cardIOBundle, LuhnCallback luhnCallback) {
        sLuhnCallback = luhnCallback;
        Intent intent;
        switch (type) {
            case CARD:
                intent = new Intent(context, Cuhn.class);
                break;
            case BANK:
                intent = new Intent(context, Buhn.class);
                break;
            default:
                throw new RuntimeException("Invalid LuhnType");
        }
        intent.putExtra(CARD_IO, cardIOBundle);
        intent.putExtra(STYLE_KEY, style);
        context.startActivity(intent);
    }

}
