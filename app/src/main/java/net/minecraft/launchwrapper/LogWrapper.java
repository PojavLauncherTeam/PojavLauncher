package net.minecraft.launchwrapper;

import java.util.logging.*;
import net.minecraft.launchwrapper.LogWrapper.*;
import java.text.*;
import java.util.*;
import android.util.*;

public class LogWrapper {
    private static boolean configured;
    public static LogWrapper log = new LogWrapper();
	
    private Logger myLog;
	private SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");
	
	// Make Android compatible with java logger
	private LogWrapper.AndroidLogger androidLogger;

    private static void configureLogging() {
        log.myLog = Logger.getLogger("LaunchWrapper");
        configured = true;
    }
	
    public static void retarget(Logger to) {
        log.myLog = to;
    }

    public static void log(String logChannel, Level level, String format, Object... data) {
        makeLog(logChannel);
		String logData = String.format(format, data);
        Logger.getLogger(logChannel).log(level, logData);
		androidLog(logChannel, level.getName(), logData);
    }

    public static void log(Level level, String format, Object... data) {
        if (!configured) {
            configureLogging();
        }
		String logData = String.format(format, data);
        log.myLog.log(level, logData);
		androidLog("LaunchWrapper", level.getName(), logData);
    }

    public static void log(String logChannel, Level level, Throwable ex, String format, Object... data) {
        makeLog(logChannel);
		String logData = String.format(format, data);
        Logger.getLogger(logChannel).log(level, logData, ex);
		androidLog(logChannel, level.getName(), logData + "\n" + Log.getStackTraceString(ex));
    }

    public static void log(Level level, Throwable ex, String format, Object... data) {
        if (!configured) {
            configureLogging();
        }
		String logData = String.format(format, data);
        log.myLog.log(level, logData, ex);
		androidLog("LaunchWrapper", level.getName(), logData + "\n" + Log.getStackTraceString(ex));
    }

    public static void severe(String format, Object... data) {
        log(Level.SEVERE, format, data);
    }

    public static void warning(String format, Object... data) {
        log(Level.WARNING, format, data);
    }

    public static void info(String format, Object... data) {
        log(Level.INFO, format, data);
    }

    public static void fine(String format, Object... data) {
        log(Level.FINE, format, data);
    }

    public static void finer(String format, Object... data) {
        log(Level.FINER, format, data);
    }

    public static void finest(String format, Object... data) {
        log(Level.FINEST, format, data);
    }

    public static void makeLog(String logChannel) {
        Logger.getLogger(logChannel).setParent(log.myLog);
    }
	
	// Android compatible add:
	private static void androidLog(String name, String level, String data) {
		if (log.androidLogger != null) {
			String timeStr = log.time.format(new Date());
			log.androidLogger.onPrint(timeStr, name, level, data);
		}
		// Log.d("[" + level + "] " + name, data);
	}
	
	public static void setAndroidLogReceiver(AndroidLogger logger) {
		log.androidLogger = logger;
	}
	
	public static interface AndroidLogger {
		public void onPrint(String time, String name, String level, String message);
	}
}

