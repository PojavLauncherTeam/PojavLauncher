package net.kdt.pojavlaunch.colorselector;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import net.kdt.pojavlaunch.R;

public class ColorSelector implements HueSelectionListener, RectangleSelectionListener, AlphaSelectionListener, TextWatcher{
    private static final int ALPHA_MASK = ~(0xFF << 24);
    private final HueView mHueView;
    private final SVRectangleView mLuminosityIntensityView;
    private final AlphaView mAlphaView;
    private final ColorSideBySideView mColorView;
    private final EditText mTextView;
    private final AlertDialog mDialog;
    private ColorSelectionListener mColorSelectionListener;
    private float[] mHueTemplate = new float[] {0,1,1};
    private float[] mHsvSelected = new float[] {360,1,1};
    private int mAlphaSelected = 0xff;
    private ColorStateList mTextColors;
    private boolean mWatch = true;


    /**
     * Creates a color selector dialog for this Context.
     * @param context Context used for this ColorSelector dialog
     * @param colorSelectionListener Color selection listener to which the events will be sent to. Can be null.
     */
    public ColorSelector(Context context, ColorSelectionListener colorSelectionListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_color_selector,null);
        mHueView = view.findViewById(R.id.color_selector_hue_view);
        mLuminosityIntensityView = view.findViewById(R.id.color_selector_rectangle_view);
        mAlphaView = view.findViewById(R.id.color_selector_alpha_view);
        mColorView = view.findViewById(R.id.color_selector_color_view);
        mTextView = view.findViewById(R.id.color_selector_hex_edit);
        runColor(Color.RED);
        mHueView.setHueSelectionListener(this);
        mLuminosityIntensityView.setRectSelectionListener(this);
        mAlphaView.setAlphaSelectionListener(this);
        mTextView.addTextChangedListener(this);
        mTextColors = mTextView.getTextColors();
        builder.setView(view);
        builder.setPositiveButton(android.R.string.ok,(dialog,which)->{
            if (mColorSelectionListener != null) {
                mColorSelectionListener.onColorSelected(Color.HSVToColor(mAlphaSelected, mHsvSelected));
            }
        });
        builder.setNegativeButton(android.R.string.cancel, ((dialog, which) -> {}));
        mColorSelectionListener = colorSelectionListener;
        mDialog = builder.create();
    }

    /**
     * Shows the color selector with the default (red) color selected.
     */
    public void show() {
        runColor(Color.RED);
        dispatchColorChange();
        mDialog.show();
    }

    /**
     * Shows the color selector with the desired ARGB color selected
     * @param previousColor the desired ARGB color
     */
    public void show(int previousColor) {
        runColor(previousColor); // initialize
        dispatchColorChange(); // set the hex text
        mDialog.show();
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
    }

    //IUO: sets all Views to render the desired color. Used for initilaization and HEX color input
    protected void runColor(int color) {
        Color.RGBToHSV(Color.red(color), Color.green(color), Color.blue(color), mHsvSelected);
        mHueTemplate[0] = mHsvSelected[0];
        mHueView.setHue(mHsvSelected[0]);
        mLuminosityIntensityView.setColor(Color.HSVToColor(mHueTemplate), false);
        mLuminosityIntensityView.setLuminosityIntensity(mHsvSelected[2], mHsvSelected[1]);
        mAlphaSelected = Color.alpha(color);
        mAlphaView.setAlpha(mAlphaSelected);
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
}
