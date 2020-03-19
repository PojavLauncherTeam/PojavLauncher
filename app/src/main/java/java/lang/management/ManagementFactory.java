package java.lang.management;

import java.util.Collections;
import java.util.List;

public class ManagementFactory {
    private static RuntimeMXBean runtimeMXBeanInstance = new RuntimeMXBeanImpl();

    private static class RuntimeMXBeanImpl implements RuntimeMXBean {
        private RuntimeMXBeanImpl() {
        }

        public List<String> getInputArguments() {
            return Collections.emptyList();
        }
    }

    public static RuntimeMXBean getRuntimeMXBean() {
        return runtimeMXBeanInstance;
    }
}

