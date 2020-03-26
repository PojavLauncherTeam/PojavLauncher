package java.lang.management;

import java.util.List;

public interface RuntimeMXBean {
    List<String> getInputArguments();
	long getStartTime();
}

