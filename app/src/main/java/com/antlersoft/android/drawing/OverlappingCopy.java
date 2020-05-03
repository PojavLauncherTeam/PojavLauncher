package com.antlersoft.android.drawing;

import com.antlersoft.util.ObjectPool;
import com.antlersoft.util.SafeObjectPool;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class OverlappingCopy
{
	private static SafeObjectPool<Rect> ocRectPool = new SafeObjectPool<Rect>() {
		@Override
		protected Rect itemForPool()
		{
			return new Rect();
		}
	};
	private static void transformRect(Rect source, Rect transformedSource, int deltaX, int deltaY)
	{
		transformedSource.set(deltaX < 0 ? source.right * -1 : source.left,
				deltaY < 0 ? source.bottom * -1 : source.top,
				deltaX < 0 ? source.left * -1 : source.right,
				deltaY < 0 ? source.top * -1 : source.bottom);		
	}
	private static void copyTransformedRect(Rect stepSourceRect, Rect stepDestRect, int deltaX, int deltaY, Bitmap data, Canvas bitmapBackedCanvas, Paint paint)
	{
		transformRect(stepSourceRect,stepSourceRect,deltaX,deltaY);
		stepDestRect.set(stepSourceRect);
		stepDestRect.offset(deltaX,deltaY);
		bitmapBackedCanvas.drawBitmap(data, stepSourceRect, stepDestRect, paint);
	}
	public static void Copy(Bitmap data, Canvas bitmapBackedCanvas, Paint paint, Rect source, int destX, int destY)
	{
		Copy(data,bitmapBackedCanvas,paint,source,destX,destY,ocRectPool);
	}
	public static void Copy(Bitmap data, Canvas bitmapBackedCanvas, Paint paint, Rect source, int destX, int destY, ObjectPool<Rect> rectPool)
	{
		//android.util.Log.i("LBM","Copy "+source.toString()+" to "+destX+","+destY);
		int deltaX = destX - source.left;
		int deltaY = destY - source.top;
		int absDeltaX = deltaX < 0 ? -deltaX : deltaX;
		int absDeltaY = deltaY < 0 ? -deltaY : deltaY;
		
		// Look for degenerate case
		if (absDeltaX == 0 && absDeltaY == 0)
			return;
		// Look for non-overlap case
		if (absDeltaX >= source.right - source.left || absDeltaY >= source.bottom - source.top)
		{
			// Non-overlapping copy
			ObjectPool.Entry<Rect> entry = rectPool.reserve();
			Rect dest = entry.get();
			dest.set(source.left + deltaX, source.top + deltaY, source.right + deltaX, source.bottom + deltaY);
			bitmapBackedCanvas.drawBitmap(data, source, dest, paint);
			rectPool.release(entry);
			return;
		}
		// Determine coordinate transform so that dest rectangle is always down and to the right.
		ObjectPool.Entry<Rect> transformedSourceEntry = rectPool.reserve();
		Rect transformedSource = transformedSourceEntry.get();
		transformRect(source,transformedSource,deltaX,deltaY);
		ObjectPool.Entry<Rect> transformedDestEntry = rectPool.reserve();
		Rect transformedDest = transformedDestEntry.get();
		transformedDest.set(transformedSource);
		transformedDest.offset(absDeltaX, absDeltaY);
		ObjectPool.Entry<Rect> intersectEntry = rectPool.reserve();
		Rect intersect = intersectEntry.get();
		intersect.setIntersect(transformedSource, transformedDest);
		
		boolean xStepDone = false;
		int xStepWidth;
		int yStepHeight;
		if (absDeltaX > absDeltaY)
		{
			xStepWidth = absDeltaX;
			yStepHeight = source.bottom - source.top - absDeltaY;
		}
		else
		{
			xStepWidth = source.right - source.left - absDeltaX;
			yStepHeight = absDeltaY;
		}
		
		ObjectPool.Entry<Rect> stepSourceEntry = rectPool.reserve();
		Rect stepSourceRect = stepSourceEntry.get();
		ObjectPool.Entry<Rect> stepDestEntry = rectPool.reserve();
		Rect stepDestRect = stepDestEntry.get();
		
		for (int xStep = 0; ! xStepDone; xStep++)
		{
			int stepRight = intersect.right - xStep * xStepWidth;
			int stepLeft = stepRight - xStepWidth;
			if (stepLeft <= intersect.left)
			{
				stepLeft = intersect.left;
				xStepDone = true;
			}
			boolean yStepDone = false;
			for (int yStep = 0; ! yStepDone; yStep++)
			{
				int stepBottom = intersect.bottom - yStep * yStepHeight;
				int stepTop = stepBottom - yStepHeight;
				if (stepTop <= intersect.top)
				{
					stepTop = intersect.top;
					yStepDone = true;
				}
				stepSourceRect.set(stepLeft,stepTop,stepRight,stepBottom);
				//android.util.Log.i("LBM","Copy transformed "+stepSourceRect.toString()+" "+deltaX+" "+deltaY);
				copyTransformedRect(stepSourceRect, stepDestRect, deltaX, deltaY, data, bitmapBackedCanvas, paint);
			}
		}
		if (absDeltaX>0)
		{
			// Copy left edge
			stepSourceRect.set(transformedSource.left,transformedSource.top,intersect.left,transformedSource.bottom);
			copyTransformedRect(stepSourceRect, stepDestRect, deltaX, deltaY, data, bitmapBackedCanvas, paint);
		}
		if (absDeltaY>0)
		{
			// Copy top excluding left edge
			stepSourceRect.set(intersect.left,transformedSource.top,transformedSource.right,intersect.top);
			copyTransformedRect(stepSourceRect, stepDestRect, deltaX, deltaY, data, bitmapBackedCanvas, paint);
		}
		
		rectPool.release(stepDestEntry);
		rectPool.release(stepSourceEntry);
		rectPool.release(intersectEntry);
		rectPool.release(transformedDestEntry);
		rectPool.release(transformedSourceEntry);
	}
}


