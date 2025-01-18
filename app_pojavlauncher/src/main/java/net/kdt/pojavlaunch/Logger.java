package net.kdt.pojavlaunch;

import androidx.annotation.Keep;

/** Singleton class made to log on one file
 * The singleton part can be removed but will require more implementation from the end-dev
 */
@Keep
public class Logger {
    /** Print the text to the log file if not censored */
    public static native void appendToLog(String text);


    /** Reset the log file, effectively erasing any previous logs */
    public static native void begin(String logFilePath);

    /** Small listener for anything listening to the log */
    @Keep
    public interface eventLogListener {
        void onEventLogged(String text);
    }

    /** Link a log listener to the logger */
    public static native void setLogListener(eventLogListener logListener);
}
