package net.kdt.pojavlaunch;

import android.os.Build;

import java.util.Locale;


public class Architecture
{
	public static int ARCH_ARM64 = 0x1;
	public static int ARCH_ARM = 0x2;
	public static int ARCH_X86 = 0x4;
	public static int ARCH_X86_64 = 0x8;

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
	 * Tells the device supported architecture
	 * As of now, the function may just fail to detect ARM emulation from zenfones
	 *
	 * @return ARCH_ARM || ARCH_ARM64 || ARCH_X86 || ARCH_86_64
	 */
	public static int getDeviceArchitecture(){
		if(is64BitsDevice()){
			return archAsInt(Build.SUPPORTED_64_BIT_ABIS[0]);
		}
		return archAsInt(Build.SUPPORTED_32_BIT_ABIS[0]);
	}

	/**
	 * Convert an architecture from a String to an int.
	 * @param arch The architecture as a String
	 * @return The architecture as an int.
	 */
	public static int archAsInt(String arch){
		arch = arch.toLowerCase();
		if(arch.equals("arm64-v8a") || arch.equals("aarch64")) return ARCH_ARM64;
		if(arch.contains("armeabi")) return ARCH_ARM;
		if(arch.equals("x86_64")) return ARCH_X86_64;
		if(arch.equals("x86") || (arch.startsWith("i") && arch.endsWith("86"))) return ARCH_X86;
		//Shouldn't happen
		return -1;
	}

}
