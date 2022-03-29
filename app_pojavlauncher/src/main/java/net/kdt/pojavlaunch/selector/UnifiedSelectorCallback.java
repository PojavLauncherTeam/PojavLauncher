package net.kdt.pojavlaunch.selector;

import java.io.InputStream;

public interface UnifiedSelectorCallback {
    void onSelected(InputStream stream, String name);
    void onError(Throwable th);
}
