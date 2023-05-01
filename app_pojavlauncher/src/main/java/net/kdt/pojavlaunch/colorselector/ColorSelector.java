package net.kdt.pojavlaunch.colorselector;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.Nullable;

import net.kdt.pojavlaunch.R;

public class ColorSelector implements HueSelectionListener, RectangleSelectionListener, AlphaSelectionListener, TextWatcher{
    private static final int ALPHA_MASK = ~(0xFF << 24);
    private final View mRootView;
    private final HueView mHueView;
    private final SVRectangleView mLuminosityIntensityView;
    private final AlphaView mAlphaView;
    private final ColorSideBySideView mColorView;
    private final EditText mTextView;

    private ColorSelectionListener mColorSelectionListener;
    private final float[] mHueTemplate = new float[] {0,1,1};
    private final float[] mHsvSelected = new float[] {360,1,1};
    private int mAlphaSelected = 0xff;
    private final ColorStateList mTextColors;
    private boolean mWatch = true;

    private boolean mAlphaEnabled = true;


    /**
     * Creates a color selector dialog for this Context.
     * @param context Context used for this ColorSelector dialog
     * @param colorSelectionListener Color selection listener to which the events will be sent to. Can be null.
     */
    public ColorSelector(Context context, ViewGroup parent, @Nullable ColorSelectionListener colorSelectionListener) {

        mRootView = LayoutInflater.from(context).inflate(R.layout.dialog_color_selector,parent, false);
        mHueView = mRootView.findViewById(R.id.color_selector_hue_view);
        mLuminosityIntensityView = mRootView.findViewById(R.id.color_selector_rectangle_view);
        mAlphaView = mRootView.findViewById(R.id.color_selector_alpha_view);
        mColorView = mRootView.findViewById(R.id.color_selector_color_view);
        mTextView = mRootView.findViewById(R.id.color_selector_hex_edit);
        runColor(Color.RED);
        mHueView.setHueSelectionListener(this);
        mLuminosityIntensityView.setRectSelectionListener(this);
        mAlphaView.setAlphaSelectionListener(this);
        mTextView.addTextChangedListener(this);
        mTextColors = mTextView.getTextColors();

        mColorSelectionListener = colorSelectionListener;

        parent.addView(mRootView);
    }

    /** @return The root view, mainly for position manipulation purposes */
    public View getRootView(){
        return mRootView;
    }

    /**
     * Shows the color selector with the default (red) color selected.
     */
    public void show() {
        show(Color.RED);
    }

    /**
     * Shows the color selector with the desired ARGB color selected
     * @param previousColor the desired ARGB color
     */
    public void show(int previousColor) {
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
        mAlphaView.setVisibility(alphaEnabled ? View.VISIBLE : View.GONE);
        mAlphaView.setAlpha(255);
    }

    private void notifyColorSelector(int color){
        if(mColorSelectionListener != null)
            mColorSelectionListener.onColorSelected(color);
    }
}
