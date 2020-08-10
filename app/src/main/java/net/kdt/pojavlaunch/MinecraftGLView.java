package net.kdt.pojavlaunch;

import android.content.*;
import android.util.*;
import android.view.*;

public class MinecraftGLView extends TextureView
// GLTextureView
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
}

