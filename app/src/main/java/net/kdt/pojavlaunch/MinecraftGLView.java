package net.kdt.pojavlaunch;

import android.content.*;
import android.util.*;
import android.view.*;
import com.kdt.glsupport.*;

public class MinecraftGLView extends GLTextureView
{
	private View.OnTouchListener mTouchListener;
    public MinecraftGLView(Context context) {
        super(context);
		//setPreserveEGLContextOnPause(true);
    }

    public MinecraftGLView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
		//setPreserveEGLContextOnPause(true);
    }
/*
	@Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
		//super.surfaceDestroyed(surfaceHolder);
        System.out.println("Surface destroyed!");
		//new Throwable("Surface destroyed!").printStackTrace();
    }
*/
	@Override
	public void setOnTouchListener(View.OnTouchListener l)
	{
		super.setOnTouchListener(l);
		mTouchListener = l;
	}

	@Override
	public void setOnClickListener(View.OnClickListener l)
	{
		super.setOnClickListener(l);
	}

	@Override
	public boolean onCapturedPointerEvent(MotionEvent event)
	{
		mTouchListener.onTouch(this, event);
		return super.onCapturedPointerEvent(event);
	}
}

