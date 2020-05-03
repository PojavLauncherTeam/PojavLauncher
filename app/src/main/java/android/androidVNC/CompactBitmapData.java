/**
 * Copyright (C) 2009 Michael A. MacDonald
 */
package android.androidVNC;

import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * @author Michael A. MacDonald
 *
 */
class CompactBitmapData extends AbstractBitmapData {

	class CompactBitmapDrawable extends AbstractBitmapDrawable
	{
		CompactBitmapDrawable()
		{
			super(CompactBitmapData.this);
		}
		/* (non-Javadoc)
		 * @see android.graphics.drawable.DrawableContainer#draw(android.graphics.Canvas)
		 */
		@Override
		public void draw(Canvas canvas) {
			draw(canvas, 0, 0);
		}
	}
	
	CompactBitmapData(RfbProto rfb, VncCanvas c)
	{
		super(rfb,c);
		bitmapwidth=framebufferwidth;
		bitmapheight=framebufferheight;
		mbitmap = Bitmap.createBitmap(rfb.framebufferWidth, rfb.framebufferHeight, Bitmap.Config.RGB_565);
		memGraphics = new Canvas(mbitmap);
		bitmapPixels = new int[rfb.framebufferWidth * rfb.framebufferHeight];
	}

	@Override
	void writeFullUpdateRequest(boolean incremental) throws IOException {
		rfb.writeFramebufferUpdateRequest(0, 0, framebufferwidth, framebufferheight, incremental);
	}

	@Override
	boolean validDraw(int x, int y, int w, int h) {
		return true;
	}

	@Override
	int offset(int x, int y) {
		return y * bitmapwidth + x;
	}

	/* (non-Javadoc)
	 * @see android.androidVNC.AbstractBitmapData#createDrawable()
	 */
	@Override
	AbstractBitmapDrawable createDrawable() {
		return new CompactBitmapDrawable();
	}

	/* (non-Javadoc)
	 * @see android.androidVNC.AbstractBitmapData#updateBitmap(int, int, int, int)
	 */
	@Override
	void updateBitmap(int x, int y, int w, int h) {
		mbitmap.setPixels(bitmapPixels, offset(x,y), bitmapwidth, x, y, w, h);
	}

	/* (non-Javadoc)
	 * @see android.androidVNC.AbstractBitmapData#copyRect(android.graphics.Rect, android.graphics.Rect, android.graphics.Paint)
	 */
	@Override
	void copyRect(Rect src, Rect dest, Paint paint) {
		memGraphics.drawBitmap(mbitmap, src, dest, paint);
	}

	/* (non-Javadoc)
	 * @see android.androidVNC.AbstractBitmapData#drawRect(int, int, int, int, android.graphics.Paint)
	 */
	@Override
	void drawRect(int x, int y, int w, int h, Paint paint) {
		memGraphics.drawRect(x, y, x + w, y + h, paint);
	}

	/* (non-Javadoc)
	 * @see android.androidVNC.AbstractBitmapData#scrollChanged(int, int)
	 */
	@Override
	void scrollChanged(int newx, int newy) {
		// Don't need to do anything here
	}

	/* (non-Javadoc)
	 * @see android.androidVNC.AbstractBitmapData#syncScroll()
	 */
	@Override
	void syncScroll() {
		// Don't need anything here either
		
	}
}
