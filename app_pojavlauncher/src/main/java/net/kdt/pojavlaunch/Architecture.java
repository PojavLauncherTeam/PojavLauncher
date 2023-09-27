package net.kdt.pojavlaunch;

import android.os.Build;

/**
 * This class aims at providing a simple and easy way to deal with the device architecture.
 */
public class Architecture {
	public static final int UNSUPPORTED_ARCH = -1;
	public static final int ARCH_ARM64 = 0x1;
	public static final int ARCH_ARM = 0x2;
	public static final int ARCH_X86 = 0x4;
	public static final int ARCH_X86_64 = 0x8;

	/**
	 * Tell us if the device supports 64 bits architecture
	 * @return If the device supports 64 bits architecture
	 */
	public static boolean is64BitsDevice(){
		return Build.SUPPORTED_64_BIT_ABIS.length != 0;
	}

	/**
	 * Tell us if the device supports 32 bits architecture
	 * Note, that a 64 bits device won't be reported as supporting 32 bits.
	 * @return If the device supports 32 bits architecture
	 */
	public static boolean is32BitsDevice(){
		return !is64BitsDevice();
	}

	/**
	 * Tells the device supported architecture.
	 * Since mips(/64) has been phased out long ago, is isn't checked here.
	 *
	 * @return ARCH_ARM || ARCH_ARM64 || ARCH_X86 || ARCH_86_64
	 */
	public static int getDeviceArchitecture(){
		if(isx86Device()){
			return is64BitsDevice() ? ARCH_X86_64 : ARCH_X86;
		}
		return is64BitsDevice() ? ARCH_ARM64 : ARCH_ARM;
	}

	/**
	 * Tell is the device is based on an x86 processor.
	 * It doesn't tell if the device is 64 or 32 bits.
	 * @return Whether or not the device is x86 based.
	 */
	public static boolean isx86Device(){
		//We check the whole range of supported ABIs,
		//Since asus zenfones can place arm before their native instruction set.
		String[] ABI = is64BitsDevice() ? Build.SUPPORTED_64_BIT_ABIS : Build.SUPPORTED_32_BIT_ABIS;
		int comparedArch = is64BitsDevice() ? ARCH_X86_64 : ARCH_X86;
		for (String str : ABI) {
			if (archAsInt(str) == comparedArch) return true;
		}
		return false;
	}



	/**
	 * Convert an architecture from a String to an int.
	 * @param arch The architecture as a String
	 * @return The architecture as an int, can be UNSUPPORTED_ARCH if unknown.
	 */
	public static int archAsInt(String arch){
		arch = arch.toLowerCase().trim().replace(" ", "");
		if(arch.contains("arm64") || arch.equals("aarch64")) return ARCH_ARM64;
		if(arch.contains("arm") || arch.equals("aarch32")) return ARCH_ARM;
		if(arch.contains("x86_64") || arch.contains("amd64")) return ARCH_X86_64;
		if(arch.contains("x86") || (arch.startsWith("i") && arch.endsWith("86"))) return ARCH_X86;
		//Shouldn't happen
		return UNSUPPORTED_ARCH;
	}

	/**
	 * Convert to a string an architecture.
	 * @param arch The architecture as an int.
	 * @return "arm64" || "arm" || "x86_64" || "x86" || "UNSUPPORTED_ARCH"
	 */
	public static String archAsString(int arch){
		if(arch == ARCH_ARM64) return "arm64";
		if(arch == ARCH_ARM) return "arm";
		if(arch == ARCH_X86_64) return "x86_64";
		if(arch == ARCH_X86) return "x86";
		return "UNSUPPORTED_ARCH";
	}

}
