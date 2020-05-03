/**
 * Copyright (C) 2009 Michael A. MacDonald
 */
package android.androidVNC;

import java.io.IOException;

import com.antlersoft.android.drawing.OverlappingCopy;
import com.antlersoft.android.drawing.RectList;
import com.antlersoft.util.ObjectPool;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * @author Michael A. MacDonald
 *
 */
class LargeBitmapData extends AbstractBitmapData {
	
	/**
	 * Multiply this times total number of pixels to get estimate of process size with all buffers plus
	 * safety factor
	 */
	static final int CAPACITY_MULTIPLIER = 21;
	
	int xoffset;
	int yoffset;
	int scrolledToX;
	int scrolledToY;
	private Rect bitmapRect;
	private Paint defaultPaint;
	private RectList invalidList;
	private RectList pendingList;
	
	/**
	 * Pool of temporary rectangle objects.  Need to synchronize externally access from
	 * multiple threads.
	 */
	private static ObjectPool<Rect> rectPool = new ObjectPool<Rect>() {

		/* (non-Javadoc)
		 * @see com.antlersoft.util.ObjectPool#itemForPool()
		 */
		@Override
		protected Rect itemForPool() {
			return new Rect();
		}		
	};
	
	class LargeBitmapDrawable extends AbstractBitmapDrawable
	{
		LargeBitmapDrawable()
		{
			super(LargeBitmapData.this);
		}
		/* (non-Javadoc)
		 * @see android.graphics.drawable.DrawableContainer#draw(android.graphics.Canvas)
		 */
		@Override
		public void draw(Canvas canvas) {
			//android.util.Log.i("LBM", "Drawing "+xoffset+" "+yoffset);
			int xoff, yoff;
			synchronized ( LargeBitmapData.this )
			{
				xoff=xoffset;
				yoff=yoffset;
			}
			draw(canvas, xoff, yoff);
		}
	}
	
	/**
	 * 
	 * @param p Protocol implementation
	 * @param c View that will display screen
	 * @param displayWidth
	 * @param displayHeight
	 * @param capacity Max process heap size in bytes
	 */
	LargeBitmapData(RfbProto p, VncCanvas c, int displayWidth, int displayHeight, int capacity)
	{
		super(p,c);
		double scaleMultiplier = Math.sqrt((double)(capacity * 1024 * 1024) / (double)(CAPACITY_MULTIPLIER * framebufferwidth * framebufferheight));
		if (scaleMultiplier > 1)
			scaleMultiplier = 1;
		bitmapwidth=(int)((double)framebufferwidth * scaleMultiplier);
		if (bitmapwidth < displayWidth)
			bitmapwidth = displayWidth;
		bitmapheight=(int)((double)framebufferheight * scaleMultiplier);
		if (bitmapheight < displayHeight)
			bitmapheight = displayHeight;
		android.util.Log.i("LBM", "bitmapsize = ("+bitmapwidth+","+bitmapheight+")");
		mbitmap = Bitmap.createBitmap(bitmapwidth, bitmapheight, Bitmap.Config.RGB_565);
		memGraphics = new Canvas(mbitmap);
		bitmapPixels = new int[bitmapwidth * bitmapheight];
		invalidList = new RectList(rectPool);
		pendingList = new RectList(rectPool);
		bitmapRect=new Rect(0,0,bitmapwidth,bitmapheight);
		defaultPaint = new Paint();
	}
	
	@Override
	AbstractBitmapDrawable createDrawable()
	{
		return new LargeBitmapDrawable();
	}
	
	/* (non-Javadoc)
	 * @see android.androidVNC.AbstractBitmapData#copyRect(android.graphics.Rect, android.graphics.Rect, android.graphics.Paint)
	 */
	@Override
	void copyRect(Rect src, Rect dest, Paint paint) {
		// TODO copy rect working?
		throw new RuntimeException( "copyrect Not implemented");
	}

