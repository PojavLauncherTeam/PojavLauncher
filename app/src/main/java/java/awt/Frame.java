package java.awt;

import java.awt.*;
import java.util.*;
import java.awt.event.*;

public class Frame extends Window {
	public Frame() {
		super();
    }
	
    public Frame(String title) {
		super(title);
    }
	
	public static Frame[] getFrames() {
        Window[] allWindows = Window.getWindows();

        List<Frame> frames = new ArrayList<Frame>();
        for (Window w : allWindows) {
            if (w instanceof Frame) {
                frames.add((Frame) w);
            }
        }

        return frames.toArray(new Frame[0]);
    }
	
	public void setLocationRelativeTo(Component comp) {
		//super.setLocationRelativeTo(comp);
    }

    public void addWindowListener(WindowListener listener) {
		//super.addWindowListener(lisener);
    }
}

