package git.artdeell.forgeinstaller;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.WindowEvent;
import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

public class Agent implements AWTEventListener {
    private boolean forgeWindowHandled = false;
    private final boolean suppressProfileCreation;

    public Agent(boolean ps) {
        this.suppressProfileCreation = ps;
    }

    @Override
    public void eventDispatched(AWTEvent event) {
        WindowEvent windowEvent = (WindowEvent) event;
        Window window = windowEvent.getWindow();
        if(windowEvent.getID() == WindowEvent.WINDOW_OPENED) {
            if(!forgeWindowHandled) { // false at startup, so we will handle the first window as the Forge one
                handleForgeWindow(window);
                forgeWindowHandled = true;
            }else if(window instanceof JDialog) { // expecting a new dialog
                handleDialog(window);
            }
        }
    }

    public void handleForgeWindow(Window window) {
        List<Component> components = new ArrayList<>();
        insertAllComponents(components, window, new MainWindowFilter());
        AbstractButton okButton = null;
        for(Component component : components) {
            if(component instanceof AbstractButton) {
                AbstractButton abstractButton = (AbstractButton) component;
                switch(abstractButton.getText()) {
                    case "OK":
                        okButton = abstractButton; // store the button, so we can press it after processing other stuff
                        break;
                    case "Install client":
                        abstractButton.doClick(); // It should be the default, but let's make sure
                }

            }
        }
        if(okButton == null) {
            System.out.println("Failed to set all the UI components.");
            System.exit(17);
        }else{
            ProfileFixer.storeProfile();
            EventQueue.invokeLater(okButton::doClick); // do that after forge actually builds its window, otherwise we set the path too fast
        }
    }

    public void handleDialog(Window window) {
        List<Component> components = new ArrayList<>();
        insertAllComponents(components, window, new DialogFilter()); // ensure that it's a JOptionPane dialog
        if(components.size() == 1) {
            // another common trait of them - they only have one option pane in them,
            // so we can discard the rest of the dialog structure
            JOptionPane optionPane = (JOptionPane) components.get(0);
            if(optionPane.getMessageType() == JOptionPane.INFORMATION_MESSAGE) { // forge doesn't emit information messages for other reasons yet
                System.out.println("The install was successful!");
                ProfileFixer.reinsertProfile(suppressProfileCreation);
                System.exit(0); // again, forge doesn't call exit for some reason, so we do that ourselves here
            }
        }
    }

    public void insertAllComponents(List<Component> components, Container parent, ComponentFilter filter) {
        int componentCount = parent.getComponentCount();
        for(int i = 0; i < componentCount; i++) {
            Component component = parent.getComponent(i);
            if(filter.checkComponent(component)) components.add(component);
            if(component instanceof Container) {
                insertAllComponents(components, (Container) component, filter);
            }
        }
    }

    public static void premain(String args, Instrumentation inst) {
        Toolkit.getDefaultToolkit()
                .addAWTEventListener(new Agent(!"NPS".equals(args)), // No Profile Suppression
                        AWTEvent.WINDOW_EVENT_MASK);
    }
}
