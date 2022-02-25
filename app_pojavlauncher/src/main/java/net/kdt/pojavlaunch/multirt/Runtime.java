package net.kdt.pojavlaunch.multirt;

import java.util.Objects;

public class Runtime {
    public Runtime(String name) {
        this.name = name;
    }
    
    public String name;
    public String versionString;
    public String arch;
    public int javaVersion;

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