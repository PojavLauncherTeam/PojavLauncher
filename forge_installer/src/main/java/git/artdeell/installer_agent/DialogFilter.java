package git.artdeell.installer_agent;

import javax.swing.*;
import java.awt.*;

public class DialogFilter implements ComponentFilter{
    @Override
    public boolean checkComponent(Component component) {
        return component instanceof JOptionPane
                || component instanceof JProgressBar;
    }
}
