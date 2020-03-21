package java.awt;

import android.graphics.*;
import java.awt.image.*;

import android.graphics.Canvas;

public class Graphics {
	private Paint androidPaint;
    private Canvas androidCanvas;
    private BufferedImage bufImage;

    public Graphics(BufferedImage bufImage) {
        this.bufImage = bufImage;
        this.androidCanvas = new Canvas(bufImage.getAndroidBitmap());
		this.androidPaint = new Paint();
    }

    public void setColor(Color color) {
		androidPaint.setColor(color.hashCode());
    }

    public void fillRect(int x, int y, int width, int height) {
		this.androidCanvas.drawRect(x, y, x + width, y + height, androidPaint);
    }

    public void drawString(String s, int x, int y) {
		this.androidCanvas.drawText(s, x, y, androidPaint);
    }

    public void dispose() {
    }

    public boolean drawImage(Image image, int x, int y, ImageObserver observer) {
		return false;
	}
	
	public boolean drawImage(Image image, ImageObserver observer) {
		return drawImage(image, 0, 0, observer);
	}
	
	public boolean drawImage(Image image, int x, int y, int width, int height, ImageObserver observer) {
        return drawImage(image, x ,y ,x + width, y + height, observer);
    }
	
	public boolean drawImage(Image image, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
		if (image instanceof BufferedImage) {
			((BufferedImage) image).getAndroidBitmap().reconfigure(dx2 - dx1, dy2 - dy1, Bitmap.Config.ARGB_8888);
            this.androidCanvas.drawBitmap(((BufferedImage) image).getAndroidBitmap(), (float) dx1, (float) dy1, null);
			return true;
        }
        return false;
	}
}

