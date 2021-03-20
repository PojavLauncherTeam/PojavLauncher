package net.kdt.pojavlaunch;


public abstract class LoggableActivity extends BaseActivity {
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
