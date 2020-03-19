package net.kdt.pojavlaunch;

import android.view.*;

public class WindowAnimation
{
	public static void fadeIn(final View view, int duration) {
		// 0.1 -> 1.0
		doFade(view, true, duration);
	}
	
	public static void fadeOut(final View view, int duration) {
		// 1.0 -> 0.1
		doFade(view, false, duration);
	}
	
	private static void doFade(final View view, final boolean fadeIn, int duration) {
		final long millis = ((long) duration) / 10l;

		view.setVisibility(View.VISIBLE);
		view.setAlpha(fadeIn ? 0 : 1);
		view.setEnabled(false);
		new Thread(new Runnable(){
				private float alpha = fadeIn ? 0 : 1;
				@Override
				public void run()
				{
					try {
						while (fadeIn ? alpha < 1 : alpha > 0) {
							try {
								Thread.sleep(millis);
							} catch (InterruptedException e) {}

							if (fadeIn) alpha += 0.1;
							else alpha -= 0.1;

							view.post(new Runnable(){

									@Override
									public void run()
									{
										view.setAlpha(alpha);
									}
								});
						}
					} finally {
						view.post(new Runnable(){

								@Override
								public void run()
								{
									if (fadeIn) view.setEnabled(true);
									else {
										view.setVisibility(View.GONE);
									}
								}
							});
					}
				}
			}).start();
	}
}
