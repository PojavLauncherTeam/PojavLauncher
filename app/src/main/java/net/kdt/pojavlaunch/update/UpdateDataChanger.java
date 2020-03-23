package net.kdt.pojavlaunch.update;
import java.lang.reflect.*;
import net.kdt.pojavlaunch.*;
import java.io.*;

public class UpdateDataChanger
{
	/*
	 * Change the data from old version to this version
	 * For users update from:
	 * 2.4 --> 2.4.2
	 *
	 * /storage/sdcard0/AppProjects/PojavLauncher-2.4.2-20200323-preview4.apk
	 */

	@UpdateTarget(from="2.4", to="2.4.2")
	public static void changeData() {
		Tools.deleteRecursive(new File(Tools.oldLibrariesDir));
		Tools.deleteRecursive(new File(Tools.oldVersionDir));
		try {
			Tools.moveInside(Tools.oldGameDir, Tools.MAIN_PATH);
		} catch (Throwable th) {
			th.printStackTrace();
		}
	}
	
	public static void changeDataAuto(String from, String to) {
		try {
			/*
			for (Method method : UpdateDataChanger.class.getDeclaredMethods()) {
				if (method.getName().equals("changeData")) {
					UpdateTarget annotation = method.getAnnotation(UpdateTarget.class);
					if (annotation.from().equals(from) && annotation.to().equals(to)) {
						method.invoke(null);
					}
				}
			}
			*/
			
			changeData();
		} catch (Throwable th) {
			throw new RuntimeException("Unable to migrate to new data type", th);
		}
	}
}
