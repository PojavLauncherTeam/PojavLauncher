package net.kdt.pojavlaunch;

import java.util.Map;

public abstract class LoggableActivity extends BaseActivity {
    public Map<String, String> jreReleaseList;
    public void appendToLog(String text) {
        appendToLog(text, true);
    }
    
    public void appendlnToLog(String text) {
        appendlnToLog(text, true);
    }
    
    public void appendlnToLog(String text, boolean checkAllow) {
        appendToLog(text + "\n", checkAllow);
    }
    public abstract void appendToLog(final String text, boolean checkAllow);
}
