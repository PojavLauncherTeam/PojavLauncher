package net.kdt.pojavlaunch;

import android.content.res.*;
import android.graphics.*;
import android.view.*;
import android.widget.*;

public class FontChanger
{
    private Typeface typeface;

    public FontChanger(Typeface typeface)
    {
        this.typeface = typeface;
    }

    public FontChanger(AssetManager assets, String assetsFontFileName)
    {
        typeface = Typeface.createFromAsset(assets, assetsFontFileName);
    }

    public ViewGroup replaceFonts(ViewGroup viewTree)
    {
        View child;
        for(int i = 0; i < viewTree.getChildCount(); ++i) {
            child = viewTree.getChildAt(i);
            if (child instanceof ViewGroup) {
                replaceFonts((ViewGroup)child);
            } else if (child instanceof TextView) {
                replaceFont((TextView) child);
            }
        }
		return viewTree;
    }
	
	public View replaceFont(TextView view) {
		view.setTypeface(typeface);
		return view;
	}
}