	/* (non-Javadoc)
	 * @see android.androidVNC.AbstractBitmapData#drawRect(int, int, int, int, android.graphics.Paint)
	 */
	@Override
	void drawRect(int x, int y, int w, int h, Paint paint) {
		x-=xoffset;
		y-=yoffset;
		memGraphics.drawRect(x, y, x+w, y+h, paint);
	}

	/* (non-Javadoc)
	 * @see android.androidVNC.AbstractBitmapData#offset(int, int)
	 */
	@Override
	int offset(int x, int y) {
		return (y - yoffset) * bitmapwidth + x - xoffset;
	}

	/* (non-Javadoc)
	 * @see android.androidVNC.AbstractBitmapData#scrollChanged(int, int)
	 */
	@Override
	synchronized void scrollChanged(int newx, int newy) {
		//android.util.Log.i("LBM","scroll "+newx+" "+newy);
		int newScrolledToX = scrolledToX;
		int newScrolledToY = scrolledToY;
		int visibleWidth = vncCanvas.getVisibleWidth();
		int visibleHeight = vncCanvas.getVisibleHeight();
		if (newx - xoffset < 0 )
		{
			newScrolledToX = newx + visibleWidth / 2 - bitmapwidth / 2;
			if (newScrolledToX < 0)
				newScrolledToX = 0;
		}
		else if (newx - xoffset + visibleWidth > bitmapwidth)
		{
			newScrolledToX = newx + visibleWidth / 2 - bitmapwidth / 2;
			if (newScrolledToX + bitmapwidth > framebufferwidth)
				newScrolledToX = framebufferwidth - bitmapwidth;
		}
		if (newy - yoffset < 0 )
		{
			newScrolledToY = newy + visibleHeight / 2 - bitmapheight / 2;
			if (newScrolledToY < 0)
				newScrolledToY = 0;
		}
		else if (newy - yoffset + visibleHeight > bitmapheight)
		{
			newScrolledToY = newy + visibleHeight / 2 - bitmapheight / 2;
			if (newScrolledToY + bitmapheight > framebufferheight)
				newScrolledToY = framebufferheight - bitmapheight;
		}
		if (newScrolledToX != scrolledToX || newScrolledToY != scrolledToY)
		{
			scrolledToX = newScrolledToX;
			scrolledToY = newScrolledToY;
			if ( waitingForInput)
				syncScroll();
		}
	}

	/* (non-Javadoc)
	 * @see android.androidVNC.AbstractBitmapData#updateBitmap(int, int, int, int)
	 */
	@Override
	void updateBitmap(int x, int y, int w, int h) {
		mbitmap.setPixels(bitmapPixels, offset(x,y), bitmapwidth, x-xoffset, y-yoffset, w, h);
	}

	/* (non-Javadoc)
	 * @see android.androidVNC.AbstractBitmapData#validDraw(int, int, int, int)
	 */
	@Override
	synchronized boolean validDraw(int x, int y, int w, int h) {
		//android.util.Log.i("LBM", "Validate Drawing "+x+" "+y+" "+w+" "+h+" "+xoffset+" "+yoffset+" "+(x-xoffset>=0 && x-xoffset+w<=bitmapwidth && y-yoffset>=0 && y-yoffset+h<=bitmapheight));
		boolean result = x-xoffset>=0 && x-xoffset+w<=bitmapwidth && y-yoffset>=0 && y-yoffset+h<=bitmapheight;
		ObjectPool.Entry<Rect> entry = rectPool.reserve();
		Rect r = entry.get();
		r.set(x, y, x+w, y+h);
		pendingList.subtract(r);
		if ( ! result)
		{
			invalidList.add(r);
		}
		else
			invalidList.subtract(r);
		rectPool.release(entry);
		return result;
	}

