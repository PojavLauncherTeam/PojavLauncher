package java.awt.mod;

import android.graphics.*;
import android.util.*;
import java.awt.image.*;
import java.lang.reflect.*;
import java.util.*;
import net.kdt.pojavlaunch.*;

public class ModdingKit
{
	public static MainActivity getCurrentActivity()
	{
		try {
			Class activityThreadClass = Class.forName("android.app.ActivityThread");
			Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null);
			Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
			activitiesField.setAccessible(true);

			Map<Object, Object> activities = (Map<Object, Object>) activitiesField.get(activityThread);
			if (activities == null) return null;

			for (Object activityRecord : activities.values()) {
				Class activityRecordClass = activityRecord.getClass();
				Field pausedField = activityRecordClass.getDeclaredField("paused");
				pausedField.setAccessible(true);
				if (!pausedField.getBoolean(activityRecord)) {
					Field activityField = activityRecordClass.getDeclaredField("activity");
					activityField.setAccessible(true);
					MainActivity activity = (MainActivity) activityField.get(activityRecord);
					return activity;
				}
			}
			return null;
		} catch (Throwable th) {
			return null;
		}
	}
	
	public static Bitmap bufferToBitmap(BufferedImage bufferedImage) {
        BufferedImage bufferedImage2 = bufferedImage;
        return pixelsToBitmap(((DataBufferInt) bufferedImage2.getRaster().getDataBuffer()).getData(), bufferedImage2.getWidth(), bufferedImage2.getHeight());
    }

    public static Bitmap pixelsToBitmap(int[] iArr, int i, int i2) {
        int[] iArr2 = iArr;
        int i3 = i;
        int i4 = i2;
        Bitmap createBitmap = Bitmap.createBitmap(i3, i4, Bitmap.Config.RGB_565);
        createBitmap.setPixels(iArr2, 0, i3, 0, 0, i3, i4);
        return createBitmap;
    }
}
