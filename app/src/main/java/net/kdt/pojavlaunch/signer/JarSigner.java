package net.kdt.pojavlaunch.signer;

import android.util.*;
import java.io.*;
import java.util.*;
import java.util.jar.*;
import java.util.zip.*;
import net.kdt.pojavlaunch.*;
//import org.apache.commons.codec.digest.*;
import net.kdt.pojavlaunch.util.*;

public class JarSigner
{
	private static final String DEX_IN_JAR_NAME = "classes.dex";
	private static final String MANIFEST_PATH = "META-INF/MANIFEST.MF";

	private TreeMap<String, byte[]> outputResources = new TreeMap<String, byte[]>();

	public static void sign(String inputJar, String outputJar) throws Exception
	{
		new JarSigner(inputJar, outputJar);
	}
	private JarSigner(String inputJar, String outputJar) throws Exception
	{
		ZipFile jarFile = new ZipFile(inputJar);
		Enumeration<? extends ZipEntry> entries = jarFile.entries();

		while (entries.hasMoreElements()) {
			ZipEntry entry = entries.nextElement();
			outputResources.put(entry.getName(), Tools.getByteArray(jarFile.getInputStream(entry)));
		}
		createJar(outputJar);
	}
	private byte[] makeManifest() {
        StringBuilder baos = new StringBuilder();

		// First, put some general information:
		baos.append("Manifest-Version: 1.0\n");
        baos.append("Created-By: " + Tools.usingVerName + " (" + Tools.APP_NAME + ": JarSigner)\n");
		baos.append("Build-Jdk: 1.6.0_29");
        baos.append("Dex-Location: " + DEX_IN_JAR_NAME + "\n");

        return baos.toString().getBytes();
    }
	private boolean createJar(String fileName) {
        /*
         * Make or modify the manifest (as appropriate), put the dex
         * array into the resources map, and then process the entire
         * resources map in a uniform manner.
         */

        try {
            byte[] manifest = makeManifest();
            OutputStream out = new FileOutputStream(fileName);
            JarOutputStream jarOut = new JarOutputStream(out);

            try {
                for (Map.Entry<String, byte[]> e : outputResources.entrySet()) {
                    String name = e.getKey();
                    byte[] contents = e.getValue();
                    JarEntry entry = new JarEntry(name);
                    int length = contents.length;
					entry.setSize(length);
					/*
					 if (args.verbose) {
					 context.out.println("writing " + name + "; size " + length + "...");
					 }
					 */
					if (name.endsWith(".SF") ||
						name.endsWith(".RSA")) {
						// Remove these files.
						continue;
					} else if (name.endsWith(MANIFEST_PATH)) {
						length = manifest.length;
						jarOut.putNextEntry(entry);
						jarOut.write(manifest);
						jarOut.closeEntry();
					} else {
						jarOut.putNextEntry(entry);
						jarOut.write(contents);
						jarOut.closeEntry();
					}

                }
            } finally {
                jarOut.finish();
                jarOut.flush();
                closeOutput(out);
            }
        } catch (Exception ex) {
			throw new RuntimeException("Trouble writing output:", ex);
        }

        return true;
    }
	private void closeOutput(OutputStream stream) throws IOException {
        if (stream == null) {
            return;
        }

        stream.flush();

        if (stream != System.out) {
            stream.close();
        }
    }
}