	/* (non-Javadoc)
	 * @see android.androidVNC.AbstractBitmapData#writeFullUpdateRequest(boolean)
	 */
	@Override
	synchronized void writeFullUpdateRequest(boolean incremental) throws IOException {
		if (! incremental) {
			ObjectPool.Entry<Rect> entry = rectPool.reserve();
			Rect r = entry.get();
			r.left=xoffset;
			r.top=yoffset;
			r.right=xoffset + bitmapwidth;
			r.bottom=yoffset + bitmapheight;
			pendingList.add(r);
			invalidList.add(r);
			rectPool.release(entry);
		}
		rfb.writeFramebufferUpdateRequest(xoffset, yoffset, bitmapwidth, bitmapheight, incremental);
	}

	/* (non-Javadoc)
	 * @see android.androidVNC.AbstractBitmapData#syncScroll()
	 */
	@Override
	synchronized void syncScroll() {
		
		int deltaX = xoffset - scrolledToX;
		int deltaY = yoffset - scrolledToY;
		xoffset=scrolledToX;
		yoffset=scrolledToY;
		bitmapRect.top=scrolledToY;
		bitmapRect.bottom=scrolledToY+bitmapheight;
		bitmapRect.left=scrolledToX;
		bitmapRect.right=scrolledToX+bitmapwidth;
		invalidList.intersect(bitmapRect);
		if ( deltaX != 0 || deltaY != 0)
		{
			boolean didOverlapping = false;
			if (Math.abs(deltaX) < bitmapwidth && Math.abs(deltaY) < bitmapheight) {
				ObjectPool.Entry<Rect> sourceEntry = rectPool.reserve();
				ObjectPool.Entry<Rect> addedEntry = rectPool.reserve();
				try
				{
					Rect added = addedEntry.get();
					Rect sourceRect = sourceEntry.get();
					sourceRect.set(deltaX<0 ? -deltaX : 0,
							deltaY<0 ? -deltaY : 0,
							deltaX<0 ? bitmapwidth : bitmapwidth - deltaX,
							deltaY < 0 ? bitmapheight : bitmapheight - deltaY);
					if (! invalidList.testIntersect(sourceRect)) {
						didOverlapping = true;
						OverlappingCopy.Copy(mbitmap, memGraphics, defaultPaint, sourceRect, deltaX + sourceRect.left, deltaY + sourceRect.top, rectPool);
						// Write request for side pixels
						if (deltaX != 0) {
							added.left = deltaX < 0 ? bitmapRect.right + deltaX : bitmapRect.left;
							added.right = added.left + Math.abs(deltaX);
							added.top = bitmapRect.top;
							added.bottom = bitmapRect.bottom;
							invalidList.add(added);
						}
						if (deltaY != 0) {
							added.left = deltaX < 0 ? bitmapRect.left : bitmapRect.left + deltaX;
							added.top = deltaY < 0 ? bitmapRect.bottom + deltaY : bitmapRect.top;
							added.right = added.left + bitmapwidth - Math.abs(deltaX);
							added.bottom = added.top + Math.abs(deltaY);
							invalidList.add(added);
						}
					}
				}
				finally {
					rectPool.release(addedEntry);
					rectPool.release(sourceEntry);
				}
			}
			if (! didOverlapping)
			{
				try
				{
					//android.util.Log.i("LBM","update req "+xoffset+" "+yoffset);
					mbitmap.eraseColor(Color.GREEN);
					writeFullUpdateRequest(false);
				}
				catch ( IOException ioe)
				{
					// TODO log this
				}
			}
		}
		int size = pendingList.getSize();
		for (int i=0; i<size; i++) {
			invalidList.subtract(pendingList.get(i));
		}
		size = invalidList.getSize();
		for (int i=0; i<size; i++) {
			Rect invalidRect = invalidList.get(i);
			try
			{
				rfb.writeFramebufferUpdateRequest(invalidRect.left, invalidRect.top, invalidRect.right-invalidRect.left, invalidRect.bottom-invalidRect.top, false);
				pendingList.add(invalidRect);
			}
			catch (IOException ioe)
			{
				//TODO Log this
			}
		}
		waitingForInput=true;
		//android.util.Log.i("LBM", "pending "+pendingList.toString() + "invalid "+invalidList.toString());
	}
}
