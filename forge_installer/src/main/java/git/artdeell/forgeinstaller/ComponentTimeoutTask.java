package git.artdeell.forgeinstaller;

import java.util.TimerTask;

public class ComponentTimeoutTask extends TimerTask {
    @Override
    public void run() {
        System.out.println("Initialization timed out!");
        System.exit(17);
    }
}
