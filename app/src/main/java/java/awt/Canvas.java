package java.awt;

import java.awt.image.*;
import net.kdt.pojavlaunch.*;

public class Canvas extends Component {
	private Graphics mGraphics;
	private Dimension mSize;
	
	public Canvas() {
		mSize = new Dimension(1280, 720);
		mGraphics = new Graphics(new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB));
	}
	
    public void setPreferredSize(Dimension dim){
		mSize = dim;
    }
	
	public void setFocusTraversalKeysEnabled(boolean b) {
		System.out.println("java.awt.Canvas.setFocusTraversalKeysEnabled(" + b + ")");
	}
	
	public boolean isDisplayable(){
		return true;
	}

    public Graphics getGraphics() {
        return mGraphics;
    }

    public int getWidth() {
        return (int) mSize.getWidth();
    }

    public int getHeight() {
        return (int) mSize.getHeight();
    }
}

