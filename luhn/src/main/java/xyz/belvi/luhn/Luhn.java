package xyz.belvi.luhn;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StyleRes;

import xyz.belvi.luhn.interfaces.LuhnCallback;

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
