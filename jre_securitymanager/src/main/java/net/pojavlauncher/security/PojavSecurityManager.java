package net.pojavlauncher.security;
import java.security.*;

public class PojavSecurityManager extends SecurityManager
{
    @Override
    public void checkPermission(Permission perm, Object obj) {
        super.checkPermission(perm, obj);
    }
}
