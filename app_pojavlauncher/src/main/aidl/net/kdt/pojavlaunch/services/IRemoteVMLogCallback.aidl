// IRemoteVMLogCallback.aidl
package net.kdt.pojavlaunch.services;

// Declare any non-default types here with import statements

interface IRemoteVMLogCallback {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void getLogLine(String line);
}