package net.minecraft.launchwrapper.injector;

import java.applet.Applet;
import java.applet.AppletStub;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JPanel;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;

public class AlphaVanillaTweakInjector implements IClassTransformer {

    private static class LauncherFake extends Applet implements AppletStub {
        final /* synthetic */ Map map;

        public LauncherFake(Map map) {
            this.map = map;
        }

        public void appletResize(int width, int height) {
        }

        public boolean isActive() {
            return true;
        }

        public URL getDocumentBase() {
            try {
                return new URL("http://www.minecraft.net/game/");
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }
        }

        public URL getCodeBase() {
            try {
                return new URL("http://www.minecraft.net/game/");
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }
        }

        public String getParameter(String paramName) {
            if (this.map.containsKey(paramName)) {
                return (String) this.map.get(paramName);
            }
            System.err.println("Client asked for parameter: " + paramName);
            return null;
        }
    }

    public byte[] transform(String name, String transformedName, byte[] bytes) {
        return bytes;
    }

    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class clazz;
        try {
            clazz = getaClass("net.minecraft.client.MinecraftApplet");
        } catch (ClassNotFoundException e) {
            clazz = getaClass("com.mojang.minecraft.MinecraftApplet");
        }
        System.out.println("AlphaVanillaTweakInjector.class.getClassLoader() = " + AlphaVanillaTweakInjector.class.getClassLoader());
        Object object = clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
        for (Field field : clazz.getDeclaredFields()) {
            String name = field.getType().getName();
            if (!(name.contains("awt") || name.contains("java") || name.equals("long"))) {
                System.out.println("Found likely Minecraft candidate: " + field);
                Field fileField = getWorkingDirField(name);
                if (fileField != null) {
                    System.out.println("Found File, changing to " + Launch.minecraftHome);
                    fileField.setAccessible(true);
                    fileField.set(null, Launch.minecraftHome);
                    break;
                }
            }
        }
        startMinecraft((Applet) object, args);
    }

    private static void startMinecraft(final Applet applet, String[] args) {
        Map<String, String> params = new HashMap<String, String>();
        String name = "Player" + (System.currentTimeMillis() % 1000);
        if (args.length > 0) {
            name = args[0];
        }
        String sessionId = "-";
        if (args.length > 1) {
            sessionId = args[1];
        }
        params.put("username", name);
        params.put("sessionid", sessionId);
        Frame launcherFrameFake = new Frame();
        launcherFrameFake.setTitle("Minecraft");
        launcherFrameFake.setBackground(Color.BLACK);
        JPanel panel = new JPanel();
        launcherFrameFake.setLayout(new BorderLayout());
        panel.setPreferredSize(new Dimension(854, 480));
        launcherFrameFake.add(panel, "Center");
        launcherFrameFake.pack();
        launcherFrameFake.setLocationRelativeTo(null);
        launcherFrameFake.setVisible(true);
        launcherFrameFake.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					System.exit(1);
				}
			});
			
        LauncherFake fakeLauncher = new LauncherFake(params);
        applet.setStub(fakeLauncher);
        fakeLauncher.setLayout(new BorderLayout());
        fakeLauncher.add(applet, "Center");
        fakeLauncher.validate();
        launcherFrameFake.removeAll();
        launcherFrameFake.setLayout(new BorderLayout());
        launcherFrameFake.add(fakeLauncher, "Center");
        launcherFrameFake.validate();
		
        applet.init();
        applet.start();
        Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					applet.stop();
				}
			});
        VanillaTweakInjector.loadIconsOnFrames();
    }

    private static Class<?> getaClass(String name) throws ClassNotFoundException {
        return Launch.classLoader.loadClass(name);
    }

    private static Field getWorkingDirField(String name) throws ClassNotFoundException {
        for (Field field : getaClass(name).getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) && field.getType().getName().equals("java.io.File")) {
                return field;
            }
        }
        return null;
    }
}

