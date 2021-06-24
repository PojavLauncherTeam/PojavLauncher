package net.kdt.pojavlaunch;

import java.util.Map;

public abstract class LoggableActivity extends BaseActivity {
    public Map<String, String> jreReleaseList;
    private boolean filteredSessionID = false;
    public void appendToLog(String text) {
        appendToLog(text, true);
    }
    
    public void appendlnToLog(String text) {
        // Filter out Session ID here
        int index;
        if (!filteredSessionID && (index = text.indexOf("(Session ID is ")) != -1) {
            text = text.substring(0, index) + "(Session ID is <censored>)";
            filteredSessionID = true;
        }

        appendlnToLog(text, true);
    }
    
    public void appendlnToLog(String text, boolean checkAllow) {
        appendToLog(text + "\n", checkAllow);
    }
    public abstract void appendToLog(final String text, boolean checkAllow);
}
