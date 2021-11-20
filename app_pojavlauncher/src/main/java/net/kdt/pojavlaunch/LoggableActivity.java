package net.kdt.pojavlaunch;

import java.util.Map;

public abstract class LoggableActivity extends BaseActivity {
    // TODO WE STILL HAVE TO MOVE THIS SHIT ELSEWHERE
    // Who the fuck thought it was a good idea to put the runtimelist inside a log activity :drunkdev:
    public Map<String, String> jreReleaseList;
}