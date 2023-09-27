package net.kdt.pojavlaunch.multirt;

import java.util.Objects;

public class Runtime {
    public final String name;
    public final String versionString;
    public final String arch;
    public final int javaVersion;
    public Runtime(String name) {
        this.name = name;
        this.versionString = null;
        this.arch = null;
        this.javaVersion = 0;
    }
    Runtime(String name, String versionString, String arch, int javaVersion) {
        this.name = name;
        this.versionString = versionString;
        this.arch = arch;
        this.javaVersion = javaVersion;
    }
    


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Runtime runtime = (Runtime) o;
        return name.equals(runtime.name);
    }
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}