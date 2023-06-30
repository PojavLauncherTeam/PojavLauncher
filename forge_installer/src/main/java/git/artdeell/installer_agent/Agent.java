package git.artdeell.installer_agent;

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
import java.util.Timer;

import javax.swing.AbstractButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

public class Agent implements AWTEventListener {
    private boolean forgeWindowHandled = false;
    private final boolean suppressProfileCreation;
    private final boolean optiFineInstallation;
    private final Timer componentTimer = new Timer();

    public Agent(boolean nps, boolean of) {
        this.suppressProfileCreation = !nps;
        this.optiFineInstallation = of;
    }

    @Override
    public void eventDispatched(AWTEvent event) {
        WindowEvent windowEvent = (WindowEvent) event;
        Window window = windowEvent.getWindow();
        if(windowEvent.getID() != WindowEvent.WINDOW_OPENED) return;
        if(forgeWindowHandled && window instanceof JDialog) { // expecting a new dialog
            handleDialog(window);
            return;
        }
        if(!forgeWindowHandled) { // false at startup, so we will handle the first window as the Forge one
            forgeWindowHandled =  handleMainWindow(window);
            checkComponentTimer();
        }
    }

    public void checkComponentTimer() {
        if(forgeWindowHandled) {
            componentTimer.cancel();
            componentTimer.purge();
            return;
        }
        componentTimer.schedule(new ComponentTimeoutTask(), 30000);

    }

    public boolean handleMainWindow(Window window) {
        List<Component> components = new ArrayList<>();
        insertAllComponents(components, window, new MainWindowFilter());
        AbstractButton okButton = null;
        for(Component component : components) {
            if(component instanceof AbstractButton) {
                AbstractButton abstractButton = (AbstractButton) component;
                abstractButton = optiFineInstallation ?
                        handleOptiFineButton(abstractButton) :
                        handleForgeButton(abstractButton);
                if(abstractButton != null) okButton = abstractButton;
            }
        }
        if(okButton == null) {
            System.out.println("Failed to set all the UI components, wil try again in the next window");
            return false;
        }else{
            ProfileFixer.storeProfile(optiFineInstallation ? "OptiFine" : "forge");
            EventQueue.invokeLater(okButton::doClick); // do that after forge actually builds its window, otherwise we set the path too fast
            return true;
        }
    }


    public AbstractButton handleForgeButton(AbstractButton abstractButton) {
        switch(abstractButton.getText()) {
            case "OK":
                return  abstractButton; // return the button, so we can press it after processing other stuff
            case "Install client":
                abstractButton.doClick(); // It should be the default, but let's make sure
        }
        return null;
    }

    public AbstractButton handleOptiFineButton(AbstractButton abstractButton) {
        if ("Install".equals(abstractButton.getText())) {
            return abstractButton;
        }
        return null;
    }

    public void handleDialog(Window window) {
        List<Component> components = new ArrayList<>();
        insertAllComponents(components, window, new DialogFilter()); // ensure that it's a JOptionPane dialog
        if(components.size() == 1) {
            // another common trait of them - they only have one option pane in them,
            // so we can discard the rest of the dialog structure
            // also allows us to discard dialogs with progress bars which older installers use
            JOptionPane optionPane = (JOptionPane) components.get(0);
            if(optionPane.getMessageType() == JOptionPane.INFORMATION_MESSAGE) { // forge doesn't emit information messages for other reasons yet
                System.out.println("The install was successful!");
                ProfileFixer.reinsertProfile(optiFineInstallation ? "OptiFine" : "forge", suppressProfileCreation);
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
        boolean noProfileSuppression = false;
        boolean optifine = false;
        if(args != null ) {
            noProfileSuppression = args.contains("NPS"); // No Profile Suppression
            optifine = args.contains("OF"); // OptiFine
        }
        Agent agent = new Agent(noProfileSuppression, optifine);
        Toolkit.getDefaultToolkit()
                .addAWTEventListener(agent,
                        AWTEvent.WINDOW_EVENT_MASK);
    }
}
