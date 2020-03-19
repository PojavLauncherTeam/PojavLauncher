package net.kdt.pojavlaunch;
import android.content.*;

public class PojavV2ActivityManager
{
	public static String CATEGORY_LAUNCHER = "launcher";
	public static Class<?> LAUNCHER_V1 = MCLauncherActivity.class;
	public static Class<?> LAUNCHER_V2 = LAUNCHER_V1; // PojavLauncherActivity.class;

	public static boolean setLauncherRemakeClass(Context context, Class<?> cls) {
		return setLauncherRemakeVer(context, cls.getName().equals(LAUNCHER_V1.getName()) ? 0 : 1);
	}

    public static boolean setLauncherRemakeVer(Context context, int i) {
		Context context2 = context;
		int i2 = i;
        if (i2 >= 0 && i2 <= 1) {
			return getPref(context2).edit().putInt(CATEGORY_LAUNCHER, i2).commit();
		} else throw new IllegalArgumentException("ver must be 0 or 1");
	}

	public static int getLauncherRemakeInt(Context context) {
		if (Tools.enableDevFeatures) {
			return getPref(context).getInt(CATEGORY_LAUNCHER, 0);
		} else {
			return 0;
		}
	}

	public static Class<?> getLauncherRemakeVer(Context context) {
		return getLauncherRemakeInt(context) == 0 ? LAUNCHER_V1 : LAUNCHER_V2;
	}

	private static SharedPreferences getPref(Context ctx) {
		return ctx.getSharedPreferences("remake", Context.MODE_PRIVATE);
	}
}
