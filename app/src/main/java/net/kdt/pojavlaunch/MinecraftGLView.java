package net.kdt.pojavlaunch;

import android.content.*;
import android.util.*;
import android.view.*;
import com.kdt.glsupport.*;

public class MinecraftGLView extends GLTextureView
{
	// private View.OnTouchListener mTouchListener;
    public MinecraftGLView(Context context) {
        super(context);
		//setPreserveEGLContextOnPause(true);
    }

    public MinecraftGLView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
		//setPreserveEGLContextOnPause(true);
    }

	@Override
	public void setOnTouchListener(View.OnTouchListener l)
	{
		super.setOnTouchListener(l);
		// mTouchListener = l;
	}

	@Override
	public void setOnClickListener(View.OnClickListener l)
	{
		super.setOnClickListener(l);
	}
}

