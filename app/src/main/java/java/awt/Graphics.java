package java.awt;

import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import android.graphics.Canvas;
import android.graphics.Paint;

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
        if (image instanceof BufferedImage) {
            this.androidCanvas.drawBitmap(((BufferedImage) image).getAndroidBitmap(), (float) x, (float) y, null);
        }
        return true;
    }
}

