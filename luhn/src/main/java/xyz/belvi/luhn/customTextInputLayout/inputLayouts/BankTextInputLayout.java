package xyz.belvi.luhn.customTextInputLayout.inputLayouts;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.design.widget.TextInputLayout;
import android.util.AttributeSet;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by aksalj on 27/10/2017.
 */

public class BankTextInputLayout extends TextInputLayout {

    private boolean hasValidInput;
    private Object collapsingTextHelper;
    private Rect bounds;
    private Method recalculateMethod;
    private boolean hasUpdated;

    public BankTextInputLayout(Context context) {
        this(context, null);
    }

    public BankTextInputLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BankTextInputLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        adjustBounds();
        if (!hasUpdated) {
            try {
                Typeface face = Typeface.createFromAsset(getEditText().getContext().getAssets(), CalligraphyConfig.get().getFontPath());
                setTypeface(face);
                getEditText().setTypeface(face);
            } catch (Exception e) {
                e.printStackTrace();
            }
            hasUpdated = true;
        }

    }

    private void init() {
        try {
            Field cthField = TextInputLayout.class.getDeclaredField("mCollapsingTextHelper");
            cthField.setAccessible(true);
            collapsingTextHelper = cthField.get(this);


            Field boundsField = collapsingTextHelper.getClass().getDeclaredField("mCollapsedBounds");
            boundsField.setAccessible(true);
            bounds = (Rect) boundsField.get(collapsingTextHelper);

            recalculateMethod = collapsingTextHelper.getClass().getDeclaredMethod("recalculate");

        } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException e) {
            collapsingTextHelper = null;
            bounds = null;
            recalculateMethod = null;
            e.printStackTrace();
        }
    }

    private void adjustBounds() {
        if (collapsingTextHelper == null) {
            return;
        }

        try {
            bounds.left = getEditText().getLeft() + getEditText().getPaddingLeft();
            recalculateMethod.invoke(collapsingTextHelper);
        } catch (InvocationTargetException | IllegalAccessException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }


    public boolean hasValidInput() {
        return this.hasValidInput;
    }

    public BankTextInputLayout setHasValidInput(boolean hasValidInput) {
        this.hasValidInput = hasValidInput;
        return this;
    }
}
