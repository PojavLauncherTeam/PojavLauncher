package net.kdt.pojavlaunch;

import android.support.v7.app.*;

public abstract class LoggableActivity extends AppCompatActivity {
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
