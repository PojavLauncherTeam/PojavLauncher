// IRemoteVMProcess.aidl
package net.kdt.pojavlaunch.services;

// Declare any non-default types here with import statements
import net.kdt.pojavlaunch.services.IRemoteVMLogCallback;

interface IRemoteVMProcess {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void runJVM(IRemoteVMLogCallback logCallback, in String[] commandLine);
}