/**
 * Copyright (C) 2009 Michael A. MacDonald
 */
package android.androidVNC;

import android.widget.*;
import net.kdt.pojavlaunch.*;


/**
 * @author Michael A. MacDonald
 * 
 * A scaling mode for the VncCanvas; based on ImageView.ScaleType
 */
abstract class AbstractScaling {
	private static final int scaleModeIds[] = { R.id.itemFitToScreen, R.id.itemOneToOne, R.id.itemZoomable };
	
	private static AbstractScaling[] scalings;

	static AbstractScaling getById(int id)
	{
		if ( scalings==null)
		{
			scalings=new AbstractScaling[scaleModeIds.length];
		}
		for ( int i=0; i<scaleModeIds.length; ++i)
		{
			if ( scaleModeIds[i]==id)
			{
				if ( scalings[i]==null)
				{
					switch ( id )
					{
					case R.id.itemFitToScreen :
						scalings[i]=new FitToScreenScaling();
						break;
					case R.id.itemOneToOne :
						scalings[i]=new OneToOneScaling();
						break;
					case R.id.itemZoomable :
						scalings[i]=new ZoomScaling();
						break;
					}
				}
				return scalings[i];
			}
		}
		throw new IllegalArgumentException("Unknown scaling id " + id);
	}
	
	float getScale() { return 1; }
	
	void zoomIn(VncCanvasActivity activity) {}
	void zoomOut(VncCanvasActivity activity) {}
	
	static AbstractScaling getByScaleType(ImageView.ScaleType scaleType)
	{
		for (int i : scaleModeIds)
		{
			AbstractScaling s = getById(i);
			if (s.scaleType==scaleType)
				return s;
		}
		throw new IllegalArgumentException("Unsupported scale type: "+ scaleType.toString());
	}
	
	private int id;
	protected ImageView.ScaleType scaleType;
	
	protected AbstractScaling(int id, ImageView.ScaleType scaleType)
	{
		this.id = id;
		this.scaleType = scaleType;
	}
	
	/**
	 * 
	 * @return Id corresponding to menu item that sets this scale type
	 */
	int getId()
	{
		return id;
	}

	/**
	 * Sets the activity's scale type to the scaling
	 * @param activity
	 */
	void setScaleTypeForActivity(VncCanvasActivity activity)
	{
		activity.zoomer.hide();
		activity.vncCanvas.scaling = this;
		activity.vncCanvas.setScaleType(scaleType);
		activity.getConnection().setScaleMode(scaleType);
		if (activity.inputHandler == null || ! isValidInputMode(activity.getModeIdFromHandler(activity.inputHandler))) {
			activity.inputHandler=activity.getInputHandlerById(getDefaultHandlerId());
			activity.getConnection().setInputMode(activity.inputHandler.getName());
		}
		activity.getConnection().Gen_update(activity.database.getWritableDatabase());
		activity.updateInputMenu();
	}
	
	abstract int getDefaultHandlerId();
	
	/**
	 * True if this scale type allows panning of the image
	 * @return
	 */
	abstract boolean isAbleToPan();
	
	/**
	 * True if the listed input mode is valid for this scaling mode
	 * @param mode Id of the input mode
	 * @return True if the input mode is compatible with the scaling mode
	 */
	abstract boolean isValidInputMode(int mode);
	
	/**
	 * Change the scaling and focus dynamically, as from a detected scale gesture
	 * @param activity Activity containing to canvas to scale
	 * @param scaleFactor Factor by which to adjust scaling
	 * @param fx Focus X of center of scale change
	 * @param fy Focus Y of center of scale change
	 */
	void adjust(VncCanvasActivity activity, float scaleFactor, float fx, float fy)
	{
	
	}
}
