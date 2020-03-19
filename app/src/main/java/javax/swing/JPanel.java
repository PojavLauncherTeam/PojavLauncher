package javax.swing;

import java.awt.*;
import javax.accessibility.*;

public class JPanel extends JComponent implements Accessible
{

	@Override
	public AccessibleContext getAccessibleContext()
	{
		// TODO: Implement this method
		return new AccessibleContext(){};
	}

	public void setPreferredSize(Dimension d) {
	}
}
