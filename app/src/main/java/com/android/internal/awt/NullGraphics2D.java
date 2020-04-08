package com.android.internal.awt;

import java.awt.*;
import java.text.*;
import java.awt.image.renderable.*;
import java.awt.font.*;
import java.awt.image.*;
import java.awt.RenderingHints.*;
import java.util.*;
import java.awt.geom.*;

public class NullGraphics2D extends Graphics2D
{

	@Override
	public void clearRect(int x, int y, int width, int height)
	{
		// TODO: Implement this method
	}

	@Override
	public void clipRect(int x, int y, int width, int height)
	{
		// TODO: Implement this method
	}

	@Override
	public void copyArea(int sx, int sy, int width, int height, int dx, int dy)
	{
		// TODO: Implement this method
	}

	@Override
	public Graphics create()
	{
		return this;
	}

	@Override
	public void dispose()
	{
		// TODO: Implement this method
	}

	@Override
	public void drawArc(int x, int y, int width, int height, int sa, int ea)
	{
		// TODO: Implement this method
	}

	@Override
	public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer)
	{
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean drawImage(Image img, int x, int y, ImageObserver observer)
	{
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer)
	{
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer)
	{
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer)
	{
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer)
	{
		// TODO: Implement this method
		return false;
	}

	@Override
	public void drawLine(int x1, int y1, int x2, int y2)
	{
		// TODO: Implement this method
	}

	@Override
	public void drawOval(int x, int y, int width, int height)
	{
		// TODO: Implement this method
	}

	@Override
	public void drawPolygon(int[] xpoints, int[] ypoints, int npoints)
	{
		// TODO: Implement this method
	}

	@Override
	public void drawPolyline(int[] xpoints, int[] ypoints, int npoints)
	{
		// TODO: Implement this method
	}

	@Override
	public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight)
	{
		// TODO: Implement this method
	}

	@Override
	public void fillArc(int x, int y, int width, int height, int sa, int ea)
	{
		// TODO: Implement this method
	}

	@Override
	public void fillOval(int x, int y, int width, int height)
	{
		// TODO: Implement this method
	}

	@Override
	public void fillPolygon(int[] xpoints, int[] ypoints, int npoints)
	{
		// TODO: Implement this method
	}

	@Override
	public void fillRect(int x, int y, int width, int height)
	{
		// TODO: Implement this method
	}

	@Override
	public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight)
	{
		// TODO: Implement this method
	}

	@Override
	public Shape getClip()
	{
		// TODO: Implement this method
		return null;
	}

	@Override
	public Rectangle getClipBounds()
	{
		// TODO: Implement this method
		return null;
	}

	@Override
	public Color getColor()
	{
		return Color.BLACK;
	}

	@Override
	public Font getFont()
	{
		// TODO: Implement this method
		return Font.decode(null);
	}

	@Override
	public FontMetrics getFontMetrics(Font font)
	{
		// TODO: Implement this method
		return new FontMetrics(getFont()){};
	}

	@Override
	public void setClip(int x, int y, int width, int height)
	{
		// TODO: Implement this method
	}

	@Override
	public void setClip(Shape clip)
	{
		// TODO: Implement this method
	}

	@Override
	public void setColor(Color c)
	{
		// TODO: Implement this method
	}

	@Override
	public void setFont(Font font)
	{
		// TODO: Implement this method
	}

	@Override
	public void setPaintMode()
	{
		// TODO: Implement this method
	}

	@Override
	public void setXORMode(Color color)
	{
		// TODO: Implement this method
	}

	@Override
	public void addRenderingHints(Map<?, ?> hints)
	{
		// TODO: Implement this method
	}

	@Override
	public void clip(Shape s)
	{
		// TODO: Implement this method
	}

	@Override
	public void draw(Shape s)
	{
		// TODO: Implement this method
	}

	@Override
	public void drawGlyphVector(GlyphVector g, float x, float y)
	{
		// TODO: Implement this method
	}

	@Override
	public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y)
	{
		// TODO: Implement this method
	}

	@Override
	public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs)
	{
		// TODO: Implement this method
		return false;
	}

	@Override
	public void drawRenderableImage(RenderableImage img, AffineTransform xform)
	{
		// TODO: Implement this method
	}

	@Override
	public void drawRenderedImage(RenderedImage img, AffineTransform xform)
	{
		// TODO: Implement this method
	}

	@Override
	public void drawString(AttributedCharacterIterator iterator, float x, float y)
	{
		// TODO: Implement this method
	}

	@Override
	public void drawString(AttributedCharacterIterator iterator, int x, int y)
	{
		// TODO: Implement this method
	}

	@Override
	public void drawString(String s, float x, float y)
	{
		// TODO: Implement this method
	}

	@Override
	public void drawString(String str, int x, int y)
	{
		// TODO: Implement this method
	}

	@Override
	public void fill(Shape s)
	{
		// TODO: Implement this method
	}

	@Override
	public Color getBackground()
	{
		// TODO: Implement this method
		return null;
	}

	@Override
	public Composite getComposite()
	{
		// TODO: Implement this method
		return null;
	}

	@Override
	public GraphicsConfiguration getDeviceConfiguration()
	{
		// TODO: Implement this method
		return null;
	}

	@Override
	public FontRenderContext getFontRenderContext()
	{
		// TODO: Implement this method
		return null;
	}

	@Override
	public Paint getPaint()
	{
		// TODO: Implement this method
		return null;
	}

	@Override
	public Object getRenderingHint(RenderingHints.Key key)
	{
		// TODO: Implement this method
		return null;
	}

	@Override
	public RenderingHints getRenderingHints()
	{
		// TODO: Implement this method
		return null;
	}

	@Override
	public Stroke getStroke()
	{
		// TODO: Implement this method
		return null;
	}

	@Override
	public AffineTransform getTransform()
	{
		// TODO: Implement this method
		return null;
	}

	@Override
	public boolean hit(Rectangle rect, Shape s, boolean onStroke)
	{
		// TODO: Implement this method
		return false;
	}

	@Override
	public void rotate(double theta)
	{
		// TODO: Implement this method
	}

	@Override
	public void rotate(double theta, double x, double y)
	{
		// TODO: Implement this method
	}

	@Override
	public void scale(double sx, double sy)
	{
		// TODO: Implement this method
	}

	@Override
	public void setBackground(Color color)
	{
		// TODO: Implement this method
	}

	@Override
	public void setComposite(Composite comp)
	{
		// TODO: Implement this method
	}

	@Override
	public void setPaint(Paint paint)
	{
		// TODO: Implement this method
	}

	@Override
	public void setRenderingHint(RenderingHints.Key key, Object value)
	{
		// TODO: Implement this method
	}

	@Override
	public void setRenderingHints(Map<?, ?> hints)
	{
		// TODO: Implement this method
	}

	@Override
	public void setStroke(Stroke s)
	{
		// TODO: Implement this method
	}

	@Override
	public void setTransform(AffineTransform Tx)
	{
		// TODO: Implement this method
	}

	@Override
	public void shear(double shx, double shy)
	{
		// TODO: Implement this method
	}

	@Override
	public void transform(AffineTransform Tx)
	{
		// TODO: Implement this method
	}

	@Override
	public void translate(double tx, double ty)
	{
		// TODO: Implement this method
	}

	@Override
	public void translate(int x, int y)
	{
		// TODO: Implement this method
	}
	
}
