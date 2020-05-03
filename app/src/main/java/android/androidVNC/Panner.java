/**
 * Copyright (C) 2009 Michael A. MacDonald
 */
package android.androidVNC;

import android.graphics.PointF;
import android.os.Handler;
import android.os.SystemClock;

/**
 * Handles panning the screen continuously over a period of time
 * @author Michael A. MacDonald
 */
class Panner implements Runnable {
	
	VncCanvasActivity activity;
	Handler handler;
	PointF velocity;
	long lastSent;
	VelocityUpdater updater;
	
	private static final String TAG = "PANNER";
	
	/**
	 * Specify how the panning velocity changes over time
	 * @author Michael A. MacDonald
	 */
	interface VelocityUpdater {
		/**
		 * Called approximately every 50 ms to update the velocity of panning
		 * @param p X and Y components to update
		 * @param interval Milliseconds since last update
		 * @return False if the panning should stop immediately; true otherwise
		 */
		boolean updateVelocity(PointF p, long interval);
	}

	static class DefaultUpdater implements VelocityUpdater {
		
		static DefaultUpdater instance = new DefaultUpdater();

		/**
		 * Don't change velocity
		 */
		@Override
		public boolean updateVelocity(PointF p, long interval) {
			return true;
		}
		
	}
	
	Panner(VncCanvasActivity act, Handler hand) {
		activity = act;
		velocity = new PointF();
		handler = hand;
	}
	
	void stop()
	{
		handler.removeCallbacks(this);
	}
	
	void start(float xv, float yv, VelocityUpdater update)
	{
		if (update == null)
			update = DefaultUpdater.instance;
		updater = update;
		velocity.x = xv;
		velocity.y = yv;
		//Log.v(TAG, String.format("pan start %f %f", velocity.x, velocity.y));
		lastSent = SystemClock.uptimeMillis();
		
		handler.postDelayed(this, 50);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		long interval = SystemClock.uptimeMillis() - lastSent;
		lastSent += interval;
		double scale = (double)interval / 50.0;
		//Log.v(TAG, String.format("panning %f %d %d", scale, (int)((double)velocity.x * scale), (int)((double)velocity.y * scale)));
		if ( activity.vncCanvas.pan((int)((double)velocity.x * scale), (int)((double)velocity.y * scale)))
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
