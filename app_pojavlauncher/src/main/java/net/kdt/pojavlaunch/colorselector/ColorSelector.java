package net.kdt.pojavlaunch.colorselector;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import net.kdt.pojavlaunch.R;

public class ColorSelector implements HueSelectionListener, RectangleSelectionListener, AlphaSelectionListener, TextWatcher{
    static final int ALPHA_MASK = ~(0xFF << 24);
    HueView mHueView;
    SVRectangleView mLuminosityIntensityView;
    AlphaView mAlphaView;
    ColorSideBySideView mColorView;
    EditText mTextView;
    float[] mHueTemplate = new float[] {0,1,1};
    float[] mHsvSelected = new float[] {360,1,1};
    int mAlphaSelected = 0xff;
    boolean mWatch = true;
    AlertDialog mDialog;
    public ColorSelector(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_color_selector,null);
        mHueView = view.findViewById(R.id.color_selector_hue_view);
        mHueView.setHue(0f);
        mLuminosityIntensityView = view.findViewById(R.id.color_selector_rectangle_view);
        mLuminosityIntensityView.setColor(Color.RED);
        mLuminosityIntensityView.setLuminosityIntensity(1,1);
        mAlphaView = view.findViewById(R.id.color_selector_alpha_view);
        mAlphaView.setAlpha(0xff);
        mColorView = view.findViewById(R.id.color_selector_color_view);
        mColorView.setColor(Color.RED);
        mTextView = view.findViewById(R.id.color_selector_hex_edit);
        mHueView.setHueSelectionListener(this);
        mLuminosityIntensityView.setRectSelectionListener(this);
        mAlphaView.setAlphaSelectionListener(this);
        mTextView.addTextChangedListener(this);
        builder.setView(view);
        builder.setPositiveButton(android.R.string.ok,(dialog,which)->{

        });
        mDialog = builder.create();
    }

    public void show() {
        mDialog.show();
    }

    @Override
    public void onHueSelected(float hue) {
        mHsvSelected[0] = mHueTemplate[0] = hue;
        mLuminosityIntensityView.setColor(Color.HSVToColor(mHueTemplate));
        dispatchColorChange(Color.HSVToColor(mAlphaSelected, mHsvSelected));
    }

    @Override
    public void onLuminosityIntensityChanged(float luminosity, float intensity) {
        mHsvSelected[1] = intensity;
        mHsvSelected[2] = luminosity;
        dispatchColorChange(Color.HSVToColor(mAlphaSelected, mHsvSelected));
    }

    @Override
    public void onAlphaSelected(int alpha) {
        mAlphaSelected = alpha;
        dispatchColorChange(Color.HSVToColor(mAlphaSelected, mHsvSelected));
    }


    public static int setAlpha(int color, int alpha) {
        return color & ALPHA_MASK | ((alpha & 0xFF) << 24);
    }

    protected void dispatchColorChange(int color) {
        mColorView.setColor(color);
        mWatch = false;
        mTextView.setText(String.format("%08X",color));
    }

    protected void runColor(int color) {
        Color.RGBToHSV(Color.red(color), Color.green(color), Color.blue(color), mHsvSelected);
        mHueTemplate[0] = mHsvSelected[0];
        mHueView.setHue(mHsvSelected[0]);
        mLuminosityIntensityView.setColor(Color.HSVToColor(mHueTemplate));
        mLuminosityIntensityView.setLuminosityIntensity(mHsvSelected[2], mHsvSelected[1]);
        mAlphaView.setAlpha(Color.alpha(color));
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
                runColor(color);
            }catch (NumberFormatException ignored) {
            }
        }else{
            mWatch = true;
        }
    }
}
