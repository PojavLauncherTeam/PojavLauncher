package net.kdt.pojavlaunch.colorselector;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import net.kdt.pojavlaunch.R;

public class ColorSelector implements HueSelectionListener, RectangleSelectionListener, AlphaSelectionListener{
    HueView mHueView;
    SVRectangleView mLuminosityIntensityView;
    EditText mTextView;
    float[] mHueTemplate = new float[] {0,1,1};
    float[] mHsvSelected = new float[] {360,1,1};
    AlertDialog mDialog;
    public ColorSelector(Context context) {
        AlertDialog.Builder bldr = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_color_selector,null);
        mHueView = view.findViewById(R.id.color_selector_hue_view);
        mHueView.setHueSelectionListener(this);
        mLuminosityIntensityView = view.findViewById(R.id.color_selector_rectangle_view);
        mLuminosityIntensityView.setRectSelectionListener(this);
        mLuminosityIntensityView.setLuminosityIntensity(1,1);
        //mTextView = view.findViewById(R.id.color_selector_hex_edit);
        bldr.setView(view);
        bldr.setPositiveButton(android.R.string.ok,(dialog,which)->{

        });
        mDialog = bldr.create();
    }

    public void show() {
        mDialog.show();
    }

    @Override
    public void onHueSelected(float hue, boolean tapping) {
        mHsvSelected[0] = mHueTemplate[0] = hue;
        mLuminosityIntensityView.setColor(Color.HSVToColor(mHueTemplate));
        dispatchColorChange(Color.HSVToColor(mHsvSelected),tapping);
    }

    @Override
    public void onLuminosityIntensityChanged(float luminosity, float intensity, boolean tapping) {
        mHsvSelected[1] = intensity;
        mHsvSelected[2] = luminosity;
        dispatchColorChange(Color.HSVToColor(mHsvSelected),tapping);
    }

    @Override
    public void onAlphaSelected(int alpha, boolean tapping) {

    }

    protected void dispatchColorChange(int color, boolean tapping) {

    }
}
