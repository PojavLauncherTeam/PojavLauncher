package net.kdt.pojavlaunch.patcher;

import android.util.*;
import java.io.*;
import java.math.*;
import java.security.*;
import java.util.*;
import java.util.jar.*;
import net.kdt.pojavlaunch.*;

public class OptiFinePatcher
{
	private File input;
	private JarFile inputFile;
	private Enumeration<? extends JarEntry> inputEntries;
	
	public OptiFinePatcher(File input) throws IOException {
		this.input = input;
		inputFile = new JarFile(input);
		inputEntries = inputFile.entries();
	}
	
	public String[] saveInstaller(File patchDir) throws Exception {
		String md5File = Tools.calculateMD5(input);
		File optifineJar = new File(patchDir, md5File + "_OptiFine_patched.jar");
		BufferedOutputStream optifineBuf = new BufferedOutputStream(new FileOutputStream(optifineJar));
		JarOutputStream optifineJarStream = new JarOutputStream(optifineBuf);

		while (inputEntries.hasMoreElements()) {
			JarEntry inEntry = inputEntries.nextElement();
			if (!inEntry.getName().equals("optifine/OptiFineClassTransformer.class")) {
				optifineJarStream.putNextEntry(inEntry);
				optifineJarStream.write(Tools.getByteArray(inputFile.getInputStream(inEntry)));
				optifineJarStream.closeEntry();
			}
		}
		
		for (File patchClass : new File(patchDir, "optifine_patch").listFiles()) {
			if (patchClass.isFile()) {
				byte[] bArr = Tools.getByteArray(patchClass.getAbsolutePath());
				String patchName = patchClass.getName();
				JarEntry entry = new JarEntry("optifine/" + patchName.replace(".class.patch", ".class"));
				entry.setSize(bArr.length);
				optifineJarStream.putNextEntry(entry);
				optifineJarStream.write(bArr);
				optifineJarStream.closeEntry();
			}
		}

		optifineJarStream.finish();
		optifineJarStream.flush();
		optifineBuf.flush();
		optifineBuf.close();
		
		return new String[]{md5File, optifineJar.getAbsolutePath()};
	}

	public void saveTweaker() throws Exception {
		File patchedFile = new File(input.getAbsolutePath().replace(".jar", "_patched.jar"));
		BufferedOutputStream optifineBuf = new BufferedOutputStream(new FileOutputStream(patchedFile));
		JarOutputStream optifineJarStream = new JarOutputStream(optifineBuf);

		while (inputEntries.hasMoreElements()) {
			JarEntry inEntry = inputEntries.nextElement();
			if (!inEntry.getName().startsWith("classes") && !inEntry.getName().endsWith(".dex")) {
				optifineJarStream.putNextEntry(inEntry);
				optifineJarStream.write(Tools.getByteArray(inputFile.getInputStream(inEntry)));
				optifineJarStream.closeEntry();
			}
		}

		optifineJarStream.finish();
		optifineJarStream.flush();
		optifineBuf.flush();
		optifineBuf.close();
		
		input.delete();
		patchedFile.renameTo(input);
	}
}
