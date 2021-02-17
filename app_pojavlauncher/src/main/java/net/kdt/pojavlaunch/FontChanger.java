package net.kdt.pojavlaunch;

import android.graphics.*;
import android.view.*;
import android.widget.*;
import android.content.*;
import com.kdt.mcgui.*;

public class FontChanger
{
	private static Typeface fNotoSans, fMinecraftTen;
	
	public static void initFonts(Context ctx) {
		fNotoSans = Typeface.createFromAsset(ctx.getAssets(), "font/NotoSans-Bold.ttf");
		fMinecraftTen = Typeface.createFromAsset(ctx.getAssets(), "font/minecraft-ten.ttf");
	}
	
	public static void changeFonts(ViewGroup viewTree) {
		View child;
        for(int i = 0; i < viewTree.getChildCount(); ++i) {
            child = viewTree.getChildAt(i);
            if (child instanceof ViewGroup) {
                changeFonts((ViewGroup) child);
            } else if (child instanceof TextView) {
                changeFont((TextView) child);
            }
        }
	}
	
	public static void changeFont(TextView view) {
		view.setTypeface(view instanceof MineButton ? fMinecraftTen : fNotoSans);
	}
}
