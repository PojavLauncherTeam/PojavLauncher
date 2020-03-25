package java.awt.image;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

public class BufferedImage extends Image implements RenderedImage {
    public static final int TYPE_INT_ARGB = 2;
    private Bitmap bitmap;
	private WritableRaster raster;

	private Bitmap mkBitmap(int width, int height, Config mode) {
		return Bitmap.createBitmap(width, height, mode);
	}
	
    public BufferedImage(Bitmap bitmap) {
		if (bitmap != null) {
			this.bitmap = bitmap;
		} else {
			mkBitmap(1, 1, Config.ARGB_8888);
		}
		init();
    }

    public BufferedImage(int width, int height, int imageType) {
		this.bitmap = mkBitmap(width, height, Config.ARGB_8888);
		/*
		if (imageType == TYPE_INT_ARGB) {
			this.bitmap = mkBitmap(width, height, Config.ARGB_8888);
		} else {
			this.bitmap = mkBitmap(width, height, Config.HARDWARE);
		}
		*/
		init();
    }
	
	public void init() {
		raster = new WritableRaster(this);
	}

    public int getWidth() {
        return this.bitmap.getWidth();
    }

    public int getHeight() {
        return this.bitmap.getHeight();
    }

    public int[] getRGB(int x, int y, int w, int h, int[] rgbArray, int offset, int scansize) {
        if (rgbArray == null) {
            rgbArray = new int[((scansize * h) + offset)];
        }
        this.bitmap.getPixels(rgbArray, offset, scansize, x, y, w, h);
        return rgbArray;
    }

    public Graphics getGraphics() {
        return new Graphics2D(this);
    }

    public Graphics2D createGraphics() {
        return new Graphics2D(this);
    }

    public void setRGB(int x, int y, int w, int h, int[] rgbArray, int offset, int scansize) {
        System.out.println("Setting RGB stub");
		
		if (rgbArray == null) {
			int size = (h - 1) * scansize + w;
			rgbArray = new int[size];
		}
		bitmap.setPixels(rgbArray, offset, scansize, x, y, w, h);
    }

    public WritableRaster getRaster() {
        return raster;
    }

    public Bitmap getAndroidBitmap() {
        return this.bitmap;
    }

    public int getType() {
        return TYPE_INT_ARGB;
    }
}

