/**
 * Copyright (C) 2009 Michael A. MacDonald
 */
package android.androidVNC;

import android.widget.ImageView.*;
import net.kdt.pojavlaunch.*;

/**
 * @author Michael A. MacDonald
 */
class OneToOneScaling extends AbstractScaling {

	/**
	 * @param id
	 * @param scaleType
	 */
	public OneToOneScaling() {
		super(R.id.itemOneToOne,ScaleType.CENTER);
	}

	/* (non-Javadoc)
	 * @see android.androidVNC.AbstractScaling#getDefaultHandlerId()
	 */
	@Override
	int getDefaultHandlerId() {
		return R.id.itemInputTouchPanTrackballMouse;
	}

	/* (non-Javadoc)
	 * @see android.androidVNC.AbstractScaling#isAbleToPan()
	 */
	@Override
	boolean isAbleToPan() {
		return true;
	}

	/* (non-Javadoc)
	 * @see android.androidVNC.AbstractScaling#isValidInputMode(int)
	 */
	@Override
	boolean isValidInputMode(int mode) {
		return mode != R.id.itemInputFitToScreen;
	}

	/* (non-Javadoc)
	 * @see android.androidVNC.AbstractScaling#setScaleTypeForActivity(android.androidVNC.VncCanvasActivity)
	 */
	@Override
	void setScaleTypeForActivity(VncCanvasActivity activity) {
		super.setScaleTypeForActivity(activity);
		activity.vncCanvas.scrollToAbsolute();
		activity.vncCanvas.pan(0,0);
	}

}
