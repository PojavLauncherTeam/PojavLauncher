package java.lang.management;

import java.util.Collections;
import java.util.List;
// import javax.management.*;
import java.security.*;

public class ManagementFactory {
    private static RuntimeMXBean runtimeMXBeanInstance = new RuntimeMXBeanImpl();

	// private static MBeanServer platformMBeanServer;

    private static class RuntimeMXBeanImpl implements RuntimeMXBean {
		private long startTime = -1;
        private RuntimeMXBeanImpl() {
        }

		@Override
        public List<String> getInputArguments() {
            return Collections.emptyList();
        }
		
		@Override
		public long getStartTime() {
			if (startTime == -1) {
				startTime = System.currentTimeMillis();
			}
			return startTime;
		}
    }
/*
	public static synchronized MBeanServer getPlatformMBeanServer() {
        if (platformMBeanServer == null) {
            platformMBeanServer = new MBeanServer();
        }
        return platformMBeanServer;
    }
*/
    public static RuntimeMXBean getRuntimeMXBean() {
        return runtimeMXBeanInstance;
    }
}

