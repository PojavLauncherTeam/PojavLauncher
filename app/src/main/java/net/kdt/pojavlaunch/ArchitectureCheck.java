package net.kdt.pojavlaunch;

public class ArchitectureCheck
{
	public static String getArch() {
		String arch = System.getProperty("os.arch");
		String archConverted = null;
		
		if (arch.equals("aarch64") ||
		  	arch.endsWith("v8a") ||
		  	arch.startsWith("arm64")) {
			archConverted = "arm64";
	 	} else if (arch.startsWith("arm") || arch.endsWith("v7a")) {
			archConverted = "arm32";
		} else if (arch.startsWith("x86") || arch.startsWith("amd")) {
			archConverted = "x86";
		}
		
		return archConverted;
	}
}
