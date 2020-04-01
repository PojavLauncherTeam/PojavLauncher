package java.awt;

import java.awt.image.*;
import net.kdt.pojavlaunch.*;

public class Canvas extends Component {
	private Graphics graphics;
	
	public Canvas() {
		graphics = new Graphics(new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB));
	}
	
    public void setPreferredSize(Dimension dim){
    }
	
	public boolean isDisplayable(){
		return true;
	}

    public Graphics getGraphics() {
        return graphics;
    }

    public int getWidth() {
        return 1280;
    }

    public int getHeight() {
        return 720;
    }
}

