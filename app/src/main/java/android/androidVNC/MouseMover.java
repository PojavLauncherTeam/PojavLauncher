/**
 * Copyright 2010 Michael A. MacDonald
 */
package android.androidVNC;

import android.os.Handler;
import android.os.SystemClock;
import android.view.MotionEvent;

/**
 * Specialization of panner that moves the mouse instead of panning the screen
 * 
 * @author Michael A. MacDonald
 *
 */
class MouseMover extends Panner {

	public MouseMover(VncCanvasActivity act, Handler hand) {
		super(act, hand);
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		long interval = SystemClock.uptimeMillis() - lastSent;
		lastSent += interval;
		double scale = (double)interval / 50.0;
		VncCanvas canvas = activity.vncCanvas;
		//Log.v(TAG, String.format("panning %f %d %d", scale, (int)((double)velocity.x * scale), (int)((double)velocity.y * scale)));
		if ( canvas.processPointerEvent((int)(canvas.mouseX + ((double)velocity.x * scale)), (int)(canvas.mouseY + ((double)velocity.y * scale)), MotionEvent.ACTION_MOVE, 0, false, false))
		{
			if (updater.updateVelocity(velocity, interval))
			{
				handler.postDelayed(this, 50);
			}
			else
			{
				//Log.v(TAG, "Updater requests stop");
				stop();
			}
		}
		else
		{
			//Log.v(TAG, "Panning failed");
			stop();
		}
	}

}
