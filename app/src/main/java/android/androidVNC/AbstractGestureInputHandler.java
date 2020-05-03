/**
 * Copyright (C) 2009 Michael A. MacDonald
 */
package android.androidVNC;

import android.view.GestureDetector;
import android.view.MotionEvent;

import com.antlersoft.android.bc.BCFactory;
import com.antlersoft.android.bc.IBCScaleGestureDetector;
import com.antlersoft.android.bc.OnScaleGestureListener;

/**
 * An AbstractInputHandler that uses GestureDetector to detect standard gestures in touch events
 * 
 * @author Michael A. MacDonald
 */
abstract class AbstractGestureInputHandler extends GestureDetector.SimpleOnGestureListener implements AbstractInputHandler, OnScaleGestureListener {
	protected GestureDetector gestures;
	protected IBCScaleGestureDetector scaleGestures;
	private VncCanvasActivity activity;
	
	float xInitialFocus;
	float yInitialFocus;
	boolean inScaling;
	
	private static final String TAG = "AbstractGestureInputHandler";
	
	AbstractGestureInputHandler(VncCanvasActivity c)
	{
		activity = c;
		gestures=BCFactory.getInstance().getBCGestureDetector().createGestureDetector(c, this);
		gestures.setOnDoubleTapListener(this);
		scaleGestures=BCFactory.getInstance().getScaleGestureDetector(c, this);
	}

	@Override
	public boolean onTouchEvent(MotionEvent evt) {
		scaleGestures.onTouchEvent(evt);
		return gestures.onTouchEvent(evt);
	}

	/* (non-Javadoc)
	 * @see com.antlersoft.android.bc.OnScaleGestureListener#onScale(com.antlersoft.android.bc.IBCScaleGestureDetector)
	 */
	@Override
	public boolean onScale(IBCScaleGestureDetector detector) {
		boolean consumed = true;
		//if (detector.)
		//Log.i(TAG,"Focus("+detector.getFocusX()+","+detector.getFocusY()+") scaleFactor = "+detector.getScaleFactor());
		// Calculate focus shift
		float fx = detector.getFocusX();
		float fy = detector.getFocusY(); 
		double xfs = fx - xInitialFocus;
		double yfs = fy - yInitialFocus;
		double fs = Math.sqrt(xfs * xfs + yfs * yfs);
		if (Math.abs(1.0 - detector.getScaleFactor())<0.02)
			consumed = false;
		if (fs * 2< Math.abs(detector.getCurrentSpan() - detector.getPreviousSpan()))
		{
			inScaling = true;
			if (consumed)
			{
				//Log.i(TAG,"Adjust scaling "+detector.getScaleFactor());
				if (activity.vncCanvas != null && activity.vncCanvas.scaling != null)
					activity.vncCanvas.scaling.adjust(activity, detector.getScaleFactor(), fx, fy);
			}
		}
		return consumed;
	}

	/* (non-Javadoc)
	 * @see com.antlersoft.android.bc.OnScaleGestureListener#onScaleBegin(com.antlersoft.android.bc.IBCScaleGestureDetector)
	 */
	@Override
	public boolean onScaleBegin(IBCScaleGestureDetector detector) {
		xInitialFocus = detector.getFocusX();
		yInitialFocus = detector.getFocusY();
		inScaling = false;
		//Log.i(TAG,"scale begin ("+xInitialFocus+","+yInitialFocus+")");
		return true;
	}

	/* (non-Javadoc)
	 * @see com.antlersoft.android.bc.OnScaleGestureListener#onScaleEnd(com.antlersoft.android.bc.IBCScaleGestureDetector)
	 */
	@Override
	public void onScaleEnd(IBCScaleGestureDetector detector) {
		//Log.i(TAG,"scale end");
		inScaling = false;
	}
}
