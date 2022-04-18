package net.kdt.pojavlaunch.colorselector;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import net.kdt.pojavlaunch.R;

public class ColorSelector implements HueSelectionListener, RectangleSelectionListener {
    HueView mHueView;
    SVRectangleView mLuminosityIntensityView;
    TextView mTextView;
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
        mTextView = view.findViewById(R.id.color_selector_color_text_view);
        bldr.setView(view);
        mDialog = bldr.create();
    }

    public void show() {
        mDialog.show();
    }

    @Override
    public void onHueSelected(float hue) {
        mHueTemplate[0] = hue;
        mLuminosityIntensityView.setColor(Color.HSVToColor(mHueTemplate));
    }

    @Override
    public void onLuminosityIntensityChanged(float luminosity, float intensity) {
        mHsvSelected[0] = mHueTemplate[0];
        mHsvSelected[1] = (intensity -1)*-1 ;
        mHsvSelected[2] = luminosity;
        mTextView.setTextColor(Color.HSVToColor(mHsvSelected));
    }
}
