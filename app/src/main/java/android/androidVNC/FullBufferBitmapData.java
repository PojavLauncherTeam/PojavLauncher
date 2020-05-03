/**
 * Copyright (c) 2010 Michael A. MacDonald
 */
package android.androidVNC;

import java.io.IOException;
import java.util.Arrays;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.widget.ImageView;

/**
 * @author Michael A. MacDonald
 *
 */
class FullBufferBitmapData extends AbstractBitmapData {

	int xoffset;
	int yoffset;
	
	/**
	 * @author Michael A. MacDonald
	 *
	 */
	class Drawable extends AbstractBitmapDrawable {

		/**
		 * @param data
		 */
		public Drawable(AbstractBitmapData data) {
			super(data);
			// TODO Auto-generated constructor stub
		}

		/* (non-Javadoc)
		 * @see android.graphics.drawable.DrawableContainer#draw(android.graphics.Canvas)
		 */
		@Override
		public void draw(Canvas canvas) {
			if (vncCanvas.getScaleType() == ImageView.ScaleType.FIT_CENTER)
			{
				canvas.drawBitmap(data.bitmapPixels, 0, data.framebufferwidth, xoffset, yoffset, framebufferwidth, framebufferheight, false, null);				
			}
			else
			{
				float scale = vncCanvas.getScale();
				int xo = xoffset < 0 ? 0 : xoffset;
				int yo = yoffset < 0 ? 0 : yoffset;
				/*
				if (scale == 1 || scale <= 0)
				{
				*/
					int drawWidth = vncCanvas.getVisibleWidth();
					if (drawWidth + xo > data.framebufferwidth)
						drawWidth = data.framebufferwidth - xo;
					int drawHeight = vncCanvas.getVisibleHeight();
					if (drawHeight + yo > data.framebufferheight)
						drawHeight = data.framebufferheight - yo;
					canvas.drawBitmap(data.bitmapPixels, offset(xo, yo), data.framebufferwidth, xo, yo, drawWidth, drawHeight, false, null);
				/*
				}
				else
				{
					int scalewidth = (int)(vncCanvas.getVisibleWidth() / scale + 1);
					if (scalewidth + xo > data.framebufferwidth)
						scalewidth = data.framebufferwidth - xo;
					int scaleheight = (int)(vncCanvas.getVisibleHeight() / scale + 1);
					if (scaleheight + yo > data.framebufferheight)
						scaleheight = data.framebufferheight - yo;
					canvas.drawBitmap(data.bitmapPixels, offset(xo, yo), data.framebufferwidth, xo, yo, scalewidth, scaleheight, false, null);				
				}
				*/
			}
			if(data.vncCanvas.connection.getUseLocalCursor())
			{
				setCursorRect(data.vncCanvas.mouseX, data.vncCanvas.mouseY);
				clipRect.set(cursorRect);
				if (canvas.clipRect(cursorRect))
				{
					drawCursor(canvas);
				}
			}
		}
	}

	/**
	 * Multiply this times total number of pixels to get estimate of process size with all buffers plus
	 * safety factor
	 */
	static final int CAPACITY_MULTIPLIER = 7;
	
	/**
	 * @param p
	 * @param c
	 */
	public FullBufferBitmapData(RfbProto p, VncCanvas c, int capacity) {
		super(p, c);
		framebufferwidth=rfb.framebufferWidth;
		framebufferheight=rfb.framebufferHeight;
		bitmapwidth=framebufferwidth;
		bitmapheight=framebufferheight;
		android.util.Log.i("FBBM", "bitmapsize = ("+bitmapwidth+","+bitmapheight+")");
		bitmapPixels = new int[framebufferwidth * framebufferheight];
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
	 * @see android.androidVNC.AbstractBitmapData#createDrawable()
	 */
	@Override
	AbstractBitmapDrawable createDrawable() {
		return new Drawable(this);
	}

	/* (non-Javadoc)
	 * @see android.androidVNC.AbstractBitmapData#drawRect(int, int, int, int, android.graphics.Paint)
	 */
	@Override
	void drawRect(int x, int y, int w, int h, Paint paint) {
		int color = paint.getColor();
		int offset = offset(x,y);
		if (w > 10)
		{
			for (int j = 0; j < h; j++, offset += framebufferwidth)
			{
				Arrays.fill(bitmapPixels, offset, offset + w, color);
			}
		}
		else
		{
			for (int j = 0; j < h; j++, offset += framebufferwidth - w)
			{
				for (int k = 0; k < w; k++, offset++)
				{
					bitmapPixels[offset] = color;
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see android.androidVNC.AbstractBitmapData#offset(int, int)
	 */
	@Override
	int offset(int x, int y) {
		return x + y * framebufferwidth;
	}

	/* (non-Javadoc)
	 * @see android.androidVNC.AbstractBitmapData#scrollChanged(int, int)
	 */
	@Override
	void scrollChanged(int newx, int newy) {
		xoffset = newx;
		yoffset = newy;
	}

	/* (non-Javadoc)
	 * @see android.androidVNC.AbstractBitmapData#syncScroll()
	 */
	@Override
	void syncScroll() {
		// Don't need to do anything here

	}

	/* (non-Javadoc)
	 * @see android.androidVNC.AbstractBitmapData#updateBitmap(int, int, int, int)
	 */
	@Override
	void updateBitmap(int x, int y, int w, int h) {
		// Don't need to do anything here

	}

	/* (non-Javadoc)
	 * @see android.androidVNC.AbstractBitmapData#validDraw(int, int, int, int)
	 */
	@Override
	boolean validDraw(int x, int y, int w, int h) {
		return true;
	}

	/* (non-Javadoc)
	 * @see android.androidVNC.AbstractBitmapData#writeFullUpdateRequest(boolean)
	 */
	@Override
	void writeFullUpdateRequest(boolean incremental) throws IOException {
		rfb.writeFramebufferUpdateRequest(0, 0, framebufferwidth, framebufferheight, incremental);
	}

}
