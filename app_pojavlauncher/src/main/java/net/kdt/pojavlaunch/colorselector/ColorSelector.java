package net.kdt.pojavlaunch.colorselector;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.kdt.SideDialogView;

import net.kdt.pojavlaunch.R;

public class ColorSelector extends SideDialogView implements HueSelectionListener, RectangleSelectionListener, AlphaSelectionListener, TextWatcher{
    private static final int ALPHA_MASK = ~(0xFF << 24);
    private HueView mHueView;
    private SVRectangleView mLuminosityIntensityView;
    private AlphaView mAlphaView;
    private ColorSideBySideView mColorView;
    private EditText mTextView;

    private ColorSelectionListener mColorSelectionListener;
    private final float[] mHueTemplate = new float[] {0,1,1};
    private final float[] mHsvSelected = new float[] {360,1,1};
    private int mAlphaSelected = 0xff;
    private ColorStateList mTextColors;
    private boolean mWatch = true;

    private boolean mAlphaEnabled = true;


    public ColorSelector(Context context, ViewGroup parent, @Nullable ColorSelectionListener colorSelectionListener) {
        super(context, parent, R.layout.dialog_color_selector);
        mColorSelectionListener = colorSelectionListener;
    }

    @Override
    protected void onInflate() {
        super.onInflate();
        // Initialize the view contents
        mHueView = mDialogContent.findViewById(R.id.color_selector_hue_view);
        mLuminosityIntensityView = mDialogContent.findViewById(R.id.color_selector_rectangle_view);
        mAlphaView = mDialogContent.findViewById(R.id.color_selector_alpha_view);
        mColorView = mDialogContent.findViewById(R.id.color_selector_color_view);
        mTextView = mDialogContent.findViewById(R.id.color_selector_hex_edit);
        runColor(Color.RED);
        mHueView.setHueSelectionListener(this);
        mLuminosityIntensityView.setRectSelectionListener(this);
        mAlphaView.setAlphaSelectionListener(this);
        mTextView.addTextChangedListener(this);
        mTextColors = mTextView.getTextColors();
        mAlphaView.setVisibility(mAlphaEnabled ? View.VISIBLE : View.GONE);

        // Set elevation to show above other side dialogs.
        // Jank, should be done better
        View contentParent = mDialogContent.findViewById(R.id.side_dialog_scrollview);
        if(contentParent != null) {
            ViewGroup dialogLayout = (ViewGroup) mDialogContent.getParent();
            dialogLayout.setElevation(11);
            dialogLayout.setTranslationZ(11);
        }
    }

    /**
     * Shows the color selector with the default (red) color selected.
     */
    public void show(boolean fromRight) {
        show(fromRight, Color.RED);
    }

    /**
     * Shows the color selector with the desired ARGB color selected
     * @param previousColor the desired ARGB color
     */
    public void show(boolean fromRight, int previousColor) {
        appear(fromRight);
        runColor(previousColor); // initialize
        dispatchColorChange(); // set the hex text
    }

    @Override
    public void onHueSelected(float hue) {
        mHsvSelected[0] = mHueTemplate[0] = hue;
        mLuminosityIntensityView.setColor(Color.HSVToColor(mHueTemplate), true);
        dispatchColorChange();
    }

    @Override
    public void onLuminosityIntensityChanged(float luminosity, float intensity) {
        mHsvSelected[1] = intensity;
        mHsvSelected[2] = luminosity;
        dispatchColorChange();
    }

    @Override
    public void onAlphaSelected(int alpha) {
        mAlphaSelected = alpha;
        dispatchColorChange();
    }

    /**
     * Replaces the alpha value of the color passed in, and returns the result.
     * @param color the color to replace the alpha of
     * @param alpha the alpha to use
     * @return the new color
     */
    public static int setAlpha(int color, int alpha) {
        return color & ALPHA_MASK | ((alpha & 0xFF) << 24);
    }

    //IUO: called on all color changes
    protected void dispatchColorChange() {
        int color = Color.HSVToColor(mAlphaSelected, mHsvSelected);
        mColorView.setColor(color);
        mWatch = false;
        mTextView.setText(String.format("%08X",color));
        notifyColorSelector(color);
    }

    //IUO: sets all Views to render the desired color. Used for initialization and HEX color input
    protected void runColor(int color) {
        Color.RGBToHSV(Color.red(color), Color.green(color), Color.blue(color), mHsvSelected);
        mHueTemplate[0] = mHsvSelected[0];
        mHueView.setHue(mHsvSelected[0]);
        mLuminosityIntensityView.setColor(Color.HSVToColor(mHueTemplate), false);
        mLuminosityIntensityView.setLuminosityIntensity(mHsvSelected[2], mHsvSelected[1]);
        mAlphaSelected = Color.alpha(color);
        mAlphaView.setAlpha(mAlphaEnabled ? mAlphaSelected : 255);
        mColorView.setColor(color);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable s) {
        if(mWatch) {
            try {
                int color = Integer.parseInt(s.toString(), 16);
                mTextView.setTextColor(mTextColors);
                runColor(color);
            }catch (NumberFormatException exception) {
                mTextView.setTextColor(Color.RED);
            }
        }else{
            mWatch = true;
        }
    }

    public void setColorSelectionListener(ColorSelectionListener listener){
        mColorSelectionListener = listener;
    }

    public void setAlphaEnabled(boolean alphaEnabled){
        mAlphaEnabled = alphaEnabled;
        if(mAlphaView != null) {
            mAlphaView.setVisibility(alphaEnabled ? View.VISIBLE : View.GONE);
            mAlphaView.setAlpha(255);
        }
    }

    private void notifyColorSelector(int color){
        if(mColorSelectionListener != null)
            mColorSelectionListener.onColorSelected(color);
    }
}
