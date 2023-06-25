package git.artdeell.forgeinstaller;

import javax.swing.*;
import java.awt.*;

public class DialogFilter implements ComponentFilter{
    @Override
    public boolean checkComponent(Component component) {
        return component instanceof JOptionPane;
    }
}
